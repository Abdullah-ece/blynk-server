package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.session.CookieUtil;
import cc.blynk.server.internal.StateHolderUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.AttributeKey;
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
    public final ConcurrentHashMap<Integer, Session> orgSession = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> httpSession = new ConcurrentHashMap<>();

    public Session getOrgSession(int orgId) {
        return orgSession.get(orgId);
    }

    //threadsafe
    public Session getOrCreateSessionForOrg(int orgId, EventLoop initialEventLoop) {
        Session group = orgSession.get(orgId);
        //only one side came
        if (group == null) {
            Session value = new Session(initialEventLoop);
            group = orgSession.putIfAbsent(orgId, value);
            if (group == null) {
                log.trace("Creating unique session for org: {}", orgId);
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

    public void deleteUser(int orgId, String email) {
        closeAppChannelsByUser(orgId, email);
        closeWebChannelsByUser(orgId, email);
        for (Map.Entry<String, User> entry : httpSession.entrySet()) {
            User user = entry.getValue();
            if (user.email.equals(email)) {
                httpSession.remove(entry.getKey());
                return;
            }
        }
    }

    public void closeAppChannelsByUser(int orgId, String email) {
        Session session = orgSession.get(orgId);
        if (session != null) {
            session.appChannels.forEach(channel -> {
                if (StateHolderUtil.isSameEmail(channel, email)) {
                    channel.close();
                }
            });
        }
    }

    private void closeWebChannelsByUser(int orgId, String email) {
        Session session = orgSession.get(orgId);
        if (session != null) {
            session.webChannels.forEach(channel -> {
                if (StateHolderUtil.isSameEmail(channel, email)) {
                    channel.close();
                }
            });
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

    /**
     * When user switches organization via OrgSwitch we move it to another org session.
     * This is needed to avoid lookup across all sessions, so we can take only session
     * of the specific organization and send data between them without iterating through
     * sessions of other organizations
     */
    public void moveToAnotherSession(Channel channel, int prevOrgId, int newOrgId) {
        //couldn't be null, as current user in it
        Session prevSession = orgSession.get(prevOrgId);
        //could be null, for now using the same EventLoop, but we need to use round-robin
        Session newSession = getOrCreateSessionForOrg(newOrgId, channel.eventLoop());

        //order is important here, as we use the same
        prevSession.removeWebChannel(channel);
        newSession.addWebChannel(channel);
    }

    public void close() {
        System.out.println("Closing all sockets...");
        orgSession.forEach((orgKey, session) -> {
            session.appChannels.forEach(ChannelOutboundInvoker::close);
            session.webChannels.forEach(ChannelOutboundInvoker::close);
            session.hardwareChannels.forEach(ChannelOutboundInvoker::close);
        });
    }
}
