package cc.blynk.core.http.rest;

import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.03.17.
 */
public final class HandlerHolder {

    private static final Logger log = LogManager.getLogger(HandlerHolder.class);

    public final HandlerWrapper handler;

    public final Map<String, String> extractedParams;

    public HandlerHolder(HandlerWrapper handler, Map<String, String> extractedParams) {
        this.handler = handler;
        this.extractedParams = extractedParams;
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

}
