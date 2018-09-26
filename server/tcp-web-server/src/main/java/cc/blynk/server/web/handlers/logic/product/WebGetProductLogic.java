package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetProductLogic {

    private static final Logger log = LogManager.getLogger(WebGetProductLogic.class);

    private WebGetProductLogic() {
    }

    public static void messageReceived(Holder holder,
                                       ChannelHandlerContext ctx, User user, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        //todo for now taking product only from user organization
        int orgId = user.orgId;
        Organization organization = holder.organizationDao.getOrgById(orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            ctx.writeAndFlush(json(message.id, "Cannot find organization."), ctx.voidPromise());
            return;
        }

        Product product = holder.organizationDao.getProductById(productId);

        if (product == null) {
            log.error("Cannot find product with id {} for org {} and user {}",
                    productId, organization.name, user.email);
            ctx.writeAndFlush(json(message.id, "Cannot find product with passed id."), ctx.voidPromise());
            return;
        }

        if (!holder.organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access product he has no access.", user.email);
            ctx.writeAndFlush(json(message.id, "User tries to access product he has no access."), ctx.voidPromise());
            return;
        }

        if (ctx.channel().isWritable()) {
            String productString = product.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
