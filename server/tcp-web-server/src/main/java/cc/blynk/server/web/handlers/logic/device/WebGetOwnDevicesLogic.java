package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.PermissionBasedLogic;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetOwnDevicesLogic implements PermissionBasedLogic {

    private static final Logger log = LogManager.getLogger(WebGetOwnDevicesLogic.class);

    private final OrganizationDao organizationDao;

    public WebGetOwnDevicesLogic(Holder holder) {
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
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);

        List<DeviceDTO> deviceDTOs = org.getDevicesByOwnerDTOs(state.user.email);
        String devicesJson = JsonParser.toJson(deviceDTOs);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesJson), ctx.voidPromise());
        }
    }

}
