package cc.blynk.server.web.handlers.logic.organization.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.19.
 */
public final class WebGetOrgShipmentsLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebGetOrgShipmentsLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public int getPermission() {
        return OTA_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = state.selectedOrgId;
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);

        String shipments = JsonParser.toJson(org.shipments);
        StringMessage response = makeUTF8StringMessage(message.command, message.id, shipments);
        ctx.writeAndFlush(response, ctx.voidPromise());
    }
}
