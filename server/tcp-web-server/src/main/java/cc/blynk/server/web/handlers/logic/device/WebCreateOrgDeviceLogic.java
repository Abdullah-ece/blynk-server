package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_CREATE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebCreateOrgDeviceLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final WebCreateOwnDeviceLogic webCreateOwnDeviceLogic;

    public WebCreateOrgDeviceLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.webCreateOwnDeviceLogic = new WebCreateOwnDeviceLogic(holder);
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canCreateOrgDevice();
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_CREATE;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webCreateOwnDeviceLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        //todo check access to this org
        int orgId = Integer.parseInt(split[0]);
        User user = state.user;
        Device newDevice = JsonParser.parseDevice(split[1], message.id);

        if (newDevice == null) {
            log.error("Create device command is empty for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Create device command is empty."), ctx.voidPromise());
            return;
        }

        if (newDevice.productId < 1) {
            log.error("Create device for {} has wrong product id. {}", user.email, newDevice);
            ctx.writeAndFlush(json(message.id, "Command has wrong product id."), ctx.voidPromise());
            return;
        }

        if (newDevice.isNotValid()) {
            log.error("WebCreate device for {} has wrong name or board. {}", user.email, newDevice);
            ctx.writeAndFlush(json(message.id, "Device has no name or board type selected."), ctx.voidPromise());
            return;
        }

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Product product = organizationDao.assignToOrgAndAddDevice(org, newDevice);
        deviceDao.create(orgId, user.email, product, newDevice);

        if (ctx.channel().isWritable()) {
            String deviceString = new DeviceDTO(newDevice, product, org.name).toString();
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, deviceString), ctx.voidPromise());
        }
    }

}
