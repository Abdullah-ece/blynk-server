package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.logic.organization.dto.DeviceCountDTO;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 25.01.19.
 */
public final class WebGetDeviceCountFromProductIDLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebGetDeviceCountFromProductIDLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public int getPermission() {
        return PRODUCT_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        int parentProductId = Integer.parseInt(msg.body);

        Product parent = organizationDao.getProductByIdOrThrow(parentProductId);

        int parentDeviceCount = parent.getDeviceCount();
        int childDeviceCount  = getChildDeviceCount(parentProductId);

        if (ctx.channel().isWritable()) {
            DeviceCountDTO deviceCountDTO = new DeviceCountDTO(parentDeviceCount, childDeviceCount);
            ctx.writeAndFlush(
                    makeASCIIStringMessage(msg.command, msg.id, deviceCountDTO.toString()),
                    ctx.voidPromise());
        }
    }

    private int getChildDeviceCount(int parentProductId) {
        int childDeviceCount = 0;
        int[] productChildIds = organizationDao.getProductChilds(parentProductId);

        // assume child can only be from same org hierarchy
        for (int childId: productChildIds) {
            Product child = organizationDao.getProductById(childId);
            if (child != null && child.id != parentProductId) {
                childDeviceCount += child.getDeviceCount();
            }
        }
        return childDeviceCount;
    }
}
