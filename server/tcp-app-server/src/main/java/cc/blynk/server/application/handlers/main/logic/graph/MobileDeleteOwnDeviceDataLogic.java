package cc.blynk.server.application.handlers.main.logic.graph;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.Permission2BasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.reporting.raw.RawDataCacheForGraphProcessor;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.db.dao.ReportingDBDao;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICE_DATA_DELETE;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileDeleteOwnDeviceDataLogic implements Permission2BasedLogic<MobileStateHolder> {

    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBDao reportingDBDao;
    private final RawDataCacheForGraphProcessor rawDataCacheForGraphProcessor;
    private final DeviceDao deviceDao;

    MobileDeleteOwnDeviceDataLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBDao = holder.reportingDBManager.reportingDBDao;
        this.rawDataCacheForGraphProcessor = holder.reportingDBManager.rawDataCacheForGraphProcessor;
    }

    @Override
    public int getPermission() {
        return OWN_DEVICE_DATA_DELETE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        String[] messageParts = StringUtils.split2Device(message.body);
        int deviceId = Integer.parseInt(messageParts[0]);

        User user = state.user;
        Device device = deviceDao.getById(deviceId);
        if (device == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        if (!state.role.canDeleteOrgDeviceData() && !device.hasOwner(state.user.email)) {
            log.error("User {} is not owner of requested deviceId {}.", user.email, deviceId);
            throw new NoPermissionException("User is not owner of requested device.");
        }

        //we have only deviceId
        if (messageParts.length == 1) {
            delete(ctx.channel(), message.id, deviceId);
        } else {
            //we have deviceId and datastreams to clean
            delete(ctx.channel(), message.id, deviceId,
                    messageParts[1].split(StringUtils.BODY_SEPARATOR_STRING));
        }
    }

    private void delete(Channel channel, int msgId, int... deviceIds) {
        blockingIOProcessor.executeHistory(() -> {
            try {
                for (int deviceId : deviceIds) {
                    reportingDBDao.delete(deviceId);
                }
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

    private void delete(Channel channel, int msgId, int deviceId, String[] pins) {
        blockingIOProcessor.executeHistory(() -> {
            try {
                for (String pinString : pins) {
                    PinType pinType = PinType.getPinType(pinString.charAt(0));
                    short pin = NumberUtil.parsePin(pinString.substring(1));
                    int removedCounter =
                            reportingDBDao.delete(deviceId, pin, pinType);
                    rawDataCacheForGraphProcessor.removeCacheEntry(deviceId, pinType, pin);
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
