package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_DELETE;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebDeleteOrgDeviceLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final SessionDao sessionDao;
    private final UserDao userDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDiskDao reportingDiskDao;
    private final WebDeleteOwnDeviceLogic webDeleteOwnDeviceLogic;

    public WebDeleteOrgDeviceLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.sessionDao = holder.sessionDao;
        this.userDao = holder.userDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDiskDao = holder.reportingDiskDao;
        this.webDeleteOwnDeviceLogic = new WebDeleteOwnDeviceLogic(holder);
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canDeleteOrgDevice();
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_DELETE;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage msg) {
        webDeleteOwnDeviceLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);
        int deviceId = Integer.parseInt(split[1]);

        //todo refactor when permissions ready
        //todo check access for the device
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} not allowed to access orgId {}", user.email, orgId);
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        Device device = deviceDao.getById(deviceId);
        if (device == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        log.debug("Deleting device {} for orgId {}.", deviceId, orgId);
        deviceDao.delete(deviceId);
        //todo this is temp solution for now
        for (User userTemp : userDao.users.values()) {
            userTemp.deleteDevice(deviceId);
        }
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
