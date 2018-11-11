package cc.blynk.server.web.handlers.logic.organization.users;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationUsersLogic;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class WebDeleteUserLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationUsersLogic.class);

    private final UserDao userDao;
    private final FileManager fileManager;
    private final DBManager dbManager;
    private final SessionDao sessionDao;

    public WebDeleteUserLogic(Holder holder) {
        this.userDao = holder.userDao;
        this.fileManager = holder.fileManager;
        this.dbManager = holder.dbManager;
        this.sessionDao = holder.sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        User user = state.user;
        if (split.length < 2) {
            log.debug("Wrong delete user info request {} for {}.", message.body, user.email);
            ctx.writeAndFlush(json(message.id, "Wrong delete user info request."), ctx.voidPromise());
            return;
        }

        //todo check user has access to modify the org
        //if (!user.isAdmin()) {
        //    log.error("Non admin {} tries to remove users.", user.email);
        //    ctx.writeAndFlush(json(message.id, "Only admin allowed to remove users."), ctx.voidPromise());
        //    return;
        //}

        int orgId = Integer.parseInt(split[0]);
        String[] emailsToDelete = JsonParser.readAny(split[1], String[].class);

        if (emailsToDelete == null || emailsToDelete.length == 0) {
            log.error("Bad data for user delete for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Delete command has empty body."), ctx.voidPromise());
            return;
        }

        for (String emailToDelete : emailsToDelete) {
            User userToDelete = userDao.getByName(emailToDelete);
            if (userToDelete != null) {
                if (userToDelete.isSuperAdmin()) {
                    log.error("{} tries to remove super admin.", user.email);
                    ctx.writeAndFlush(json(message.id, "You can't remove superadmin."), ctx.voidPromise());
                    return;
                }
                if (userToDelete.orgId == orgId) {
                    log.info("Deleting {} user for {}.", emailToDelete, user.email);
                    userDao.delete(emailToDelete);
                    fileManager.delete(emailToDelete);
                    dbManager.deleteUser(emailToDelete);
                    sessionDao.deleteUser(orgId, emailToDelete);
                }
            }
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
