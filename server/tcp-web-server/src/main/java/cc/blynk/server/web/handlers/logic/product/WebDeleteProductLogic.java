package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebDeleteProductLogic {

    private static final Logger log = LogManager.getLogger(WebDeleteProductLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebDeleteProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        User user = state.user;

        Product product = organizationDao.getProductByIdOrThrow(productId);
        if (product.parentId > 0) {
            log.error("Product {} is reference and can be deleted only via parent product. {}.",
                    product.id, user.email);
            ctx.writeAndFlush(json(message.id,
                    "Sub Org can't do anything with the Product Templates created by Meta Org."), ctx.voidPromise());
            return;
        }

        if (deviceDao.productHasDevices(productId)) {
            log.error("{} not allowed to remove product {} with devices.", user.email, productId);
            ctx.writeAndFlush(json(message.id, "You are not allowed to remove product with devices."),
                    ctx.voidPromise());
            return;
        }

        //todo for now we just allow to remove from user org
        Organization org = organizationDao.getOrgById(user.orgId);
        boolean isRemoved = org.deleteProduct(productId);

        if (isRemoved) {
            log.debug("Product {} successfully deleted for {}", productId, user.email);
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(json(message.id, "Error removing product."), ctx.voidPromise());
        }
    }

}
