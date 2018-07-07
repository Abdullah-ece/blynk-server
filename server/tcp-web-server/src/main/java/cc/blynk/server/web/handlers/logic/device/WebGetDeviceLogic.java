package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.DeviceDTO;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.productNotExists;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetDeviceLogic {

    private static final Logger log = LogManager.getLogger(WebGetDeviceLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebGetDeviceLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);
        int deviceId = Integer.parseInt(split[1]);

        //todo refactor when permissions ready
        //todo check access for the device
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} not allowed to access orgId {}", user.email, orgId);
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        Device device = deviceDao.getById(deviceId);
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Product product = org.getProduct(device.productId);

        if (product == null) {
            log.error("Product with passed id {} not exists for org {}.", device.productId, orgId);
            ctx.writeAndFlush(productNotExists(message.id), ctx.voidPromise());
            return;
        }

        if (ctx.channel().isWritable()) {
            String devicesString = new DeviceDTO(device, product, org.name).toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesString), ctx.voidPromise());
        }
    }

}
