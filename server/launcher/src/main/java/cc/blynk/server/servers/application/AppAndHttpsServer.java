package cc.blynk.server.servers.application;

import cc.blynk.core.http.handlers.NoMatchHandler;
import cc.blynk.core.http.handlers.StaticFile;
import cc.blynk.core.http.handlers.StaticFileEdsWith;
import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.core.http.handlers.UploadHandler;
import cc.blynk.core.http.handlers.url.UrlMapper;
import cc.blynk.core.http.handlers.url.UrlReWriterHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.AccountHandler;
import cc.blynk.server.api.http.dashboard.AuthCookieHandler;
import cc.blynk.server.api.http.dashboard.DataHandler;
import cc.blynk.server.api.http.dashboard.DevicesHandler;
import cc.blynk.server.api.http.dashboard.ExternalAPIHandler;
import cc.blynk.server.api.http.dashboard.OrganizationHandler;
import cc.blynk.server.api.http.dashboard.ProductHandler;
import cc.blynk.server.api.http.dashboard.WebLoginHandler;
import cc.blynk.server.api.http.handlers.BaseHttpAndBlynkUnificationHandler;
import cc.blynk.server.api.http.handlers.BaseWebSocketUnificator;
import cc.blynk.server.api.websockets.handlers.WebSocketHandler;
import cc.blynk.server.api.websockets.handlers.WebSocketWrapperEncoder;
import cc.blynk.server.api.websockets.handlers.WebSocketsGenericLoginHandler;
import cc.blynk.server.application.handlers.main.AppChannelStateHandler;
import cc.blynk.server.application.handlers.main.auth.AppLoginHandler;
import cc.blynk.server.application.handlers.main.auth.GetServerHandler;
import cc.blynk.server.application.handlers.main.auth.RegisterHandler;
import cc.blynk.server.application.handlers.sharing.auth.AppShareLoginHandler;
import cc.blynk.server.core.dao.CSVGenerator;
import cc.blynk.server.core.protocol.handlers.decoders.AppMessageDecoder;
import cc.blynk.server.core.protocol.handlers.decoders.MessageDecoder;
import cc.blynk.server.core.protocol.handlers.encoders.AppMessageEncoder;
import cc.blynk.server.core.protocol.handlers.encoders.MessageEncoder;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.handlers.common.UserNotLoggedHandler;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.utils.UrlStartWithMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import static cc.blynk.utils.StringUtils.WEBSOCKET_PATH;
import static cc.blynk.utils.properties.ServerProperties.STATIC_FILES_FOLDER;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/12/2015.
 */
public class AppAndHttpsServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public AppAndHttpsServer(Holder holder) {
        super(holder.props.getProperty("listen.address"),
                holder.props.getIntProperty("https.port"), holder.transportTypeHolder);

        AppChannelStateHandler appChannelStateHandler = new AppChannelStateHandler(holder.sessionDao);
        RegisterHandler registerHandler = new RegisterHandler(holder);
        AppLoginHandler appLoginHandler = new AppLoginHandler(holder);
        AppShareLoginHandler appShareLoginHandler = new AppShareLoginHandler(holder);
        UserNotLoggedHandler userNotLoggedHandler = new UserNotLoggedHandler();
        GetServerHandler getServerHandler = new GetServerHandler(holder);

        String apiPath = holder.props.getApiPath();

        GlobalStats stats = holder.stats;
        WebSocketsGenericLoginHandler genericLoginHandler = new WebSocketsGenericLoginHandler(holder, port);

        //http API handlers
        NoMatchHandler noMatchHandler = new NoMatchHandler();

        ExternalAPIHandler externalAPIHandler = new ExternalAPIHandler(holder, "/external/api");
        UrlReWriterHandler urlReWriterHandler = new UrlReWriterHandler(
                new UrlMapper("/favicon.ico", "/static/favicon.ico"),
                new UrlMapper("/", "/static/index.html"),
                new UrlStartWithMapper("/dashboard", "/static/index.html")
        );

        String jarPath = holder.props.jarPath;

        WebLoginHandler webLoginHandler = new WebLoginHandler(holder, apiPath);
        AuthCookieHandler authCookieHandler = new AuthCookieHandler(holder.sessionDao);
        AccountHandler accountHandler = new AccountHandler(holder, apiPath);
        DevicesHandler devicesHandler = new DevicesHandler(holder, apiPath);
        DataHandler dataHandler = new DataHandler(holder, apiPath);
        ProductHandler productHandler = new ProductHandler(holder, apiPath);
        OrganizationHandler organizationHandler = new OrganizationHandler(holder, apiPath);

