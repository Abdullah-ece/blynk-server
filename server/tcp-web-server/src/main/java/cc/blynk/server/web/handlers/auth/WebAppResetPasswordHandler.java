package cc.blynk.server.web.handlers.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.appllication.ResetPasswordMessage;
import cc.blynk.server.internal.TokenUser;
import cc.blynk.server.internal.TokensPool;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.properties.Placeholders;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.CommonByteBufUtil.serverError;
import static cc.blynk.server.internal.WebByteBufUtil.json;


/**
 * Handler responsible for managing apps login messages.
 * Initializes netty channel with a state tied with user.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class WebAppResetPasswordHandler extends SimpleChannelInboundHandler<ResetPasswordMessage> {

    private static final Logger log = LogManager.getLogger(WebAppResetPasswordHandler.class);

    private final TokensPool tokensPool;
    private final String resetEmailBody;
    private final String resetConfirmationSubj;
    private final String resetConfirmationBody;
    private final MailWrapper mailWrapper;
    private final UserDao userDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final String host;
    private final OrganizationDao organizationDao;
    private final String resetURL;

    public WebAppResetPasswordHandler(Holder holder) {
        this.tokensPool = holder.tokensPool;
        String productName = holder.props.productName;
        this.resetEmailBody = holder.textHolder.webResetEmailTemplate
                .replace(Placeholders.PRODUCT_NAME, productName);
        this.resetConfirmationSubj = "Your new password on " + productName;
        this.resetConfirmationBody = holder.textHolder.appResetEmailConfirmationTemplate
                .replace(Placeholders.PRODUCT_NAME, productName);
        this.mailWrapper = holder.mailWrapper;
        this.userDao = holder.userDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.host = holder.props.getRestoreHost();
        this.organizationDao = holder.organizationDao;
        this.resetURL = "https://" + holder.props.getProperty("server.host") + "/dashboard" + "/resetPass?token=";
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResetPasswordMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        switch (messageParts[0]) {
            case "start" :
                if (messageParts.length < 3) {
                    log.debug("Wrong income message format.");
                    ctx.writeAndFlush(json(message.id, "Wrong income message format."), ctx.voidPromise());
                    return;
                }
                sendResetEMail(ctx, messageParts[1], messageParts[2], message.id);
                break;
            case "verify" :
                if (messageParts.length < 2) {
                    log.debug("Wrong income message format.");
                    ctx.writeAndFlush(json(message.id, "Wrong income message format."), ctx.voidPromise());
                    return;
                }
                verifyToken(ctx, messageParts[1], message.id);
                break;
            case "reset" :
                if (messageParts.length < 3) {
                    log.debug("Wrong income message format.");
                    ctx.writeAndFlush(json(message.id, "Wrong income message format."), ctx.voidPromise());
                    return;
                }
                reset(ctx, messageParts[1], messageParts[2], message.id);
                break;
        }
    }

    private void reset(ChannelHandlerContext ctx, String token, String passHash, int msgId) {
        TokenUser tokenUser = tokensPool.getUser(token);
        if (tokenUser == null) {
            log.warn("Invalid token for reset password {}", token);
            ctx.writeAndFlush(json(msgId, "Invalid token for reset password."), ctx.voidPromise());
        } else {
            String email = tokenUser.email;
            User user = userDao.getByName(email, tokenUser.appName);
            if (user == null) {
                log.warn("User is not exists anymore. {}", tokenUser);
                ctx.writeAndFlush(json(msgId, "User is not exists anymore."), ctx.voidPromise());
                return;
            }
            user.resetPass(passHash);
            tokensPool.removeToken(token);
            blockingIOProcessor.execute(() -> {
                try {
                    mailWrapper.sendHtml(email, resetConfirmationSubj,
                            resetConfirmationBody.replace(Placeholders.EMAIL, email));
                    log.debug("Confirmation {} mail sent.", email);
                } catch (Exception e) {
                    log.error("Error sending confirmation mail for {}. Reason : {}", email, e.getMessage());
                }
            });
            ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
        }
    }

    private void verifyToken(ChannelHandlerContext ctx, String token, int msgId) {
        TokenUser tokenUser = tokensPool.getUser(token);
        if (tokenUser == null) {
            log.warn("Invalid token for reset pass {}.", token);
            ctx.writeAndFlush(json(msgId, "Invalid token for reset password."), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
        }
    }

    private void sendResetEMail(ChannelHandlerContext ctx, String inEMail, String appName, int msgId) {
        String trimmedEmail = inEMail.trim().toLowerCase();

        if (BlynkEmailValidator.isNotValidEmail(trimmedEmail)) {
            log.debug("Wrong income email for reset pass.");
            ctx.writeAndFlush(json(msgId, "Wrong income email for reset pass."), ctx.voidPromise());
            return;
        }

        User user = userDao.getByName(trimmedEmail, appName);

        if (user == null) {
            log.debug("User does not exists.");
            ctx.writeAndFlush(json(msgId, "User does not exists."), ctx.voidPromise());
            return;
        }

        if (tokensPool.hasToken(trimmedEmail, appName)) {
            tokensPool.cleanupOldTokens();
            log.warn("Reset code was already generated.");
            ctx.writeAndFlush(json(msgId, "Reset code was already generated."), ctx.voidPromise());
            return;
        }

        Organization org = organizationDao.getOrgByIdOrThrow(user.orgId);
        if (org == null) {
            log.info("Organization with orgId {} not found.", user.orgId);
            ctx.writeAndFlush(json(msgId, "Organization not found."), ctx.voidPromise());
            return;
        }

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("{} trying to reset pass.", trimmedEmail);

        TokenUser userToken = new TokenUser(trimmedEmail, appName);
        tokensPool.addToken(token, userToken);

        blockingIOProcessor.execute(() -> {
            try {
                String body = resetEmailBody
                        .replace(Placeholders.ORGANIZATION, org.name)
                        .replace("{host}", host)
                        .replace("{link}", resetURL + token + "&email="
                                + URLEncoder.encode(trimmedEmail, StandardCharsets.UTF_8));
                String subject = "Reset your " + org.name + " Dashboard password";

                mailWrapper.sendHtml(trimmedEmail, subject, body);
                log.info("Reset email sent to {}.", trimmedEmail);
                ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
            } catch (Exception e) {
                log.info("Error sending mail for {}. Reason : {}", trimmedEmail, e.getMessage());
                ctx.writeAndFlush(serverError(msgId), ctx.voidPromise());
            }
        });
    }

}
