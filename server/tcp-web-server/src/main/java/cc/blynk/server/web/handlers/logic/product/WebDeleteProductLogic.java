package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.utils.IntArray;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_DELETE;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebDeleteProductLogic implements PermissionBasedLogic {

    private static final Logger log = LogManager.getLogger(WebDeleteProductLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final SessionDao sessionDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDiskDao reportingDiskDao;
    private final UserDao userDao;

    public WebDeleteProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.sessionDao = holder.sessionDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDiskDao = holder.reportingDiskDao;
        this.userDao = holder.userDao;
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
        String[] split = split2(message.body);

        int productId;
        int orgId;
        if (split.length == 1) {
            orgId = state.orgId;
            productId = Integer.parseInt(message.body);
        } else {
            orgId = Integer.parseInt(split[0]);
            productId = Integer.parseInt(split[1]);
        }

        User user = state.user;

        Product product = organizationDao.getProductByIdOrThrow(productId);
        if (product.parentId > 0) {
            log.error("Product {} is reference and can be deleted only via parent product. {}.",
                    product.id, user.email);
            ctx.writeAndFlush(json(message.id,
                    "Sub Org can't do anything with the Product Templates created by Meta Org."), ctx.voidPromise());
            return;
        }

        //todo check access
        Organization org = organizationDao.getOrgById(orgId);
        List<Device> devicesToRemove = deviceDao.getAllByProductId(productId);
        IntArray intArray = new IntArray();
        for (Device device : devicesToRemove) {
            int deviceId = device.id;
            log.trace("{} deleting deviceId {} for orgId {} and product {}.", user.email, deviceId, org.id, productId);
            deviceDao.delete(deviceId);
            intArray.add(deviceId);
        }

        int[] deviceIds = intArray.toArray();
        Session session = sessionDao.getOrgSession(org.id);
        session.closeHardwareChannelByDeviceId(deviceIds);
        blockingIOProcessor.executeHistory(() -> {
            try {
                reportingDiskDao.delete(deviceIds);
            } catch (Exception e) {
                log.warn("Error removing device data. Reason : {}.", e.getMessage());
            }
        });

        boolean isRemoved = org.deleteProduct(productId);

        List<User> users = userDao.getAllUsersByOrgId(orgId);
        for (User tempUser : users) {
            tempUser.deleteDevice(deviceIds);
        }

        if (isRemoved) {
            log.debug("Product {} successfully deleted for {}", productId, user.email);
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(json(message.id, "Error removing product."), ctx.voidPromise());
        }
    }

}
