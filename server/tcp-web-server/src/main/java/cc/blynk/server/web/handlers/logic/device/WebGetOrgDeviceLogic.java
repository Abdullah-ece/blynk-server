package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetOrgDeviceLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final WebGetOwnDeviceLogic webGetOwnDeviceLogic;

    public WebGetOrgDeviceLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.webGetOwnDeviceLogic = new WebGetOwnDeviceLogic(holder);
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
    public void noPermissionAction(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage msg) {
        webGetOwnDeviceLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        //todo no need?
        //int orgId = Integer.parseInt(split[0]);
        int deviceId = Integer.parseInt(split[1]);

        User user = state.user;
        DeviceValue deviceValue = deviceDao.getDeviceValueById(deviceId);
        if (deviceValue == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        int orgId = deviceValue.orgId;

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Product product = deviceValue.product;
        //this means orgId is wrong
        if (product == null) {
            log.error("Device {} not found for {}, probably wrong orgId {}.", deviceId, state.user.email, orgId);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        Device device = deviceValue.device;
        device.fillWebDashboardValues();
        if (ctx.channel().isWritable()) {
            String devicesString = new DeviceDTO(device, product, org.name).toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesString), ctx.voidPromise());
        }
    }

}
