package cc.blynk.server.web.handlers.logic.organization.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.web.Organization;
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
public final class WebDeleteShipmentLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebDeleteShipmentLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public int getPermission() {
        return OTA_STOP;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int shipmentId = Integer.parseInt(message.body);

        if (shipmentId == -1) {
            log.error("No productId to delete OTA progress.");
            throw new JsonException("No productId to delete OTA progress");
        }

        log.info("Deleting OTA progress for {}.", state.user.email);

        Organization org = organizationDao.getOrgByIdOrThrow(state.selectedOrgId);
        org.deleteShipment(shipmentId);

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
