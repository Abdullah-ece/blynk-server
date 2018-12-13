package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.DeviceMobileDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileGetOrgDevicesLogic implements PermissionBasedLogic<MobileStateHolder> {

    private final OrganizationDao organizationDao;
    private final MobileGetOwnDevicesLogic mobileGetOwnDevicesLogic;

    public MobileGetOrgDevicesLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.mobileGetOwnDevicesLogic = new MobileGetOwnDevicesLogic(holder);
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewOrgDevices();
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        User user = state.user;
        int orgId = user.orgId;

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        List<DeviceMobileDTO> deviceDTOs = org.getAllMobileDeviceDTOs();
        String devicesJson = JsonParser.toJson(deviceDTOs);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesJson), ctx.voidPromise());
        }
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage msg) {
        mobileGetOwnDevicesLogic.messageReceived(ctx, state, msg);
    }

}
