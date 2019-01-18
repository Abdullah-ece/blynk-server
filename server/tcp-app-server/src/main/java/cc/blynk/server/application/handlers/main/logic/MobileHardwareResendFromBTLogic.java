package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.processors.BaseProcessorHandler;
import cc.blynk.server.core.processors.WebhookProcessor;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.utils.NumberUtil;

import static cc.blynk.utils.StringUtils.split2;
import static cc.blynk.utils.StringUtils.split3;

/**
 * Handler responsible for processing messages that are forwarded
 * by application to server from Bluetooth module.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class MobileHardwareResendFromBTLogic extends BaseProcessorHandler {

    private final ReportingDBManager reportingDBManager;
    private final DeviceDao deviceDao;

    public MobileHardwareResendFromBTLogic(Holder holder) {
        super(holder.eventorProcessor, new WebhookProcessor(holder.asyncHttpClient,
                holder.limits.webhookPeriodLimitation,
                holder.limits.webhookResponseSizeLimitBytes,
                holder.limits.webhookFailureLimit,
                holder.stats),
                holder.deviceDao);
        this.reportingDBManager = holder.reportingDBManager;
        this.deviceDao = holder.deviceDao;
    }

    private static boolean isWriteOperation(String body) {
        return body.charAt(1) == 'w';
    }

    public void messageReceived(MobileStateHolder state, StringMessage message) {
        //minimum command - "1-1 vw 1"
        if (message.body.length() < 8) {
            log.debug("MobileHardwareResendFromBTLogic command body too short.");
            throw new JsonException("Command body too short.");
        }

        String[] split = split2(message.body);

        //here we have "200000"
        int deviceId = Integer.parseInt(split[0]);
        Device device = deviceDao.getByIdOrThrow(deviceId);

        if (isWriteOperation(split[1])) {
            String[] splitBody = split3(split[1]);

            if (splitBody.length < 3 || splitBody[0].isEmpty() || splitBody[2].isEmpty()) {
                log.debug("MobileHardwareResendFromBTLogic write command is wrong.");
                throw new JsonException("Write command is wrong.");
            }

            PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
            short pin = NumberUtil.parsePin(splitBody[1]);
            String value = splitBody[2];
            long now = System.currentTimeMillis();

            double parsedValue = NumberUtil.parseDouble(value);
            reportingDBManager.process(device, pin, pinType, parsedValue, now);
            device.updateValue(pin, pinType, value, now);
        }
    }

}
