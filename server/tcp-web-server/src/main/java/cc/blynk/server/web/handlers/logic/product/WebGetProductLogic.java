package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.ProductDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.utils.StringUtils.split2;

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
        String[] split = split2(message.body);

        int orgId;
        int productId;
        if (split.length == 2) {
            orgId = Integer.parseInt(split[0]);
            productId = Integer.parseInt(split[1]);
        } else {
            orgId = user.orgId;
            productId = Integer.parseInt(split[0]);
        }

        Organization organization = holder.organizationDao.getOrgById(orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            throw new JsonException("Cannot find organization.");
        }

        Product product = organization.getProductOrThrow(productId);

        if (product == null) {
            log.error("Cannot find product with id {} for org {} and user {}",
                    productId, organization.name, user.email);
            throw new JsonException("Cannot find product with passed id.");
        }

        if (!holder.organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access product he has no access.", user.email);
            throw new JsonException("User tries to access product he has no access.");
        }

        if (ctx.channel().isWritable()) {
            String productString = new ProductDTO(product).toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
