package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.DBManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.utils.BlynkByteBufUtil.*;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareLogEventLogic {

    private static final Logger log = LogManager.getLogger(HardwareLogEventLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;

    public HardwareLogEventLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length == 0) {
            log.error("Log event command body is empty.");
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        Device device = deviceDao.getById(state.deviceId);
        if (device == null) {
            log.error("Device with id {} not exists!", state.deviceId);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        Product product = organizationDao.getProductById(device.productId);
        if (product == null) {
            log.error("Product with id {} not exists.", device.productId);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String eventCode = split[0];
        Event event = product.findEventByCode(eventCode.hashCode());

        if (event == null) {
            log.error("Event with code {} not found in product {}.", eventCode, product.id);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String description = split.length > 1 ? split[1] : null;

        blockingIOProcessor.executeDB(() -> {
            try {
                dbManager.insertEvent(device.id, event.getType(), System.currentTimeMillis(), eventCode.hashCode(), description, false);
                ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error inserting log event.", e);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            }
        });
    }


}
