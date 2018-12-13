package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_DELETE;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebDeleteOwnDeviceLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final DeviceDao deviceDao;
    private final SessionDao sessionDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDiskDao reportingDiskDao;

    WebDeleteOwnDeviceLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.sessionDao = holder.sessionDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDiskDao = holder.reportingDiskDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canDeleteOwnDevice();
    }

    @Override
    public int getPermission() {
        return OWN_DEVICES_DELETE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);
        int deviceId = Integer.parseInt(split[1]);

        //todo refactor when permissions ready
        //todo check access for the device
        User user = state.user;
        Device device = deviceDao.getById(deviceId);
        if (device == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        if (!device.hasOwner(state.user.email)) {
            log.error("User {} is not owner of requested deviceId {}.", user.email, deviceId);
            throw new NoPermissionException("User is not owner of requested device.");
        }

        log.debug("Deleting device {} for orgId {}.", deviceId, orgId);
        deviceDao.delete(deviceId);

        Session session = sessionDao.getOrgSession(state.orgId);
        session.closeHardwareChannelByDeviceId(deviceId);

        blockingIOProcessor.executeHistory(() -> {
            try {
                reportingDiskDao.delete(deviceId);
            } catch (Exception e) {
                log.warn("Error removing device data. Reason : {}.", e.getMessage());
            }
        });
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
