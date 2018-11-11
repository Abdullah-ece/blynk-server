package cc.blynk.server.core.processors;

import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.protocol.exceptions.QuotaLimitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple abstract class for handling all processor engines.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.08.17.
 */
public abstract class BaseProcessorHandler {

    protected static final Logger log = LogManager.getLogger(BaseProcessorHandler.class);

    private final EventorProcessor eventorProcessor;
    private final WebhookProcessor webhookProcessor;
    private final DeviceDao deviceDao;

    protected BaseProcessorHandler(EventorProcessor eventorProcessor,
                                   WebhookProcessor webhookProcessor, DeviceDao deviceDao) {
        this.eventorProcessor = eventorProcessor;
        this.webhookProcessor = webhookProcessor;
        this.deviceDao = deviceDao;
    }

    protected void processEventorAndWebhook(User user, DashBoard dash, int deviceId, Session session, short pin,
                                            PinType pinType, String value, long now) {
        Device device = deviceDao.getById(deviceId);
        if (device != null) {
            try {
                eventorProcessor.process(user, session, dash, device, pin, pinType, value);
                webhookProcessor.process(session, dash, device.id, pin, pinType, value, now);
            } catch (QuotaLimitException qle) {
                log.debug("Device {} reached notification limit for eventor/webhook.", device.id);
            } catch (IllegalArgumentException iae) {
                String errorMessage = iae.getMessage();
                if (errorMessage != null && errorMessage.contains("missing host")) {
                    log.debug("Error processing webhook. Reason : {}", errorMessage);
                } else {
                    log.error("Error processing eventor/webhook.", iae);
                }
            } catch (Exception e) {
                log.error("Error processing eventor/webhook.", e);
            }
        }
    }

}
