package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.BaseProcessorHandler;
import cc.blynk.server.core.processors.RuleEngineProcessor;
import cc.blynk.server.core.processors.WebhookProcessor;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
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
public final class HardwareLogic extends BaseProcessorHandler {

    private final SessionDao sessionDao;
    private final ReportingDiskDao reportingDiskDao;
    private final OrganizationDao organizationDao;
    private final RuleEngineProcessor ruleEngineProcessor;
    private Organization org;

    public HardwareLogic(Holder holder) {
        super(holder.eventorProcessor, new WebhookProcessor(holder.asyncHttpClient,
                holder.limits.webhookPeriodLimitation,
                holder.limits.webhookResponseSizeLimitBytes,
                holder.limits.webhookFailureLimit,
                holder.stats),
                holder.deviceDao);
        this.sessionDao = holder.sessionDao;
        this.reportingDiskDao = holder.reportingDiskDao;
        this.organizationDao = holder.organizationDao;
        this.ruleEngineProcessor = holder.ruleEngineProcessor;
    }

    private static boolean isWriteOperation(String body) {
        return body.charAt(1) == 'w';
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        messageReceived(ctx, message, state.orgId, state.device);
    }

    public void messageReceived(ChannelHandlerContext ctx, StringMessage message,
                                int orgId, Device device) {
        String body = message.body;

        //minimum command - "ar 1"
        if (body.length() < 4) {
            log.trace("HardwareLogic command body too short.");
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        if (isWriteOperation(body)) {
            String[] splitBody = split3(body);

            if (splitBody.length < 3 || splitBody[0].isEmpty() || splitBody[2].isEmpty()) {
                log.trace("Write command is wrong {} for deviceId {}.", body, device.id);
                ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
                return;
            }

            PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
            short pin = NumberUtil.parsePin(splitBody[1]);
            String value = splitBody[2];

            long now = System.currentTimeMillis();
            reportingDiskDao.process(device, pin, pinType, value, now);
            if (org == null) {
                org = organizationDao.getOrgByIdOrThrow(orgId);
            }

            String prevValue = device.updateValue(pin, pinType, value, now);
            ruleEngineProcessor.process(org, device, pin, pinType, prevValue, value);

            Session session = sessionDao.getOrgSession(orgId);

            int deviceId = device.id;
            session.sendToApps(HARDWARE, message.id, deviceId, body);
            session.sendToSelectedDeviceOnWeb(HARDWARE, message.id, body, deviceId);
        }
    }

}
