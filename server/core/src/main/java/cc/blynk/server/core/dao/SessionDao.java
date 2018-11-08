package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.session.CookieUtil;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    public final ConcurrentHashMap<String, Session> userSession = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> httpSession = new ConcurrentHashMap<>();

    //threadsafe
    public Session getOrCreateSessionByUser(String email, EventLoop initialEventLoop) {
        Session group = userSession.get(email);
        //only one side came
        if (group == null) {
            Session value = new Session(initialEventLoop);
            group = userSession.putIfAbsent(email, value);
            if (group == null) {
                log.trace("Creating unique session for user: {}", email);
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

    public void deleteUser(String email) {
        Session session = userSession.remove(email);
        if (session != null) {
            session.closeAll();
        }
        for (Map.Entry<String, User> entry : httpSession.entrySet()) {
            User user = entry.getValue();
            if (user.email.equals(email)) {
                httpSession.remove(entry.getKey());
                return;
            }
        }
    }

    public HttpSession getUserFromCookie(HttpRequest request) {
        Cookie cookie = CookieUtil.findCookieByName(request, SESSION_COOKIE);

        if (cookie != null) {
            String token = cookie.value();
            User user = httpSession.get(token);
            if (user != null) {
                return new HttpSession(user, token);
            }
        }

        return null;
    }

    public void close() {
        System.out.println("Closing all sockets...");
        DefaultChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        userSession.forEach((userKey, session) -> {
            allChannels.addAll(session.appChannels);
            allChannels.addAll(session.hardwareChannels);
        });
        allChannels.close().awaitUninterruptibly();
    }

    public void closeAppChannelsByUser(String email) {
        Session session = userSession.get(email);
        if (session != null) {
            for (Channel appChannel : session.appChannels) {
                appChannel.close();
            }
        }
    }
}
