package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.processors.BaseProcessorHandler;
import cc.blynk.server.core.processors.WebhookProcessor;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.internal.WebByteBufUtil.deviceNotInNetwork;
import static cc.blynk.server.internal.WebByteBufUtil.json;
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
public class MobileHardwareLogic extends BaseProcessorHandler {

    private static final Logger log = LogManager.getLogger(MobileHardwareLogic.class);

    private final SessionDao sessionDao;
    private final DeviceDao deviceDao;

    public MobileHardwareLogic(Holder holder) {
        super(holder.eventorProcessor, new WebhookProcessor(holder.asyncHttpClient,
                holder.limits.webhookPeriodLimitation,
                holder.limits.webhookResponseSizeLimitBytes,
                holder.limits.webhookFailureLimit,
                holder.stats),
                holder.deviceDao);
        this.sessionDao = holder.sessionDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        //here expecting command in format "200000 vw 88 1"
        String[] split = split2(message.body);

        //deviceId or tagId or device selector widget id
        int targetId = Integer.parseInt(split[0]);

        //sending message only if widget assigned to device or tag has assigned devices
        Device device = deviceDao.getById(targetId);

        if (device == null) {
            log.debug("No assigned target id for received command.");
            return;
        }

        char operation = split[1].charAt(1);
        String[] splitBody;

        if (operation == 'w') {
            splitBody = split3(split[1]);

            if (splitBody.length < 3) {
                log.debug("Not valid write command.");
                ctx.writeAndFlush(json(message.id, "Not valid write command format."), ctx.voidPromise());
                return;
            }

            PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
            short pin = NumberUtil.parsePin(splitBody[1]);
            String value = splitBody[2];
            long now = System.currentTimeMillis();

            device.updateValue(pin, pinType, value, now);

            Session session = sessionDao.getOrgSession(state.user.orgId);
            //sending to shared dashes and master-master apps
            //session.sendToSharedApps(ctx.channel(), dash.sharedToken, DEVICE_SYNC, message.id, message.body);
            session.sendToApps(ctx.channel(), DEVICE_SYNC, message.id, message.body);
            session.sendToSelectedDeviceOnWeb(DEVICE_SYNC, message.id, split[1], device.id);

            if (session.sendMessageToHardware(HARDWARE, message.id, split[1], device.id)) {
                log.debug("Device not in the network.");
                ctx.writeAndFlush(deviceNotInNetwork(message.id), ctx.voidPromise());
            }
            //processEventorAndWebhook(state.user, dash, targetId, session, pin, pinType, value, now);
        }
    }

}
