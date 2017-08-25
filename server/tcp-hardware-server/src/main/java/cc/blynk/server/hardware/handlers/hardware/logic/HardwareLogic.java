package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.ReportingDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.processors.BaseProcessorHandler;
import cc.blynk.server.core.processors.WebhookProcessor;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.utils.BlynkByteBufUtil.illegalCommand;
import static cc.blynk.utils.StringUtils.split3;

/**
 * Handler responsible for forwarding messages from hardware to applications.
 * Also handler stores all incoming hardware commands to disk in order to export and
 * analyze data.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareLogic extends BaseProcessorHandler {

    private final ReportingDao reportingDao;
    private final SessionDao sessionDao;

    public HardwareLogic(Holder holder, String email) {
        super(holder.eventorProcessor, new WebhookProcessor(holder.asyncHttpClient,
                holder.limits.WEBHOOK_PERIOD_LIMITATION,
                holder.limits.WEBHOOK_RESPONSE_SUZE_LIMIT_BYTES,
                holder.limits.WEBHOOK_FAILURE_LIMIT,
                holder.stats,
                email));
        this.sessionDao = holder.sessionDao;
        this.reportingDao = holder.reportingDao;
    }

    private static boolean isWriteOperation(String body) {
        return body.charAt(1) == 'w';
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        String body = message.body;

        //minimum command - "ar 1"
        if (body.length() < 4) {
            log.debug("HardwareLogic command body too short.");
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        DashBoard dash = state.dash;

        if (isWriteOperation(body)) {
            String[] splitBody = split3(body);

            if (splitBody.length < 3 || splitBody[0].length() == 0 || splitBody[2].length() == 0) {
                log.debug("Write command is wrong.");
                ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
                return;
            }

            PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
            byte pin = ParseUtil.parseByte(splitBody[1]);
            String value = splitBody[2];
            long now = System.currentTimeMillis();
            int deviceId = state.device.id;

            reportingDao.process(state.user, dash.id, deviceId, pin, pinType, value, now);
            dash.update(deviceId, pin, pinType, value, now);

            Session session = sessionDao.userSession.get(state.userKey);
            process(state.user, dash, deviceId, session, pin, pinType, value, now);

            if (dash.isActive) {
                session.sendToApps(HARDWARE, message.id, dash.id, deviceId, body);
            } else {
                log.debug("No active dashboard.");
            }
        }
    }

}
