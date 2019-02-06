package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.REFRESH_TOKEN;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileRefreshTokenLogic {

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final SessionDao sessionDao;

    public MobileRefreshTokenLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.sessionDao = holder.sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                MobileStateHolder state, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);
        User user = state.user;

        Device device = deviceDao.getByIdOrThrow(deviceId);

        Product product = organizationDao.getProductById(device.productId);
        Organization org = organizationDao.getOrgByIdOrThrow(user.orgId);
        String token = deviceDao.assignNewToken(org, user.email, product, device);

        Session session = sessionDao.getOrgSession(state.user.orgId);
        session.closeHardwareChannelByDeviceId(deviceId);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(REFRESH_TOKEN, message.id, token), ctx.voidPromise());
        }
    }
}
