package cc.blynk.server.web.handlers.logic.device.control;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.internal.WebByteBufUtil;
import cc.blynk.utils.NumberUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_EDIT;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.utils.StringUtils.split2;
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
public final class WebAppOwnControlHardwareLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private static final Logger log = LogManager.getLogger(WebAppOwnControlHardwareLogic.class);

    private final SessionDao sessionDao;
    private final DeviceDao deviceDao;

    WebAppOwnControlHardwareLogic(Holder holder) {
        this.sessionDao = holder.sessionDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public int getPermission() {
        return OWN_DEVICES_EDIT;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = state.selectedOrgId;
        Session session = sessionDao.getOrgSession(orgId);

        //here expecting command in format "200000 vw 88 1"
        String[] split = split2(message.body);

        //here we have "200000"
        int deviceId = Integer.parseInt(split[0]);
        state.checkControlledDeviceIsSelected(deviceId);

        Device device = deviceDao.getByIdOrThrow(deviceId);
        if (device == null) {
            log.debug("Device with passed id {} not found.", deviceId);
            return;
        }

        String[] splitBody = split3(split[1]);

        if (splitBody.length < 3) {
            log.debug("Not valid write command.");
            throw new JsonException("Not valid write command.");
        }

        PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
        short pin = NumberUtil.parsePin(splitBody[1]);
        String value = splitBody[2];

        device.updateValue(pin, pinType, value);

        Channel channel = ctx.channel();
        //sending to shared dashes and master-master apps
        session.sendToApps(DEVICE_SYNC, message.id, deviceId, split[1]);
        session.sendToSelectedDeviceOnWeb(channel, DEVICE_SYNC, message.id, split[1], deviceId);

        if (session.sendMessageToHardware(HARDWARE, message.id, split[1], deviceId)) {
            log.debug("Device not in the network.");
            ctx.writeAndFlush(WebByteBufUtil.deviceNotInNetwork(message.id), ctx.voidPromise());
        }
    }

}
