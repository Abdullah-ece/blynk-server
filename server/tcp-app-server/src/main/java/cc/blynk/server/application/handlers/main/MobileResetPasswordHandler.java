package cc.blynk.server.application.handlers.main;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.appllication.ResetPasswordMessage;
import cc.blynk.server.internal.token.BaseToken;
import cc.blynk.server.internal.token.ResetPassToken;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.notifications.mail.QrHolder;
import cc.blynk.server.notifications.mail.ResetQrHolder;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.properties.Placeholders;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.server.internal.WebByteBufUtil.notAllowed;
import static cc.blynk.server.internal.WebByteBufUtil.serverError;

@ChannelHandler.Sharable
public class MobileResetPasswordHandler extends SimpleChannelInboundHandler<ResetPasswordMessage> {

    private static final Logger log = LogManager.getLogger(MobileResetPasswordHandler.class);

    private final TokensPool tokensPool;
    private final String resetEmailSubj;
    private final String resetEmailBody;
    private final String resetConfirmationSubj;
    private final String resetConfirmationBody;
    private final MailWrapper mailWrapper;
    private final UserDao userDao;
    private final TimerWorker timerWorker;
    private final BlockingIOProcessor blockingIOProcessor;
    private final String resetPasswordUrl;
    private final String httpsServerUrl;
    private final OrganizationDao organizationDao;

    public MobileResetPasswordHandler(Holder holder) {
        this.tokensPool = holder.tokensPool;
        String productName = holder.props.productName;
        this.resetEmailSubj = "Password restoration for your " + productName + " account.";
        this.resetEmailBody = holder.textHolder.appResetEmailTemplate
                .replace(Placeholders.PRODUCT_NAME, productName);
        this.resetConfirmationSubj = "Your new password on " + productName;
        this.resetConfirmationBody = holder.textHolder.appResetEmailConfirmationTemplate
                .replace(Placeholders.PRODUCT_NAME, productName);
        this.mailWrapper = holder.mailWrapper;
        this.userDao = holder.userDao;
        this.timerWorker = holder.timerWorker;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.resetPasswordUrl = holder.props.getResetPasswordUrl();
        this.httpsServerUrl = holder.props.httpsServerUrl;
        this.organizationDao = holder.organizationDao;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResetPasswordMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        switch (messageParts[0]) {
            case "start" :
                if (messageParts.length < 3) {
                    log.debug("Wrong income message format.");
                    ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
                    return;
                }
                sendResetEMail(ctx, messageParts[1], messageParts[2], message.id);
                break;
            case "verify" :
                if (messageParts.length < 2) {
                    log.debug("Wrong income message format.");
                    ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
                    return;
                }
                verifyToken(ctx, messageParts[1], message.id);
                break;
            case "reset" :
                if (messageParts.length < 3) {
                    log.debug("Wrong income message format.");
                    ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
                    return;
                }
                reset(ctx, messageParts[1], messageParts[2], message.id);
                break;
        }
    }

    private void reset(ChannelHandlerContext ctx, String token, String passHash, int msgId) {
        ResetPassToken resetPassToken = tokensPool.getResetPassToken(token);
        if (resetPassToken == null) {
            log.warn("Invalid token for reset pass {}", token);
            ctx.writeAndFlush(notAllowed(msgId, "Invalid token for reset password."), ctx.voidPromise());
        } else {
            String email = resetPassToken.email;
            User user = userDao.getByName(email);
            if (user == null) {
                log.warn("User not exists anymore. {}", resetPassToken);
                ctx.writeAndFlush(notAllowed(msgId, "User not exists anymore."), ctx.voidPromise());
                return;
            }
            Organization org = organizationDao.getOrgByIdOrThrow(user.orgId);
            if (org == null) {
                log.info("Organization with orgId {} not found.", user.orgId);
                ctx.writeAndFlush(json(msgId, "Organization not found."), ctx.voidPromise());
                return;
            }
            if (user.status == UserStatus.Pending) {
                userDao.createProjectForExportedApp(timerWorker, user, userDao.getAppName());
            }
            user.resetPass(passHash);
            tokensPool.removeToken(token);
            blockingIOProcessor.execute(() -> {
                try {
                    String body = resetConfirmationBody
                            .replace(Placeholders.ORG_LOGO_URL, httpsServerUrl + org.getLogoOrDefault())
                            .replace(Placeholders.EMAIL, email);
                    mailWrapper.sendHtml(email, resetConfirmationSubj, body);
                    log.debug("Confirmation {} mail sent.", email);
                } catch (Exception e) {
                    log.error("Error sending confirmation mail for {}. Reason : {}", email, e.getMessage());
                }
            });
            ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
        }
    }

    private void verifyToken(ChannelHandlerContext ctx, String token, int msgId) {
        BaseToken tokenBase = tokensPool.getBaseToken(token);
        if (tokenBase == null) {
            log.warn("Invalid token for reset pass {}", token);
            ctx.writeAndFlush(notAllowed(msgId, "Invalid token for reset password."), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
        }
    }

    private void sendResetEMail(ChannelHandlerContext ctx, String inEMail, String appName, int msgId) {
        String trimmedEmail = inEMail.trim().toLowerCase();

        if (BlynkEmailValidator.isNotValidEmail(trimmedEmail)) {
            log.debug("Wrong income email for reset password.");
            ctx.writeAndFlush(illegalCommand(msgId, "Wrong income email for reset password."), ctx.voidPromise());
            return;
        }

        User user = userDao.getByName(trimmedEmail);

        if (user == null) {
            log.debug("User doesn't exist.");
            ctx.writeAndFlush(illegalCommand(msgId, "User doesn't exist."), ctx.voidPromise());
            return;
        }

        if (tokensPool.hasResetToken(trimmedEmail, appName)) {
            log.warn("Reset code was already generated.");
            ctx.writeAndFlush(notAllowed(msgId, "Reset code was already generated."), ctx.voidPromise());
            return;
        }

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("{} trying to reset pass.", trimmedEmail);

        ResetPassToken userToken = new ResetPassToken(trimmedEmail, appName);
        tokensPool.addToken(token, userToken);

        String resetUrl = StringUtils.buildResetPasswordUrl(resetPasswordUrl, token, trimmedEmail);
        String body = resetEmailBody.replace(Placeholders.RESET_URL, resetUrl);
        String qrString = appName.toLowerCase() + "://resetPass?token=" + token + "&email=" + trimmedEmail;
        byte[] qrBytes = QRCode.from(qrString).to(ImageType.JPG).withSize(250, 250).stream().toByteArray();
        QrHolder qrHolder = new ResetQrHolder("resetPassQr.jpg", qrBytes);

        blockingIOProcessor.execute(() -> {
            try {
                mailWrapper.sendWithAttachment(trimmedEmail, resetEmailSubj, body, qrHolder);
                log.debug("{} mail sent.", trimmedEmail);
                ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error sending mail for {}. Reason {}.", trimmedEmail, e.getMessage());
                log.trace(e);
                ctx.writeAndFlush(serverError(msgId, "Error sending mail."), ctx.voidPromise());
            }
        });
    }
}
