package cc.blynk.server.http.web;

import cc.blynk.core.http.handlers.NoMatchHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * Utility handler used to define what protocol should be handled
 * on same port : http or websockets.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.02.17.
 */
@ChannelHandler.Sharable
public class HttpAndWebSocketUnificatorHandler extends ChannelInboundHandlerAdapter implements DefaultExceptionHandler {

    private final String rootPath;
    private final AuthCookieHandler authCookieHandler;

    private final NoMatchHandler noMatchHandler;

    private final WebLoginHandler webLoginHandler;
    private final AccountHandler accountHandler;
    private final DevicesHandler devicesHandler;
    private final ProductHandler productHandler;
    private final OrganizationHandler organizationHandler;

    public HttpAndWebSocketUnificatorHandler(Holder holder, String rootPath) {
        this.rootPath = rootPath;

        this.noMatchHandler = new NoMatchHandler();

        //admin API handlers
        this.webLoginHandler = new WebLoginHandler(holder, rootPath);
        this.authCookieHandler = new AuthCookieHandler(holder.sessionDao);
        this.accountHandler = new AccountHandler(holder, rootPath);
        this.devicesHandler = new DevicesHandler(holder, rootPath);
        this.productHandler = new ProductHandler(holder, rootPath);
        this.organizationHandler = new OrganizationHandler(holder, rootPath);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        initUserPipeline(ctx);
        ctx.fireChannelRead(msg);
    }

    private void initUserPipeline(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();

        pipeline.addLast(webLoginHandler);
        pipeline.addLast(authCookieHandler);

        pipeline.addLast(new UploadHandler(rootPath + "/upload"));
        pipeline.addLast(accountHandler);
        pipeline.addLast(devicesHandler);
        pipeline.addLast(productHandler);
        pipeline.addLast(organizationHandler);

        pipeline.addLast(noMatchHandler);
        pipeline.remove(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }
}
