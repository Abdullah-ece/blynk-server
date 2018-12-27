package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.dto.ProductDTO.toDTO;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.06.18.
 */
public final class WebGetProductsLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebGetProductsLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public int getPermission() {
        return PRODUCT_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        User user = state.user;
        int orgId = state.selectedOrgId;

        Organization organization = organizationDao.getOrgByIdOrThrow(orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", orgId, user.email);
            ctx.writeAndFlush(json(message.id, "Cannot find organization."), ctx.voidPromise());
            return;
        }

        String productString = JsonParser.toJson(toDTO(organization.products));
        if (productString == null) {
            log.error("Empty response for WebGetProductsLogic and {}.", user.email);
        } else {
            log.trace("Returning products for user {} and orgId {}, length {}.",
                    user.email, orgId, productString.length());
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
