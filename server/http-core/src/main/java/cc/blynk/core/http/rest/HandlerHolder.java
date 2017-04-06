package cc.blynk.core.http.rest;

import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.03.17.
 */
public class HandlerHolder {

    private static final Logger log = LogManager.getLogger(HandlerHolder.class);

    public final Handler handler;

    private final Matcher matcher;

    public HandlerHolder(Handler handler, Matcher matcher) {
        this.handler = handler;
        this.matcher = matcher;
    }

    private boolean isRestrictedAccess() {
        return handler.allowedRoleAccess != null;
    }

    public boolean hasAccess(ChannelHandlerContext ctx) {
        if (isRestrictedAccess()) {
            HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
            if (httpSession.user.role.ordinal() > handler.allowedRoleAccess.ordinal()) {
                log.error("User {} is not allowed to call {}.{}. Required {}. User has {}.",
                        httpSession.user.email, handler.handler.getClass().getSimpleName(), handler.classMethod.getName(),
                        handler.allowedRoleAccess.name(), httpSession.user.role.name());
                return false;
            }
        }

        return true;
    }

    public Map<String, String> extractParameters() {
        return handler.uriTemplate.extractParameters(matcher);
    }
}
