package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.TokensPool;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.AppName;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.utils.FileLoaderUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLEncoder;

import static cc.blynk.core.http.Response.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.03.17.
 */
@ChannelHandler.Sharable
@Path("")
public class ResetPassNotLoggedHandler extends BaseHttpHandler {

    private static final Logger log = LogManager.getLogger(ResetPassNotLoggedHandler.class);
    public final UserDao userDao;
    private final String resetURL;
    private final String emailBody;
    private final MailWrapper mailWrapper;
    private final BlockingIOProcessor blockingIOProcessor;
    private final TokensPool tokensPool;

    public ResetPassNotLoggedHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;

        this.resetURL = "https://" + holder.props.getProperty("reset-pass.host") + rootPath + "#/resetPass?token=";
        this.mailWrapper = holder.mailWrapper;
        this.emailBody = FileLoaderUtil.readResetPassMailBody();
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.tokensPool = holder.tokensPool;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/sendResetPass")
    public Response resetPath(@Context ChannelHandlerContext ctx, @FormParam("email") String email) {
        if (BlynkEmailValidator.isNotValidEmail(email)) {
            log.info("User email {} format field is wrong .", email);
            return badRequest("User email field is wrong.");
        }

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("{} trying to reset pass.", email);

        User user = userDao.getByName(email, AppName.BLYNK);

        if (user == null) {
            log.info("User with passed email {} not found.", email);
            return badRequest("User email field is wrong.");
        }

        tokensPool.addToken(token, user);

        blockingIOProcessor.execute(() -> {
            Response response;
            try {
                String body = emailBody
                        .replace("{name}", user.name)
                        .replace("{link}", resetURL + token + "&email=" + URLEncoder.encode(email, "UTF-8"));
                mailWrapper.sendHtml(email, "Password reset request.", body);
                log.info("Reset email sent to {}.", email);
                response = ok("Email was sent.");
            } catch (Exception e) {
                log.info("Error sending mail for {}. Reason : {}", email, e.getMessage());
                response = badRequest("Error sending reset email.");
            }
            ctx.writeAndFlush(response);
        });

        return noResponse();
    }

}
