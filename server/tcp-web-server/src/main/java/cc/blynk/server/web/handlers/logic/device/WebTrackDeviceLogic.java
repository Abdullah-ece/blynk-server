package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.exceptions.DeviceNotFoundException;
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

        User user = state.user;
        DeviceValue deviceValue = deviceDao.getDeviceValueById(deviceId);
        if (deviceValue == null) {
            log.warn("User {} requested not existing deviceId {}.", user.email, deviceId);
            throw new DeviceNotFoundException();
        }

        organizationDao.checkAccess(user.email, state.role, user.orgId, deviceValue.orgId);

        state.selectedDeviceId = deviceId;
        log.trace("Selecting webapp device {} for {}.", deviceId, user.email);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
