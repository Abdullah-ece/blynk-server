package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.06.18.
 */
public class WebGetProductsLogic {

    private static final Logger log = LogManager.getLogger(WebGetProductsLogic.class);

    private final OrganizationDao organizationDao;

    public WebGetProductsLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        User user = state.user;
        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        organizationDao.calcDeviceCount(organization);

        if (ctx.channel().isWritable()) {
            String productString = JsonParser.toJson(organization.products);
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