        BaseWebSocketUnificator baseWebSocketUnificator = new BaseWebSocketUnificator() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                FullHttpRequest req = (FullHttpRequest) msg;
                String uri = req.uri();

                log.debug("In https and websocket unificator handler.");
                if (uri.startsWith(WEBSOCKET_PATH)) {
                    initWebSocketPipeline(ctx, WEBSOCKET_PATH);
                } else {
                    initHttpPipeline(ctx);
                }

                ctx.fireChannelRead(msg);
            }

            private void initHttpPipeline(ChannelHandlerContext ctx) {
                ctx.pipeline()
                        .addLast(webLoginHandler)
                        .addLast(authCookieHandler)
                        .addLast(new UploadHandler(jarPath, "/api/upload", "/" + STATIC_FILES_FOLDER))
                        .addLast(accountHandler)
                        .addLast(devicesHandler)
                        .addLast(dataHandler)
                        .addLast(productHandler)
                        .addLast(organizationHandler)
                        .addLast(noMatchHandler)
                        .remove(this);
            }

            private void initWebSocketPipeline(ChannelHandlerContext ctx, String websocketPath) {
                ChannelPipeline pipeline = ctx.pipeline();

                //websockets specific handlers
                pipeline.addLast("WSWebSocketServerProtocolHandler",
                        new WebSocketServerProtocolHandler(websocketPath, true));
                pipeline.addLast("WSWebSocket", new WebSocketHandler(stats));
                pipeline.addLast("WSMessageDecoder", new MessageDecoder(stats));
                pipeline.addLast("WSSocketWrapper", new WebSocketWrapperEncoder());
                pipeline.addLast("WSMessageEncoder", new MessageEncoder(stats));
                pipeline.addLast("WSWebSocketGenericLoginHandler", genericLoginHandler);

                //remove static file handlers
                pipeline.remove(ChunkedWriteHandler.class);
                pipeline.remove(UrlReWriterHandler.class);
                pipeline.remove(StaticFileHandler.class);
                pipeline.remove(HttpObjectAggregator.class);
                pipeline.remove(HttpServerKeepAliveHandler.class);
                pipeline.remove(ExternalAPIHandler.class);

                pipeline.remove(this);
            }
        };

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                .addLast(holder.sslContextHolder.sslCtx.newHandler(ch.alloc()))
                .addLast(new BaseHttpAndBlynkUnificationHandler() {
                    @Override
                    public ChannelPipeline buildHttpPipeline(ChannelPipeline pipeline) {
                        log.trace("HTTPS connection detected.", pipeline.channel());
                        return pipeline
                                .addLast("HttpsServerCodec", new HttpServerCodec())
                                .addLast("HttpsServerKeepAlive", new HttpServerKeepAliveHandler())
                                .addLast("HttpsObjectAggregator",
                                        new HttpObjectAggregator(holder.limits.webRequestMaxSize, true))
                                .addLast("HttpChunkedWrite", new ChunkedWriteHandler())
                                .addLast("HttpUrlMapper", urlReWriterHandler)
                                .addLast("HttpStaticFile",
                                        new StaticFileHandler(holder.props, new StaticFile("/static"),
                                                new StaticFileEdsWith(CSVGenerator.CSV_DIR, ".csv.gz")))
                                .addLast(externalAPIHandler)
                                .addLast("HttpsWebSocketUnificator", baseWebSocketUnificator);
                    }

                    @Override
                    public ChannelPipeline buildBlynkPipeline(ChannelPipeline pipeline) {
                        log.trace("Blynk protocol connection detected.", pipeline.channel());
                        return pipeline
                                .addLast("AReadTimeout", new IdleStateHandler(600, 0, 0))
                                .addLast("AChannelState", appChannelStateHandler)
                                .addLast("AMessageDecoder", new AppMessageDecoder(holder.stats))
                                .addLast("AMessageEncoder", new AppMessageEncoder(holder.stats))
                                .addLast("AGetServer", getServerHandler)
                                .addLast("ARegister", registerHandler)
                                .addLast("ALogin", appLoginHandler)
                                .addLast("AShareLogin", appShareLoginHandler)
                                .addLast("ANotLogged", userNotLoggedHandler);
                    }
                });
            }
        };
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    protected String getServerName() {
        return "HTTPS API, WebSockets and Admin page";
    }

    @Override
    public void close() {
        System.out.println("Shutting down HTTPS API, WebSockets and Admin server...");
        super.close();
    }

}
