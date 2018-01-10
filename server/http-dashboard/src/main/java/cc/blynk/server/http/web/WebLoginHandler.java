package cc.blynk.server.http.web;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.Context;
import cc.blynk.core.http.annotation.CookieHeader;
import cc.blynk.core.http.annotation.FormParam;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.TokensPool;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.utils.FileLoaderUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.http.MediaType;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.net.URLEncoder;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.ok;
import static cc.blynk.core.http.Response.redirect;
import static cc.blynk.utils.AppNameUtil.BLYNK;
import static io.netty.handler.codec.http.HttpHeaderNames.SET_COOKIE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.12.15.
 */
@Path("")
@ChannelHandler.Sharable
public class WebLoginHandler extends BaseHttpHandler {

    //1 month
    private static final int COOKIE_EXPIRE_TIME = 30 * 60 * 60 * 24;

    private final UserDao userDao;
    private final OrganizationDao organizationDao;
    private final String resetURL;
    private final String host;
    private final String emailBody;
    private final MailWrapper mailWrapper;
    private final BlockingIOProcessor blockingIOProcessor;
    private final TokensPool tokensPool;

    public WebLoginHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
        this.organizationDao = holder.organizationDao;

        this.resetURL = "https://" + holder.props.getProperty("server.host") + "/dashboard" + "/resetPass?token=";
        this.host = "https://" + holder.props.getProperty("server.host");
        this.mailWrapper = holder.mailWrapper;
        this.emailBody = FileLoaderUtil.readResetPassMailBody();
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.tokensPool = holder.tokensPool;
    }

    private static Cookie makeDefaultSessionCookie(String sessionId, int maxAge) {
        DefaultCookie cookie = new DefaultCookie(SessionDao.SESSION_COOKIE, sessionId);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/login")
    public Response login(@FormParam("email") String email,
                          @FormParam("password") String password) {

        if (email == null || password == null) {
            log.error("Empty email or password field.");
            return badRequest("Empty email or password field.");
        }

        email = email.toLowerCase();

        User user = userDao.getByName(email, BLYNK);

        if (user == null) {
            log.error("User not found.");
            return badRequest("User not found.");
        }

        if (!password.equals(user.pass)) {
            log.error("Wrong password for {}", user.name);
            return badRequest("Wrong password or username.");
        }

        Response response = ok(user);

        Cookie cookie = makeDefaultSessionCookie(sessionDao.generateNewSession(user), COOKIE_EXPIRE_TIME);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));

        return response;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/invite")
    public Response loginViaInvite(@FormParam("token") String token,
                                   @FormParam("password") String password) {

        if (token == null || password == null) {
            log.error("Empty token or password field.");
            return badRequest("Empty token or password field.");
        }

        User user = tokensPool.getUser(token);

        if (user == null) {
            log.error("User not found.");
            return badRequest("Your invitation expired or was used already. Please request new one.");
        }

        user.pass = password;
        user.status = UserStatus.Active;
        Organization org = organizationDao.getOrgByIdOrThrow(user.orgId);
        org.isActive = true;

        Response response = ok(user);

        Cookie cookie = makeDefaultSessionCookie(sessionDao.generateNewSession(user), COOKIE_EXPIRE_TIME);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));

        return response;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/resetPass")
    public Response resetPass(@FormParam("token") String token,
                              @FormParam("password") String password) {

        if (token == null || password == null) {
            log.error("Empty token or password field.");
            return badRequest("Empty token or password field.");
        }

        User user = tokensPool.getUser(token);

        if (user == null) {
            log.error("User not found.");
            return badRequest("Token does not exist.");
        }

        user.pass = password;
        user.status = UserStatus.Active;

        Response response = ok(user);

        Cookie cookie = makeDefaultSessionCookie(sessionDao.generateNewSession(user), COOKIE_EXPIRE_TIME);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));

        return response;
    }

    @POST
    @Path("/logout")
    public Response logout(@CookieHeader("session") Cookie sessionCookie) {
        Response response = redirect(rootPath);

        if (sessionCookie != null) {
            sessionDao.deleteHttpSession(sessionCookie.value());
        } else {
            log.error("Cookie is empty for logout command.");
        }
        Cookie cookie = makeDefaultSessionCookie("", 0);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
        return response;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/sendResetPass")
    public Response sendResetPath(@Context ChannelHandlerContext ctx, @FormParam("email") String email) {
        if (BlynkEmailValidator.isNotValidEmail(email)) {
            log.info("User email {} format field is wrong .", email);
            return badRequest("User email field is wrong.");
        }

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("{} trying to reset pass.", email);

        User user = userDao.getByName(email, BLYNK);

        if (user == null) {
            log.info("User with passed email {} not found.", email);
            return ok("Email was sent.");
        }

        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);
        if (organization == null) {
            log.info("Organization with orgId {} not found.", user.orgId);
            return badRequest("Organization for that user is no longer exist.");
        }

        tokensPool.addToken(token, user);

        blockingIOProcessor.execute(() -> {
            Response response;
            try {
                String body = emailBody
                        .replace("{organization}", organization.name)
                        .replace("{host}", host)
                        .replace("{link}", resetURL + token + "&email=" + URLEncoder.encode(email, "UTF-8"));
                String subject = "Reset your {organization} Dashboard password".replace("{organization}",
                        organization.name);

                mailWrapper.sendHtml(email, subject, body);
                log.info("Reset email sent to {}.", email);
                response = ok("Email was sent.");
            } catch (Exception e) {
                log.info("Error sending mail for {}. Reason : {}", email, e.getMessage());
                response = badRequest("Error sending reset email.");
            }
            ctx.writeAndFlush(response);
        });

        return null;
    }

}
