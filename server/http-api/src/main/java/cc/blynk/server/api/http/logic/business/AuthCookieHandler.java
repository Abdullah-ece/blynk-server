package cc.blynk.server.api.http.logic.business;

import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.05.16.
 */
@ChannelHandler.Sharable
public class AuthCookieHandler extends ChannelInboundHandlerAdapter {

    private final SessionDao sessionDao;

    public AuthCookieHandler(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpSession httpSession = sessionDao.getUserFromCookie(request);

            //if (request.uri().equals("/admin/logout")) {
            //    ctx.channel().attr(SessionDao.userSessionAttributeKey).set(null);
            //} else {
                if (httpSession != null) {
                    ctx.channel().attr(SessionDao.userSessionAttributeKey).set(httpSession);
                }
            //}
        }
        super.channelRead(ctx, msg);
    }

}
