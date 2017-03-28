package cc.blynk.server.api.http.logic.business;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.FormParam;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.AppName;
import cc.blynk.server.core.model.auth.User;
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
public class AuthHandler extends BaseHttpHandler {

    //1 month
    private static final int COOKIE_EXPIRE_TIME = 30 * 60 * 60 * 24;

    private final UserDao userDao;

    public AuthHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
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
            return unauthorized();
        }

        User user = userDao.getByName(email, AppName.BLYNK);

        if (user == null) {
            log.error("User not found.");
            return unauthorized();
        }

        if (!password.equals(user.pass)) {
            log.error("Wrong password for {}", user.name);
            return unauthorized();
        }

        Response response = ok(user);

        Cookie cookie = makeDefaultSessionCookie(sessionDao.generateNewSession(user), COOKIE_EXPIRE_TIME);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));

        return response;
    }

    @POST
    @Path("/logout")
    public Response logout() {
        Response response = redirect(rootPath);
        Cookie cookie = makeDefaultSessionCookie("", 0);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
        return response;
    }

}
