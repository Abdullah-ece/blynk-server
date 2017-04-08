package cc.blynk.server.http.dashboard.handlers.auth;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.TokensPool;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.AppName;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import static cc.blynk.core.http.Response.*;
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
    private final TokensPool tokensPool;

    public WebLoginHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
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

        User user = userDao.getByName(email, AppName.BLYNK);

        if (user == null) {
            log.error("User not found.");
            return badRequest("User not found.");
        }

        if (!password.equals(user.pass)) {
            log.error("Wrong password for {}", user.name);
            return badRequest("Wrong password.");
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
            return badRequest("User not found.");
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

}
