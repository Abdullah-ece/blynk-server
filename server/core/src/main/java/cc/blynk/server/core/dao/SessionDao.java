package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Holds session info related to specific user.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public class SessionDao {

    public final static AttributeKey<HttpSession> userSessionAttributeKey = AttributeKey.valueOf("userSession");
    public static final String SESSION_COOKIE = "session";
    private static final Logger log = LogManager.getLogger(SessionDao.class);
    public final ConcurrentMap<UserKey, Session> userSession = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, User> httpSession = new ConcurrentHashMap<>();

    //threadsafe
    public Session getOrCreateSessionByUser(UserKey key, EventLoop initialEventLoop) {
        Session group = userSession.get(key);
        //only one side came
        if (group == null) {
            Session value = new Session(initialEventLoop);
            group = userSession.putIfAbsent(key, value);
            if (group == null) {
                log.trace("Creating unique session for user: {}", key);
                return value;
            }
        }

        return group;
    }

    public String generateNewSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        httpSession.put(sessionId, user);
        return sessionId;
    }

    public void deleteHttpSession(String sessionId) {
        httpSession.remove(sessionId);
    }

    public boolean isValid(Cookie cookie) {
        return cookie.name().equals(SESSION_COOKIE);
    }

    public HttpSession getUserFromCookie(FullHttpRequest request) {
        String cookieString = request.headers().get(HttpHeaderNames.COOKIE);

        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    if (isValid(cookie)) {
                        String token = cookie.value();
                        User user = httpSession.get(token);
                        if (user == null) {
                            return null;
                        }
                        return new HttpSession(user, token);
                    }
                }
            }
        }

        return null;
    }
}