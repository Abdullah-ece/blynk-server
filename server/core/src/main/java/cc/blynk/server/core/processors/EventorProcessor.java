package cc.blynk.server.core.processors;

import cc.blynk.server.core.dao.NotificationsDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.others.eventor.EventorRule;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.BaseAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPropertyPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.notification.NotificationAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.notification.NotifyAction;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.utils.NumberUtil;

import static cc.blynk.server.core.protocol.enums.Command.EVENTOR;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.utils.StringUtils.PIN_PATTERN;

/**
 * Class responsible for handling eventor logic.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.16.
 */
public class EventorProcessor {

    private final NotificationsDao notificationsDao;
    private final GlobalStats globalStats;

    public EventorProcessor(NotificationsDao notificationsDao, GlobalStats stats) {
        this.notificationsDao = notificationsDao;
        this.globalStats = stats;
    }

    private void execute(User user, Device device,
                         String triggerValue, NotificationAction notificationAction) {
        String body = PIN_PATTERN.matcher(notificationAction.message).replaceAll(triggerValue);
        if (notificationAction instanceof NotifyAction) {
            notificationsDao.push(user, body, device.id);
        }
    }

    public void process(User user, Session session, DashBoard dash, Device device, short pin,
                        PinType type, String triggerValue) {
        Eventor eventor = dash.getEventorWidget();
        if (eventor == null || eventor.rules == null
                || eventor.deviceId != device.id || !dash.isActive) {
            return;
        }

        double valueParsed = NumberUtil.parseDouble(triggerValue);

        for (EventorRule eventorRule : eventor.rules) {
            if (eventorRule.isReady(pin, type)) {
                if (eventorRule.matchesCondition(triggerValue, valueParsed)) {
                    if (!eventorRule.isProcessed) {
                        for (BaseAction action : eventorRule.actions) {
                            if (action.isValid()) {
                                if (action instanceof SetPinAction) {
                                    execute(session, device, (SetPinAction) action);
                                } else if (action instanceof SetPropertyPinAction) {
                                    execute(session, dash, device, (SetPropertyPinAction) action);
                                } else if (action instanceof NotificationAction) {
                                    execute(user, device, triggerValue, (NotificationAction) action);
                                }
                                globalStats.mark(EVENTOR);
                            }
                        }
                        eventorRule.isProcessed = true;
                    }
                } else {
                    eventorRule.isProcessed = false;
                }
            }
        }
    }

    private void execute(Session session,
                         Device device, SetPinAction action) {
        String body = action.makeHardwareBody();
        int deviceId = device.id;
        session.sendMessageToHardware(HARDWARE, 888, body, deviceId);
        session.sendToApps(HARDWARE, 888, body, deviceId);
        device.updateValue(action.dataStream.pin, action.dataStream.pinType, action.value);
    }

    private void execute(Session session, DashBoard dash,
                         Device device, SetPropertyPinAction action) {
        String body = action.makeHardwareBody();
        session.sendToApps(SET_WIDGET_PROPERTY, 888, body, device.id);

        Widget widget = dash.updateProperty(device.id, action.dataStream.pin, action.property, action.value);
        //this is possible case for device selector
        if (widget == null) {
            short pin = action.dataStream.pin;
            WidgetProperty property = action.property;
            device.updateValue(pin, property, action.value);
        }
    }
}
