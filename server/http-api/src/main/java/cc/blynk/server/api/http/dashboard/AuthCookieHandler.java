package cc.blynk.server.http.web;

import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.core.http.Response.unauthorized;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.05.16.
 */
@ChannelHandler.Sharable
public class AuthCookieHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(AuthCookieHandler.class);

    private final SessionDao sessionDao;

    public AuthCookieHandler(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpSession httpSession = sessionDao.getUserFromCookie(request);

            if (httpSession == null) {
                log.error("User is not logged.", request);
                try {
                    ctx.writeAndFlush(unauthorized());
                } finally {
                    request.release();
                }
                return;
            } else {
                ctx.channel().attr(SessionDao.userSessionAttributeKey).set(httpSession);
            }

        }
        super.channelRead(ctx, msg);
    }

}
