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

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class MobileGetOwnDevicesLogic implements PermissionBasedLogic<MobileStateHolder> {

    private final OrganizationDao organizationDao;

    MobileGetOwnDevicesLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewOwnDevices();
    }

    @Override
    public int getPermission() {
        return OWN_DEVICES_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        User user = state.user;
        int orgId = user.orgId;

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        List<DeviceMobileDTO> deviceDTOs = org.getDevicesByOwnerMobileDTOs(user.email);
        String devicesJson = JsonParser.toJson(deviceDTOs);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesJson), ctx.voidPromise());
        }
    }

}
