package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.exceptions.DeviceNotFoundException;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public final class WebTrackDeviceLogic {

    private static final Logger log = LogManager.getLogger(WebTrackDeviceLogic.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;

    public WebTrackDeviceLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);

        //special case, we switch from devices view, so no need to track anything
        if (deviceId == WebAppStateHolder.NO_DEVICE) {
            state.selectedDeviceId = deviceId;
            log.trace("Removing selected device for {} from the webapp session.", state.user);
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
            return;
        }

        User user = state.user;
        String email = user.email;
        DeviceValue deviceValue = deviceDao.getDeviceValueById(deviceId);
        if (deviceValue == null) {
            log.warn("User {} requested not existing deviceId {}.", email, deviceId);
            throw new DeviceNotFoundException();
        }

        Device device = deviceValue.device;
        Role role = state.role;
        int requestedDeviceOrd = deviceValue.orgId;

        if (!role.canViewOrgDevices()) {
            if (role.canViewOwnDevices()) {
                //user can view own devices, so we check he has permissions for own devices
                if (!device.hasOwner(user)) {
                    log.warn("{} with OWN_DEVICES_VIEW tries to access device he is not owner to.", email);
                    throw new NoPermissionException("User is not owner of the requested device.");
                }
            } else {
                log.warn("{} with OWN_DEVICES_VIEW tries to access device he is not owner to.", email);
                throw new NoPermissionException("User doesn't have any permissions to access the device.");
            }
        }

        //user has access to all devices and subdevices, so we check only he doesn't accesses parent org
        organizationDao.checkInheritanceAccess(email, user.orgId, requestedDeviceOrd);

        state.selectedDeviceId = deviceId;
        log.trace("Selecting webapp device {} for {}.", deviceId, email);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
