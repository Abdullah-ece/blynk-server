package cc.blynk.server.workers;

import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.GroupFunctionValue;
import cc.blynk.server.core.model.widgets.ui.tiles.group.Group;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.internal.StateHolderUtil;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_HARDWARE_GROUP;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static cc.blynk.utils.StringUtils.DEVICE_SEPARATOR;

/**
 * Purpose of this scheduler is to send batch realtime updates for groups in user profile.
 * This is done is order to avoid group updates in realtime for every device update.
 * Group can contain hundreds of devices and in that case we have to send hundreds
 * updates to the app. This is not necessary. We can do that once per 5 second.
 * This will decrease the load on the server, network and app UI. As for group we need to do
 * lookups for all devices within group.
 */
public final class GroupValueUpdaterWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(GroupValueUpdaterWorker.class);

    private final SessionDao sessionDao;
    private final OrganizationDao organizationDao;

    public GroupValueUpdaterWorker(SessionDao sessionDao, OrganizationDao organizationDao) {
        this.sessionDao = sessionDao;
        this.organizationDao = organizationDao;
    }

    private static String makeBody(int dashId, long widgetId, long groupId, char pintTypeChar, short pin, double val) {
        return "" + dashId + DEVICE_SEPARATOR + widgetId
                + DEVICE_SEPARATOR + groupId + BODY_SEPARATOR
                + pintTypeChar + "w" + BODY_SEPARATOR
                + pin + BODY_SEPARATOR + val;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        process();
        long diff = System.currentTimeMillis() - start;
        //if this worker is quick enough we are not interested in this info
        if (diff > 10) {
            log.info("Processing of group values took : {} ms.", diff);
        }
    }

    private void process() {
        //we are interested only in mobile apps that are currently online
        for (Session session : sessionDao.orgSession.values()) {
            if (!session.isAppConnected()) {
                continue;
            }

            for (Channel channel : session.appChannels) {
                MobileStateHolder state = StateHolderUtil.getMobileState(channel);
                if (state == null) {
                    continue;
                }

                for (DashBoard dashBoard : state.user.profile.dashBoards) {
                    //for now we expect only 1 DeviceTiles in the dashboard
                    DeviceTiles deviceTiles = dashBoard.getWidgetByType(DeviceTiles.class);
                    if (deviceTiles == null || deviceTiles.groups.length == 0) {
                        continue;
                    }

                    List<Device> devices = organizationDao.getDevices(state);
                    for (Group group : deviceTiles.groups) {
                        for (DataStream dataStream : group.viewDataStreams) {
                            if (dataStream.isValidForGroups()) {
                                GroupFunctionValue function = new GroupFunctionValue(group, dataStream);
                                for (Device device : devices) {
                                    for (var entry : device.pinStorage.values.entrySet()) {
                                        DeviceStorageKey key = entry.getKey();
                                        PinStorageValue value = entry.getValue();
                                        if (function.isSame(key, device.id)) {
                                            function.apply(value.lastValue());
                                        }
                                    }
                                }
                                double result = function.result();
                                String finalBody = makeBody(dashBoard.id, deviceTiles.id, group.id,
                                        dataStream.pinType.pintTypeChar, dataStream.pin, result);
                                channel.writeAndFlush(new StringMessage(1, MOBILE_HARDWARE_GROUP, finalBody));
                            }
                        }
                    }
                }

            }
        }
    }

}
