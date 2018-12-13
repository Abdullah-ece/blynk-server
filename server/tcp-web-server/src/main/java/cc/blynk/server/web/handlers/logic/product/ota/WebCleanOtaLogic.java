package cc.blynk.server.web.handlers.logic.product.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_STOP;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.12.18.
 */
public final class WebCleanOtaLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebCleanOtaLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canStopOTA();
    }

    @Override
    public int getPermission() {
        return OTA_STOP;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        if (productId == -1) {
            log.error("No productId to delete OTA progress.");
            throw new JsonException("No productId to delete OTA progress");
        }

        log.info("Deleting OTA progress for {}.", state.user.email);

        Product product = organizationDao.getProductByIdOrThrow(productId);
        product.clearOtaProgress();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
