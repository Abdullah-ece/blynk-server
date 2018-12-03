package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.PermissionBasedLogic;
import cc.blynk.server.web.handlers.logic.organization.dto.SetAuthTokenDTO;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.model.permissions.PermissionsTable.SET_AUTH_TOKEN;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebSetAuthTokenForDeviceLogic implements PermissionBasedLogic {

    private static final Logger log = LogManager.getLogger(WebSetAuthTokenForDeviceLogic.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final SessionDao sessionDao;

    public WebSetAuthTokenForDeviceLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.sessionDao = holder.sessionDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canSetAuthToken();
    }

    @Override
    public int getPermission() {
        return SET_AUTH_TOKEN;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        SetAuthTokenDTO setAuthTokenDTO = JsonParser.readAny(message.body, SetAuthTokenDTO.class);

        if (setAuthTokenDTO == null) {
            throw new JsonException("Error parsing set auth token request.");
        }

        if (!setAuthTokenDTO.isValid()) {
            throw new JsonException("Set auth token is not valid. Token is empty or length is not 32 chars.");
        }

        //todo refactor when permissions ready
        int orgId = setAuthTokenDTO.orgId;
        int deviceId = setAuthTokenDTO.deviceId;
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} not allowed to access orgId {}", user.email, orgId);
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        DeviceValue deviceValue = deviceDao.getDeviceValueById(deviceId);
        if (deviceValue == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Requested device not found."), ctx.voidPromise());
            return;
        }

        Device device = deviceValue.device;
        Product product = deviceValue.product;
        String newToken = setAuthTokenDTO.token;

        Role role = state.role;
        if (role.canEditOrgDevice() || (role.canEditOwnDevice() && device.hasOwner(state.user))) {
            deviceDao.assignNewToken(user.orgId, user.email, product, device, newToken);
            log.warn("Manual setting auth token {} for device {}, orgId = {}.", newToken, deviceDao, orgId);
        } else {
            throw new NoPermissionException("You are not allowed to set auth token.");
        }

        Session session = sessionDao.getOrgSession(orgId);
        session.closeHardwareChannelByDeviceId(deviceId);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
