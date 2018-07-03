package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.SharedTokenManager;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.WebAppStateHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.APP_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.internal.CommonByteBufUtil.deviceNotInNetwork;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
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
public class WebAppHardwareLogic {

    private static final Logger log = LogManager.getLogger(WebAppHardwareLogic.class);

    private final SessionDao sessionDao;
    private final DeviceDao deviceDao;

    public WebAppHardwareLogic(Holder holder) {
        this.sessionDao = holder.sessionDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        Session session = sessionDao.userSession.get(state.userKey);

        //here expecting command in format "200000 vw 88 1"
        String[] split = split2(message.body);

        //here we have "200000"
        int deviceId = Integer.parseInt(split[0]);

        Device device = deviceDao.getById(deviceId);
        if (device == null) {
            log.debug("Device with passed id {} not found.", deviceId);
            return;
        }

        String[] splitBody = split3(split[1]);

        if (splitBody.length < 3) {
            log.debug("Not valid write command.");
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
        byte pin = Byte.parseByte(splitBody[1]);
        String value = splitBody[2];

        device.webDashboard.update(device.id, pin, pinType, value);

        Channel channel = ctx.channel();

        //sending to shared dashes and master-master apps

        //"0-" - temp solution, until app will not support new format.
        session.sendToSharedApps(channel, SharedTokenManager.ALL,
                APP_SYNC, message.id, "0-" + message.body);

        session.sendToSelectedDeviceOnWeb(channel, APP_SYNC, message.id, split[1], deviceId);

        if (session.sendMessageToHardware(HARDWARE, message.id, split[1], deviceId)) {
            log.debug("No device in session.");
            ctx.writeAndFlush(deviceNotInNetwork(message.id), ctx.voidPromise());
        }
    }

}
