package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.PermissionBasedLogic;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_DELETE;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebCanDeleteProductLogic implements PermissionBasedLogic {

    private final DeviceDao deviceDao;

    public WebCanDeleteProductLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
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
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        if (deviceDao.productHasDevices(productId)) {
            ctx.writeAndFlush(json(message.id, "You can't delete product with devices."), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        }
    }

}
