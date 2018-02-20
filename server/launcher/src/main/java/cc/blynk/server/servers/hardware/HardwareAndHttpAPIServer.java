package cc.blynk.server.servers.hardware;

import cc.blynk.core.http.handlers.NoMatchHandler;
import cc.blynk.core.http.handlers.UploadHandler;
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
import cc.blynk.server.api.http.handlers.LetsEncryptHandler;
import cc.blynk.server.api.websockets.handlers.WebSocketHandler;
import cc.blynk.server.api.websockets.handlers.WebSocketWrapperEncoder;
import cc.blynk.server.core.protocol.handlers.decoders.MessageDecoder;
import cc.blynk.server.core.protocol.handlers.encoders.MessageEncoder;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.handlers.common.AlreadyLoggedHandler;
import cc.blynk.server.handlers.common.HardwareNotLoggedHandler;
import cc.blynk.server.hardware.handlers.hardware.HardwareChannelStateHandler;
import cc.blynk.server.hardware.handlers.hardware.auth.HardwareLoginHandler;
import cc.blynk.server.servers.BaseServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

import static cc.blynk.utils.StringUtils.WEBSOCKET_PATH;
import static cc.blynk.utils.properties.ServerProperties.STATIC_FILES_FOLDER;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/12/2015.
 */
public class HardwareAndHttpAPIServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public HardwareAndHttpAPIServer(Holder holder) {
        super(holder.props.getProperty("listen.address"),
                holder.props.getIntProperty("http.port"), holder.transportTypeHolder);

        final LetsEncryptHandler letsEncryptHandler = new LetsEncryptHandler(holder.sslContextHolder.contentHolder);

        final HardwareLoginHandler hardwareLoginHandler = new HardwareLoginHandler(holder, port);
        final HardwareChannelStateHandler hardwareChannelStateHandler =
                new HardwareChannelStateHandler(holder);
        final AlreadyLoggedHandler alreadyLoggedHandler = new AlreadyLoggedHandler();
        final int maxWebLength = holder.limits.webRequestMaxSize;
        final int hardTimeoutSecs = holder.limits.hardwareIdleTimeout;

        String rootPath = holder.props.getAdminRootPath();
        String jarPath = holder.props.jarPath;

        WebLoginHandler webLoginHandler = new WebLoginHandler(holder, rootPath);
        AuthCookieHandler authCookieHandler = new AuthCookieHandler(holder.sessionDao);
        AccountHandler accountHandler = new AccountHandler(holder, rootPath);
        DevicesHandler devicesHandler = new DevicesHandler(holder, rootPath);
        DataHandler dataHandler = new DataHandler(holder, rootPath);
        ProductHandler productHandler = new ProductHandler(holder, rootPath);
        OrganizationHandler organizationHandler = new OrganizationHandler(holder, rootPath);
        ExternalAPIHandler externalAPIHandler = new ExternalAPIHandler(holder, "/external/api");

        GlobalStats stats = holder.stats;

        //http API handlers
        NoMatchHandler noMatchHandler = new NoMatchHandler();

        BaseWebSocketUnificator baseWebSocketUnificator = new BaseWebSocketUnificator() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                FullHttpRequest req = (FullHttpRequest) msg;
                String uri = req.uri();

                log.debug("In http and websocket unificator handler.");
                if (uri.startsWith(WEBSOCKET_PATH)) {
                    initWebSocketPipeline(ctx, WEBSOCKET_PATH);
                } else {
                    initHttpPipeline(ctx);
                }

                ctx.fireChannelRead(msg);
            }

            private void initHttpPipeline(ChannelHandlerContext ctx) {
                ctx.pipeline()
                        .addLast(letsEncryptHandler)
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
                if (log.isTraceEnabled()) {
                    log.trace("Initialized http pipeline. {}", ctx.pipeline().names());
                }
            }

            private void initWebSocketPipeline(ChannelHandlerContext ctx, String websocketPath) {
                ChannelPipeline pipeline = ctx.pipeline();

                //websockets specific handlers
                pipeline.addFirst("WSIdleStateHandler", new IdleStateHandler(hardTimeoutSecs, hardTimeoutSecs, 0))
                        .addLast("WSChannelState", hardwareChannelStateHandler)
                        .addLast("WSWebSocketServerProtocolHandler",
                        new WebSocketServerProtocolHandler(websocketPath, true))
                        .addLast("WSWebSocket", new WebSocketHandler(stats))
                        .addLast("WSMessageDecoder", new MessageDecoder(stats))
                        .addLast("WSSocketWrapper", new WebSocketWrapperEncoder())
                        .addLast("WSMessageEncoder", new MessageEncoder(stats))
                        .addLast("WSLogin", hardwareLoginHandler)
                        .addLast("WSNotLogged", new HardwareNotLoggedHandler());
                pipeline.remove(ExternalAPIHandler.class);
                pipeline.remove(this);
                if (log.isTraceEnabled()) {
                    log.trace("Initialized hardware websocket pipeline. {}", ctx.pipeline().names());
                }
            }
        };

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new BaseHttpAndBlynkUnificationHandler() {
                            @Override
                            public ChannelPipeline buildHttpPipeline(ChannelPipeline pipeline) {
                                log.trace("HTTP connection detected.", pipeline.channel());
                                return pipeline
                                        .addLast("HttpServerCodec", new HttpServerCodec())
                                        .addLast("HttpServerKeepAlive", new HttpServerKeepAliveHandler())
                                        .addLast("HttpObjectAggregator", new HttpObjectAggregator(maxWebLength, true))
                                        .addLast(externalAPIHandler)
                                        .addLast("HttpWebSocketUnificator", baseWebSocketUnificator);
                            }

                            @Override
                            public ChannelPipeline buildBlynkPipeline(ChannelPipeline pipeline) {
                                log.trace("Blynk protocol connection detected.", pipeline.channel());
                                return pipeline
                                        .addFirst("H_IdleStateHandler",
                                                new IdleStateHandler(hardTimeoutSecs, hardTimeoutSecs, 0))
                                        .addLast("H_ChannelState", hardwareChannelStateHandler)
                                        .addLast("H_MessageDecoder", new MessageDecoder(holder.stats))
                                        .addLast("H_MessageEncoder", new MessageEncoder(holder.stats))
                                        .addLast("H_Login", hardwareLoginHandler)
                                        .addLast("H_NotLogged", new HardwareNotLoggedHandler())
                                        .addLast("H_AlreadyLogged", alreadyLoggedHandler);
                            }
                        }
                );
            }
        };
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    protected String getServerName() {
        return "HTTP API and WebSockets";
    }

    @Override
    public void close() {
        System.out.println("Shutting down HTTP API and WebSockets server...");
        super.close();
    }

}
