package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.logic.organization.dto.CountDTO;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebGetDeviceCountLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebGetDeviceCountLogic(Holder holder) {
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
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        int orgId = state.selectedOrgId;

        List<Organization> childs = organizationDao.getOrgChilds(orgId);
        int totalCount = devicesCount(childs);
        Organization firstOrg = childs.get(0);

        if (ctx.channel().isWritable()) {
            int orgCount = firstOrg.deviceCount();
            CountDTO countDTO = new CountDTO(orgCount, totalCount - orgCount);
            ctx.writeAndFlush(
                    makeASCIIStringMessage(msg.command, msg.id, countDTO.toString()),
                    ctx.voidPromise());
        }
    }

    private static int devicesCount(List<Organization> childs) {
        int count = 0;
        for (Organization org : childs) {
            count += org.deviceCount();
        }
        return count;
    }

}
