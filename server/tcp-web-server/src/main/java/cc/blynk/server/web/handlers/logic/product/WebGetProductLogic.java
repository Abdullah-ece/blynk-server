package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetProductLogic {

    private static final Logger log = LogManager.getLogger(WebGetProductLogic.class);

    private final OrganizationDao organizationDao;

    public WebGetProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        User user = state.user;
        //todo for now taking product only from user organization
        int orgId = user.orgId;
        Organization organization = organizationDao.getOrgByIdOrThrow(orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        Product product = organizationDao.getProductById(productId);

        if (product == null) {
            log.error("Cannot find product with id {} for org {} and user {}",
                    productId, organization.name, user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access product he has no access.", user.email);
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        if (ctx.channel().isWritable()) {
            String productString = product.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
