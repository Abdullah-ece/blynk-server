package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.TokensPool;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.utils.FileLoaderUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.core.http.Response.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/account")
@ChannelHandler.Sharable
public class AccountHandler extends BaseHttpHandler {

    private final UserDao userDao;
    private final String resetPassUrl;
    private final String emailBody;
    private final MailWrapper mailWrapper;
    private final BlockingIOProcessor blockingIOProcessor;
    private final TokensPool tokensPool;

    public AccountHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;

        this.resetPassUrl = "https://" + holder.props.getProperty("reset-pass.host") + "/landing?token=";
        this.mailWrapper = holder.mailWrapper;
        this.emailBody = FileLoaderUtil.readResetPassMailBody();
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.tokensPool = new TokensPool(60 * 60 * 1000);
    }

    @GET
    @Path("")
    public Response get(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        return ok(httpSession.user);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response update(User updatedUser) {
        User existingUser = userDao.getByName(updatedUser.email, updatedUser.appName);
        existingUser.update(updatedUser);
        return ok(existingUser);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/resetPass")
    public Response resetPath(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("{} trying to reset pass.", httpSession.user.email);

        String email = httpSession.user.email;

        tokensPool.addToken(token, httpSession.user);
        String message = emailBody.replace("{RESET_URL}", resetPassUrl + token);
        log.info("Sending token to {} address", email);

        blockingIOProcessor.execute(() -> {
            Response response;
            try {
                mailWrapper.sendHtml(email, "Password reset request.", message);
                log.info("{} mail sent.", email);
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
