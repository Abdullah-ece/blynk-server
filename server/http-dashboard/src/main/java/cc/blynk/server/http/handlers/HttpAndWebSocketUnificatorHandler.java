package cc.blynk.server.http.handlers;

import cc.blynk.core.http.handlers.NoMatchHandler;
import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.core.http.handlers.UrlReWriterHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.admin.http.logic.ConfigsLogic;
import cc.blynk.server.admin.http.logic.HardwareStatsLogic;
import cc.blynk.server.admin.http.logic.StatsLogic;
import cc.blynk.server.admin.http.logic.UsersLogic;
import cc.blynk.server.api.http.logic.HttpAPILogic;
import cc.blynk.server.api.http.logic.ResetPasswordLogic;
import cc.blynk.server.api.http.logic.ide.IDEAuthLogic;
import cc.blynk.server.api.websockets.handlers.WebSocketHandler;
import cc.blynk.server.api.websockets.handlers.WebSocketWrapperEncoder;
import cc.blynk.server.api.websockets.handlers.WebSocketsGenericLoginHandler;
import cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler;
import cc.blynk.server.core.protocol.handlers.decoders.MessageDecoder;
import cc.blynk.server.core.protocol.handlers.encoders.MessageEncoder;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.http.HttpAPIServer;
import cc.blynk.server.http.dashboard.handlers.*;
import cc.blynk.server.http.dashboard.handlers.auth.AuthCookieHandler;
import cc.blynk.server.http.dashboard.handlers.auth.WebLoginHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import static cc.blynk.core.http.Response.redirect;

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

    private final GlobalStats stats;

    private final WebSocketsGenericLoginHandler genericLoginHandler;
    private final String rootPath;
    private final AuthCookieHandler authCookieHandler;

    private final ResetPasswordLogic resetPasswordLogic;
    private final HttpAPILogic httpAPILogic;
    private final IDEAuthLogic ideAuthLogic;
    private final NoMatchHandler noMatchHandler;

    private final UsersLogic usersLogic;
    private final StatsLogic statsLogic;
    private final ConfigsLogic configsLogic;
    private final HardwareStatsLogic hardwareStatsLogic;
    private final WebLoginHandler webLoginHandler;
    private final InvitationHandler invitationHandler;
    private final AccountHandler accountHandler;
    private final ProductHandler productHandler;
    private final OrganizationHandler organizationHandler;

    public HttpAndWebSocketUnificatorHandler(Holder holder, int port, String rootPath) {
        this.stats = holder.stats;
        this.genericLoginHandler = new WebSocketsGenericLoginHandler(holder, port);
        this.rootPath = rootPath;

        //http API handlers
        this.resetPasswordLogic = new ResetPasswordLogic(holder);
        this.httpAPILogic = new HttpAPILogic(holder);
        this.ideAuthLogic = new IDEAuthLogic(holder);
        this.noMatchHandler = new NoMatchHandler();

        //admin API handlers
        this.usersLogic = new UsersLogic(holder, rootPath);
        this.statsLogic = new StatsLogic(holder, rootPath);
        this.configsLogic = new ConfigsLogic(holder, rootPath);
        this.hardwareStatsLogic = new HardwareStatsLogic(holder, rootPath);
        this.invitationHandler = new InvitationHandler(holder, rootPath);
        this.webLoginHandler = new WebLoginHandler(holder, rootPath);
        this.authCookieHandler = new AuthCookieHandler(holder.sessionDao);
        this.accountHandler = new AccountHandler(holder, rootPath);
        this.productHandler = new ProductHandler(holder, rootPath);
        this.organizationHandler = new OrganizationHandler(holder, rootPath);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final FullHttpRequest req = (FullHttpRequest) msg;
        String uri = req.uri();

        if (uri.equals("/")) {
            ctx.writeAndFlush(redirect(rootPath));
            return;
        } else if (uri.startsWith(rootPath) || uri.startsWith("/static")) {
            initUserPipeline(ctx);
        } else if (req.uri().startsWith(HttpAPIServer.WEBSOCKET_PATH)) {
            initWebSocketPipeline(ctx, HttpAPIServer.WEBSOCKET_PATH);
        } else {
            initHttpPipeline(ctx);
        }

        ctx.fireChannelRead(msg);
    }


    private void initUserPipeline(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();

        pipeline.addLast(webLoginHandler);
        pipeline.addLast(authCookieHandler);

        pipeline.addLast(new UploadLogic(rootPath + "/upload"));
        pipeline.addLast(accountHandler);
        pipeline.addLast(productHandler);
        pipeline.addLast(organizationHandler);
        pipeline.addLast(invitationHandler);
        pipeline.addLast(usersLogic);
        pipeline.addLast(statsLogic);
        pipeline.addLast(configsLogic);
        pipeline.addLast(hardwareStatsLogic);

        pipeline.addLast(resetPasswordLogic);
        pipeline.addLast(httpAPILogic);
        pipeline.addLast(noMatchHandler);
        pipeline.remove(this);
    }

    private void initHttpPipeline(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();

        pipeline.addLast(resetPasswordLogic);
        pipeline.addLast(httpAPILogic);
        pipeline.addLast(ideAuthLogic);

        pipeline.addLast(noMatchHandler);
        pipeline.remove(this);
    }

    private void initWebSocketPipeline(ChannelHandlerContext ctx, String websocketPath) {
        ChannelPipeline pipeline = ctx.pipeline();

        //websockets specific handlers
        pipeline.addLast("WSWebSocketServerProtocolHandler", new WebSocketServerProtocolHandler(websocketPath, true));
        pipeline.addLast("WSWebSocket", new WebSocketHandler(stats));
        pipeline.addLast("WSMessageDecoder", new MessageDecoder(stats));
        pipeline.addLast("WSSocketWrapper", new WebSocketWrapperEncoder());
        pipeline.addLast("WSMessageEncoder", new MessageEncoder(stats));
        pipeline.addLast("WSWebSocketGenericLoginHandler", genericLoginHandler);

        //remove static file handlers
        pipeline.remove(ChunkedWriteHandler.class);
        pipeline.remove(UrlReWriterHandler.class);
        pipeline.remove(StaticFileHandler.class);

        pipeline.remove(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }
}
