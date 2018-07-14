package cc.blynk.server.web.handlers.logic.organization.users;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.UserInviteDTO;
import cc.blynk.server.core.protocol.model.messages.ResponseMessage;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.internal.TokenUser;
import cc.blynk.server.internal.TokensPool;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationUsersLogic;
import cc.blynk.utils.FileLoaderUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.properties.Placeholders;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLEncoder;

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.CommonByteBufUtil.serverError;
import static cc.blynk.utils.AppNameUtil.BLYNK;
import static cc.blynk.utils.StringUtils.split2;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class WebInviteUserLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationUsersLogic.class);

    private final String inviteTemplate;
    private final OrganizationDao organizationDao;
    private final UserDao userDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final TokensPool tokensPool;
    private final MailWrapper mailWrapper;
    private final String productName;
    private final String inviteURL;

    public WebInviteUserLogic(Holder holder) {
        this.inviteTemplate = FileLoaderUtil.readInviteMailBody();
        this.userDao = holder.userDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.organizationDao = holder.organizationDao;
        this.tokensPool = holder.tokensPool;
        this.mailWrapper = holder.mailWrapper;
        this.productName = holder.props.productName;
        this.inviteURL = holder.props.getInviteUrl();
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);
        UserInviteDTO userInvite = JsonParser.readAny(split[1], UserInviteDTO.class);

        if (orgId == 0 || userInvite == null || userInvite.isNotValid()) {
            log.error("Invalid invitation {}.", userInvite);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        Organization org = organizationDao.getOrgById(orgId);

        if (org == null) {
            log.error("Requested organization for invite not exists {} for {}.", userInvite, user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        if (!organizationDao.hasAccess(user, orgId)) {
            log.warn("{} (orgId = {}) tries to send invite to another organization with id = {}.",
                    user.email, user.orgId, orgId);
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        String appName = BLYNK;
        User invitedUser = userDao.invite(userInvite, orgId, appName);

        if (invitedUser == null) {
            log.error("User {}-{} already exists.", userInvite.email, appName);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("Trying to send invitation email to {}.", userInvite.email);

        blockingIOProcessor.execute(() -> {
            ResponseMessage response;
            try {
                tokensPool.addToken(token,  new TokenUser(userInvite.email, appName));
                String body = inviteTemplate
                        .replace(Placeholders.ORGANIZATION, org.name)
                        .replace(Placeholders.PRODUCT_NAME, productName)
                        .replace("{link}", inviteURL + token + "&email="
                                + URLEncoder.encode(userInvite.email, "UTF-8"));
                mailWrapper.sendHtml(userInvite.email, "Invitation to " + org.name + " dashboard.", body);
                log.info("Invitation sent to {}. From {}", userInvite.email, user.email);
                response = ok(message.id);
            } catch (Exception e) {
                log.error("Error generating invitation email.", e);
                response = serverError(message.id);
            }
            ctx.writeAndFlush(response);
        });

    }

}
