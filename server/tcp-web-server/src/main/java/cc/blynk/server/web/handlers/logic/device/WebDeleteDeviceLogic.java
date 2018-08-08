package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.TokenManager;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebDeleteDeviceLogic {

    private static final Logger log = LogManager.getLogger(WebDeleteDeviceLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final TokenManager tokenManager;

    public WebDeleteDeviceLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.tokenManager = holder.tokenManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);
        int deviceId = Integer.parseInt(split[1]);

        //todo refactor when permissions ready
        //todo check access for the device
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} not allowed to access orgId {}", user.email, orgId);
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        Device device = deviceDao.getById(deviceId);
        if (device == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        deviceDao.delete(deviceId);

        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash != null) {
            try {
                int existingDeviceIndex = dash.getDeviceIndexById(deviceId);
                dash.devices = ArrayUtil.remove(dash.devices, existingDeviceIndex, Device.class);
            } catch (Exception e) {
                log.error("Error when removing device from the project for deviceId {} and orgId {}.", deviceId, orgId);
                //no device in app dashboard. ignore.
                //todo provide method without exception thrown
            }
        }

        tokenManager.deleteDevice(device);
        user.lastModifiedTs = System.currentTimeMillis();
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
