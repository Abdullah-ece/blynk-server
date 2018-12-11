package cc.blynk.server.workers.timer;

import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.NotificationsDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.widgets.Target;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.controls.Timer;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.others.eventor.Rule;
import cc.blynk.server.core.model.widgets.others.eventor.TimerTime;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.BaseAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.notification.NotifyAction;
import cc.blynk.server.core.model.widgets.ui.DeviceSelector;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.Tile;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.utils.DateTimeUtils;
import cc.blynk.utils.IntArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_INTS;

/**
 * Timer worker class responsible for triggering all timers at specified time.
 * Current implementation is some kind of Hashed Wheel Timer.
 * In general idea is very simple :
 *
 * Select timers at specified cell timer[secondsOfDayNow]
 * and run it one by one, instead of naive implementation
 * with iteration over all profiles every second
 *
 * + Concurrency around it as timerWorker may be accessed from different threads.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/6/2015.
 *
 */
public class TimerWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(TimerWorker.class);
    public static final int TIMER_MSG_ID = 7777;

    private final UserDao userDao;
    private final DeviceDao deviceDao;
    private final SessionDao sessionDao;
    private final NotificationsDao notificationsDao;
    private final AtomicReferenceArray<ConcurrentHashMap<TimerKey, BaseAction[]>> timerExecutors;
    private final static int size = 86400;

    public TimerWorker(UserDao userDao, DeviceDao deviceDao, SessionDao sessionDao, NotificationsDao notificationsDao) {
        this.userDao = userDao;
        this.deviceDao = deviceDao;
        this.sessionDao = sessionDao;
        this.notificationsDao = notificationsDao;
        //array cell for every second in a day,
        //yes, it costs a bit of memory, but still cheap :)
        this.timerExecutors = new AtomicReferenceArray<>(size);
        init(userDao.users);
    }

    private void init(ConcurrentMap<String, User> users) {
        int counter = 0;
        for (Map.Entry<String, User> entry : users.entrySet()) {
            User user = entry.getValue();
            for (DashBoard dash : user.profile.dashBoards) {
                int dashId = dash.id;
                for (Widget widget : dash.widgets) {
                    if (widget instanceof DeviceTiles) {
                        DeviceTiles deviceTiles = (DeviceTiles) widget;
                        counter += add(user.orgId, user.email, deviceTiles, dashId);
                    } else if (widget instanceof Timer) {
                        Timer timer = (Timer) widget;
                        add(user.orgId, user.email, timer, dashId, -1, -1);
                        counter++;
                    } else if (widget instanceof Eventor) {
                        Eventor eventor = (Eventor) widget;
                        add(user.orgId, user.email, eventor, dashId);
                        counter++;
                    }
                }
            }
        }
        log.info("Timers : {}", counter);
    }

    public int add(int orgId, String userKey, DeviceTiles deviceTiles, int dashId) {
        int counter = 0;
        for (TileTemplate template : deviceTiles.templates) {
            for (Widget widgetInTemplate : template.widgets) {
                if (widgetInTemplate instanceof Timer) {
                    add(orgId, userKey, (Timer) widgetInTemplate, dashId, deviceTiles.id, template.id);
                    counter++;
                }
            }
        }
        return counter;
    }

    public void add(int orgId, String userKey, Eventor eventor, int dashId) {
        if (eventor.rules != null) {
            for (Rule rule : eventor.rules) {
                if (rule.isValidTimerRule()) {
                    add(orgId, userKey, dashId, eventor.deviceId, eventor.id,
                            rule.triggerTime.id, rule.triggerTime, rule.actions);
                }
            }
        }
    }

    public void add(int orgId, String userKey, Timer timer, int dashId, long deviceTilesId, long templateId) {
        if (timer.isValid()) {
            if (timer.isValidStart()) {
                TimerTime timerTime = new TimerTime(timer.startTime);
                SetPinAction action = new SetPinAction(timer.pin, timer.pinType, timer.startValue);
                TimerKey timerKey = new TimerKey(orgId, userKey, dashId, timer.deviceId, timer.id, 0,
                        deviceTilesId, templateId, timerTime);
                getExecutorOrCreate(timerTime.time).put(timerKey, new BaseAction[]{action});
            }
            if (timer.isValidStop()) {
                TimerTime timerTime = new TimerTime(timer.stopTime);
                SetPinAction action = new SetPinAction(timer.pin, timer.pinType, timer.stopValue);
                TimerKey timerKey = new TimerKey(orgId, userKey, dashId, timer.deviceId, timer.id, 1,
                        deviceTilesId, templateId, timerTime);
                getExecutorOrCreate(timerTime.time).put(timerKey, new BaseAction[]{action});
            }
        }
    }

    private void add(int orgId, String userKey, int dashId, int deviceId, long widgetId,
                     int additionalId, TimerTime time, BaseAction[] actions) {
        ArrayList<BaseAction> validActions = new ArrayList<>(actions.length);
        for (BaseAction action : actions) {
            if (action.isValid()) {
                validActions.add(action);
            }
        }
        if (!validActions.isEmpty()) {
            getExecutorOrCreate(time.time).put(
                    new TimerKey(orgId, userKey, dashId, deviceId, widgetId, additionalId,
                            -1L, -1L, time),
                    validActions.toArray(new BaseAction[0]));
        }
    }

    public void delete(int orgId, String userKey, Eventor eventor, int dashId) {
        if (eventor.rules != null) {
            for (Rule rule : eventor.rules) {
                if (rule.isValidTimerRule()) {
                    delete(orgId, userKey, dashId, eventor.deviceId,
                            eventor.id, rule.triggerTime.id, -1L, -1L, rule.triggerTime);
                }
            }
        }
    }

    public void delete(int orgId, String userKey, Timer timer, int dashId, long deviceTilesId, long templateId) {
        if (timer.isValidStart()) {
            delete(orgId, userKey, dashId, timer.deviceId, timer.id, 0,
                    deviceTilesId, templateId, new TimerTime(timer.startTime));
        }
        if (timer.isValidStop()) {
            delete(orgId, userKey, dashId, timer.deviceId, timer.id, 1,
                    deviceTilesId, templateId, new TimerTime(timer.stopTime));
        }
    }

    private void delete(int orgId, String userKey, int dashId, int deviceId, long widgetId, int additionalId,
                        long deviceTilesId, long templateId, TimerTime time) {
        ConcurrentHashMap<TimerKey, BaseAction[]> secondExecutor = timerExecutors.get(time.time);
        if (secondExecutor != null) {
            secondExecutor.remove(new TimerKey(orgId, userKey, dashId, deviceId,
                    widgetId, additionalId,
                    deviceTilesId, templateId, time));
        }
    }

    //may be improved in Java9 with compareAndExchange
    private ConcurrentHashMap<TimerKey, BaseAction[]> getExecutorOrCreate(int seconds) {
        ConcurrentHashMap<TimerKey, BaseAction[]> secondExecutor = timerExecutors.get(seconds);
        if (secondExecutor != null) {
            return secondExecutor;
        }
        ConcurrentHashMap<TimerKey, BaseAction[]> newSecondExecutorMap = new ConcurrentHashMap<>();
        if (timerExecutors.compareAndSet(seconds, null, newSecondExecutorMap)) {
            return newSecondExecutorMap;
        }
        return timerExecutors.get(seconds);
    }

    private int actuallySendTimers;
    private int activeTimers;

    @Override
    public void run() {
        log.trace("Starting timer...");

        long now = System.currentTimeMillis();
        ConcurrentMap<TimerKey, BaseAction[]> tickedExecutors = timerExecutors.get((int) ((now / 1000) % 86400));

        if (tickedExecutors == null) {
            return;
        }

        try {
            this.activeTimers = 0;
            this.actuallySendTimers = 0;
            send(tickedExecutors, now);
        } catch (Exception e) {
            log.error("Error running timers. ", e);
        }

        if (activeTimers > 0) {
            log.info("Timer finished. Ready {}, Active {}, Actual {}. Processing time : {} ms",
                    tickedExecutors.size(), activeTimers, actuallySendTimers, System.currentTimeMillis() - now);
        }
    }

    private void send(ConcurrentMap<TimerKey, BaseAction[]> tickedExecutors, long now) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(DateTimeUtils.UTC);

        for (Map.Entry<TimerKey, BaseAction[]> entry : tickedExecutors.entrySet()) {
            TimerKey key = entry.getKey();
            BaseAction[] actions = entry.getValue();
            if (key.time.isTickTime(currentDateTime)) {
                User user = userDao.users.get(key.userKey);
                if (user != null) {
                    DashBoard dash = user.profile.getDashById(key.dashId);
                    if (dash != null && dash.isActive) {
                        activeTimers++;
                        process(user.orgId, user, dash, key, actions, now);
                    }
                }
            }
        }
    }

    private void process(int orgId, User user, DashBoard dash, TimerKey key, BaseAction[] actions, long now) {
        for (BaseAction action : actions) {

            int[] deviceIds = EMPTY_INTS;
            if (key.isTilesTimer()) {
                Widget widget = dash.getWidgetById(key.deviceTilesId);
                if (widget instanceof DeviceTiles) {
                    IntArray intArray = new IntArray();
                    DeviceTiles deviceTiles = (DeviceTiles) widget;
                    for (Tile tile : deviceTiles.tiles) {
                        if (tile.templateId == key.templateId) {
                            intArray.add(tile.deviceId);
                        }
                    }
                    deviceIds = intArray.toArray();
                }
            } else {
                Target target;
                int targetId = key.deviceId;
                if (targetId < Tag.START_TAG_ID) {
                    target = deviceDao.getById(targetId);
                } else if (targetId < DeviceSelector.DEVICE_SELECTOR_STARTING_ID) {
                    target = user.profile.getTagById(targetId);
                } else {
                    //means widget assigned to device selector widget.
                    target = dash.getDeviceSelector(targetId);
                }
                if (target == null) {
                    return;
                }

                deviceIds = target.getDeviceIds();
            }

            if (deviceIds.length == 0) {
                return;
            }

            if (action instanceof SetPinAction) {
                SetPinAction setPinAction = (SetPinAction) action;
                for (int deviceId : deviceIds) {
                    Device device = deviceDao.getById(deviceId);
                    if (device != null) {
                        device.updateValue(setPinAction.dataStream, setPinAction.value, now);
                    }
                }

                triggerTimer(orgId, sessionDao, setPinAction.makeHardwareBody(), deviceIds);
            } else if (action instanceof NotifyAction) {
                NotifyAction notifyAction = (NotifyAction) action;
                for (int deviceId : deviceIds) {
                    notificationsDao.push(user, notifyAction.message, deviceId);
                }
            }
        }
    }

    private void triggerTimer(int orgId, SessionDao sessionDao, String value, int[] deviceIds) {
        Session session = sessionDao.getOrgSession(orgId);
        if (session != null) {
            if (!session.sendMessageToHardware(HARDWARE, TIMER_MSG_ID, value, deviceIds)) {
                actuallySendTimers++;
            }
            for (int deviceId : deviceIds) {
                session.sendToApps(HARDWARE, TIMER_MSG_ID, deviceId, value);
            }
        }
    }

    public void deleteTimers(int orgId, String userKey, DashBoard dash) {
        for (Widget widget : dash.widgets) {
            if (widget instanceof DeviceTiles) {
                DeviceTiles deviceTiles = (DeviceTiles) widget;
                deleteTimers(orgId, userKey, dash.id, deviceTiles);
            } else if (widget instanceof Timer) {
                delete(orgId, userKey, (Timer) widget, dash.id, -1L, -1L);
            } else if (widget instanceof Eventor) {
                delete(orgId, userKey, (Eventor) widget, dash.id);
            }
        }
    }

    private void deleteTimers(int orgId, String userKey, int dashId, DeviceTiles deviceTiles) {
        for (TileTemplate template : deviceTiles.templates) {
            for (Widget widgetInTemplate : template.widgets) {
                if (widgetInTemplate instanceof Timer) {
                    delete(orgId, userKey, (Timer) widgetInTemplate, dashId, deviceTiles.id, template.id);
                }
            }
        }
    }
}
