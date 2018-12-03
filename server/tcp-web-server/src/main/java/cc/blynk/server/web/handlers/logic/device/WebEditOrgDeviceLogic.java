package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
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
import cc.blynk.server.web.handlers.PermissionBasedLogic;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_EDIT;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.server.internal.WebByteBufUtil.productNotExists;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebEditOrgDeviceLogic implements PermissionBasedLogic {

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final WebEditOwnDeviceLogic webEditOwnDeviceLogic;

    public WebEditOrgDeviceLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.webEditOwnDeviceLogic = new WebEditOwnDeviceLogic(holder);
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canEditOrgDevice();
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_EDIT;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webEditOwnDeviceLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);

        //todo refactor when permissions ready
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} not allowed to access orgId {}", user.email, orgId);
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        Device newDevice = JsonParser.parseDevice(split[1], message.id);

        if (newDevice == null || newDevice.productId < 1) {
            log.error("No data or productId is wrong. {}", newDevice);
            ctx.writeAndFlush(json(message.id, "Empty body."), ctx.voidPromise());
            return;
        }

        if (newDevice.isNotValid()) {
            log.error("WebUpdate device for {} has wrong name or board. {}", user.email, newDevice);
            ctx.writeAndFlush(json(message.id, "Device has no name or board type selected."), ctx.voidPromise());
            return;
        }

        if (newDevice.id == 0) {
            log.error("Cannot find device with id 0.");
            ctx.writeAndFlush(json(message.id, "Cannot find device with id 0."), ctx.voidPromise());
            return;
        }

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Product product = org.getProduct(newDevice.productId);
        if (product == null) {
            log.error("Product with passed id {} not exists for org {}.", newDevice.productId, orgId);
            ctx.writeAndFlush(productNotExists(message.id), ctx.voidPromise());
            return;
        }

        Device existingDevice = deviceDao.getByIdOrThrow(newDevice.id);
        organizationDao.verifyUserAccessToDevice(user, existingDevice);

        existingDevice.updateFromWeb(newDevice);

        if (ctx.channel().isWritable()) {
            String deviceString = new DeviceDTO(newDevice, product, org.name).toString();
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, deviceString), ctx.voidPromise());
        }
    }

}
