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

import java.net.URLEncoder;

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
    private final String resetURL;
    private final String emailBody;
    private final MailWrapper mailWrapper;
    private final BlockingIOProcessor blockingIOProcessor;
    private final TokensPool tokensPool;

    public AccountHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;

        this.resetURL = "https://" + holder.props.getProperty("reset-pass.host") + rootPath + "#/resetPass?token=";
        this.mailWrapper = holder.mailWrapper;
        this.emailBody = FileLoaderUtil.readResetPassMailBody();
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.tokensPool = holder.tokensPool;
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

        blockingIOProcessor.execute(() -> {
            Response response;
            try {
                String body = emailBody
                        .replace("{name}", httpSession.user.name)
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
