package cc.blynk.server.application.handlers.main.logic.graph;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileDeleteDeviceDataLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteDeviceDataLogic.class);

    private MobileDeleteDeviceDataLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        String[] messageParts = StringUtils.split2(message.body);
        int deviceId = Integer.parseInt(messageParts[0]);

        //we have only deviceId
        if (messageParts.length == 1) {
            delete(holder, ctx.channel(), message.id, deviceId);
        } else {
            //we have deviceId and datastreams to clean
            delete(holder,  ctx.channel(), message.id, deviceId,
                    messageParts[1].split(StringUtils.BODY_SEPARATOR_STRING));
        }
    }

    private static void delete(Holder holder, Channel channel, int msgId, int... deviceIds) {
        holder.blockingIOProcessor.executeHistory(() -> {
            try {
                holder.reportingDiskDao.delete(deviceIds);
                if (log.isDebugEnabled()) {
                    log.debug("Removed all files for deviceIds {}", Arrays.toString(deviceIds));
                }
                channel.writeAndFlush(ok(msgId), channel.voidPromise());
            } catch (Exception e) {
                log.warn("Error removing device data. Reason : {}.", e.getMessage());
                channel.writeAndFlush(json(msgId, "Error removing device data."), channel.voidPromise());
            }
        });
    }

    private static void delete(Holder holder, Channel channel, int msgId, int deviceId, String[] pins) {
        holder.blockingIOProcessor.executeHistory(() -> {
            try {
                for (String pinString : pins) {
                    PinType pinType = PinType.getPinType(pinString.charAt(0));
                    short pin = NumberUtil.parsePin(pinString.substring(1));
                    int removedCounter =
                            holder.reportingDBManager.reportingDBDao.deleteDataForDevice(deviceId, pin, pinType);
                    log.info("Removed {} records for deviceId {} and pin {}.", removedCounter, deviceId, pin);
                }
                channel.writeAndFlush(ok(msgId), channel.voidPromise());
            } catch (Exception e) {
                log.warn("Error removing device data. Reason : {}.", e.getMessage());
                channel.writeAndFlush(json(msgId, "Error removing device data."), channel.voidPromise());
            }
        });
    }

}
