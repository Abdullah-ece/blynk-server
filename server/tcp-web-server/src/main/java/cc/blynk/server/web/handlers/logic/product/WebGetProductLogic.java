package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetProductLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;

    public WebGetProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewProduct();
    }

    @Override
    public int getPermission() {
        return PRODUCT_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        User user = state.user;
        int orgId;
        int productId;
        if (split.length == 2) {
            orgId = Integer.parseInt(split[0]);
            productId = Integer.parseInt(split[1]);
        } else {
            orgId = user.orgId;
            productId = Integer.parseInt(split[0]);
        }

        Organization organization = organizationDao.getOrgById(orgId);

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

        if (ctx.channel().isWritable()) {
            String productString = new ProductDTO(product).toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
