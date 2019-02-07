package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.group.Group;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_HARDWARE_GROUP;
import static cc.blynk.server.internal.WebByteBufUtil.deviceNotInNetwork;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split3;

/**
 * Responsible for handling incoming hardware commands from applications and forwarding it to
 * appropriate hardware.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileHardwareGroupLogic {

    private static final Logger log = LogManager.getLogger(MobileHardwareGroupLogic.class);

    private final SessionDao sessionDao;
    private final DeviceDao deviceDao;

    public MobileHardwareGroupLogic(Holder holder) {
        this.sessionDao = holder.sessionDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        //here expecting command in format "dashId-widgetId-groupId vw 88 1"
        String[] split = StringUtils.split2(message.body);

        if (split.length < 2) {
            log.debug("Not valid write command.");
            ctx.writeAndFlush(json(message.id, "Not valid write group command format."), ctx.voidPromise());
            return;
        }

        String[] ids = split[0].split(StringUtils.DEVICE_SEPARATOR_STRING);

        if (ids.length < 3) {
            log.debug("Not valid write command.");
            ctx.writeAndFlush(json(message.id, "Not valid write group command format."), ctx.voidPromise());
            return;
        }

        int dashId = Integer.parseInt(ids[0]);
        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        long widgetId = Long.parseLong(ids[1]);
        DeviceTiles deviceTiles = dash.getDeviceTilesByIdOrThrow(widgetId);

        String body = split[1];
        char operation = body.charAt(1);

        if (operation == 'w') {
            String[] splitBody = split3(body);

            if (splitBody.length < 3) {
                log.debug("Not valid write command.");
                ctx.writeAndFlush(json(message.id, "Not valid write command format."), ctx.voidPromise());
                return;
            }

            PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
            short pin = NumberUtil.parsePin(splitBody[1]);
            String value = splitBody[2];
            long now = System.currentTimeMillis();

            long groupId = Long.parseLong(ids[2]);
            Group group = deviceTiles.getGroupByIdOrThrow(groupId);
            group.updateValue(pin, pinType, value);

            DeviceStorageKey deviceStorageKey = new DeviceStorageKey(pin, pinType);
            int[] deviceIds = group.deviceIds;
            for (int deviceId : deviceIds) {
                Device device = deviceDao.getById(deviceId);
                if (device != null) {
                    device.updateValue(deviceStorageKey, value, now);
                }
            }

            Session session = sessionDao.getOrgSession(state.user.orgId);
            //sending to shared dashes and master-master apps
            //session.sendToSharedApps(ctx.channel(), dash.sharedToken, DEVICE_SYNC, message.id, message.body);
            session.sendToApps(ctx.channel(), MOBILE_HARDWARE_GROUP, message.id, message.body);
            session.sendToSelectedDeviceOnWeb(MOBILE_HARDWARE_GROUP, message.id, body, deviceIds);

            if (session.sendMessageToHardware(HARDWARE, message.id, body, deviceIds)) {
                log.debug("Device not in the network.");
                ctx.writeAndFlush(deviceNotInNetwork(message.id), ctx.voidPromise());
            }
        }
    }

}
