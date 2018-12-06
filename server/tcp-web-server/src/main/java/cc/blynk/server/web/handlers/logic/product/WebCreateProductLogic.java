package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_CREATE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebCreateProductLogic implements PermissionBasedLogic {

    private static final Logger log = LogManager.getLogger(WebCreateProductLogic.class);

    private final OrganizationDao organizationDao;

    public WebCreateProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canCreateProduct();
    }

    @Override
    public int getPermission() {
        return PRODUCT_CREATE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        ProductAndOrgIdDTO productAndOrgIdDTO = JsonParser.readAny(message.body, ProductAndOrgIdDTO.class);

        User user = state.user;
        if (productAndOrgIdDTO == null) {
            log.error("Wrong create product command for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Wrong create product command."), ctx.voidPromise());
            return;
        }

        if (productAndOrgIdDTO.product == null) {
            log.error("Product is empty {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Product is empty."), ctx.voidPromise());
            return;
        }

        Product product = productAndOrgIdDTO.product.toProduct();
        product.validate();

        Organization organization = organizationDao.getOrgById(productAndOrgIdDTO.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            ctx.writeAndFlush(json(message.id, "Cannot find organization."), ctx.voidPromise());
            return;
        }

        if (organization.isSubOrg()) {
            log.error("User {} can't create products for sub organizations.", user.email);
            ctx.writeAndFlush(json(message.id, "User can't create products for sub organizations."), ctx.voidPromise());
            return;
        }

        if (!organization.isValidProductName(product)) {
            log.error("Organization {} already has product with name {} for {}.",
                    organization.name, product.name, user.email);
            ctx.writeAndFlush(json(message.id, "Organization already has product with that name."), ctx.voidPromise());
            return;
        }

        if (!product.isValidEvents()) {
            log.error("Events are not valid for the product {} for {}.", product.name, user.email);
            ctx.writeAndFlush(json(message.id, "Events are not valid for the product."), ctx.voidPromise());
            return;
        }

        product = organizationDao.createProduct(productAndOrgIdDTO.orgId, product);
        log.debug("Product for {} and orgId={} successfully created. UserOrgId={}, {}.",
                user.email, productAndOrgIdDTO.orgId, user.orgId, product);

        if (ctx.channel().isWritable()) {
            String productString = product.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
