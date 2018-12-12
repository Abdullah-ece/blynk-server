package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_DELETE;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebCanDeleteProductLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;

    public WebCanDeleteProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canDeleteProduct();
    }

    @Override
    public int getPermission() {
        return PRODUCT_DELETE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId;
        int productId;
        if (split.length == 2) {
            orgId = Integer.parseInt(split[0]);
            productId = Integer.parseInt(split[1]);
        } else {
            orgId = state.orgId;
            productId = Integer.parseInt(split[0]);
        }

        Product product = organizationDao.getProductOrThrow(orgId, productId);
        if (product.devices.length > 0) {
            ctx.writeAndFlush(json(message.id, "You can't delete product with devices."), ctx.voidPromise());
            return;
        }

        int[] subProductIds = organizationDao.subProductIds(orgId, productId);
        if (subProductIds.length != 0) {
            ctx.writeAndFlush(json(message.id, "You can't delete product that is used in sub organizations."),
                    ctx.voidPromise());
            return;
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
