package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_USERS;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetOrganizationUsersLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationUsersLogic.class);

    private final OrganizationDao organizationDao;
    private final UserDao userDao;

    public WebGetOrganizationUsersLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.userDao = holder.userDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = Integer.parseInt(message.body);

        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access organization he has no access.");
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        if (ctx.channel().isWritable()) {
            List<User> users = userDao.getUsersByOrgId(orgId, user.email);
            String usersString = JsonParser.toJson(users);
            ctx.writeAndFlush(makeUTF8StringMessage(WEB_GET_ORG_USERS, message.id, usersString),
                    ctx.voidPromise());
        }
    }

}
