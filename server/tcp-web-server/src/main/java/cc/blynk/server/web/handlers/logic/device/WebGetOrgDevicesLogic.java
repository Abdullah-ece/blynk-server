package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetOrgDevicesLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;
    private final WebGetOwnDevicesLogic wegGetOwnDevicesHandler;

    public WebGetOrgDevicesLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.wegGetOwnDevicesHandler = new WebGetOwnDevicesLogic(holder);
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
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage msg) {
        String[] split = split2(msg.body);

        int orgId = Integer.parseInt(split[0]);

        //todo refactor when permissions ready
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} not allowed to access orgId {}", user.email, orgId);
            ctx.writeAndFlush(userHasNoAccessToOrg(msg.id), ctx.voidPromise());
            return;
        }

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);

        List<DeviceDTO> deviceDTOs = org.getAllDeviceDTOs(false);
        String devicesJson = JsonParser.toJson(deviceDTOs);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(msg.command, msg.id, devicesJson), ctx.voidPromise());
        }
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage msg) {
        wegGetOwnDevicesHandler.messageReceived(ctx, state, msg);
    }
}
