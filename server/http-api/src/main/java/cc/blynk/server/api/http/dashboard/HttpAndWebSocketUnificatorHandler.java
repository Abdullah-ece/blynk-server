package cc.blynk.server.http.web;

import cc.blynk.core.http.handlers.NoMatchHandler;
import cc.blynk.core.http.handlers.StaticFile;
import cc.blynk.core.http.handlers.StaticFileEdsWith;
import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.core.http.handlers.UploadHandler;
import cc.blynk.core.http.handlers.url.UrlMapper;
import cc.blynk.core.http.handlers.url.UrlReWriterHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.api.websockets.handlers.WebSocketHandler;
import cc.blynk.server.api.websockets.handlers.WebSocketWrapperEncoder;
import cc.blynk.server.api.websockets.handlers.WebSocketsGenericLoginHandler;
import cc.blynk.server.core.dao.CSVGenerator;
import cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler;
import cc.blynk.server.core.protocol.handlers.decoders.MessageDecoder;
import cc.blynk.server.core.protocol.handlers.encoders.MessageEncoder;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.UrlStartWithMapper;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

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

    private final AuthCookieHandler authCookieHandler;

    private final NoMatchHandler noMatchHandler;

    private final WebLoginHandler webLoginHandler;
    private final AccountHandler accountHandler;
    private final DevicesHandler devicesHandler;
    private final DataHandler dataHandler;
    private final ProductHandler productHandler;
    private final OrganizationHandler organizationHandler;

    private final ServerProperties props;
    private final GlobalStats stats;
    private final WebSocketsGenericLoginHandler genericLoginHandler;
    public final ExternalAPIHandler externalAPIHandler;
    public final UrlReWriterHandler urlReWriterHandler;
    public final StaticFileHandler staticFileHandler;

    public HttpAndWebSocketUnificatorHandler(Holder holder, int port, String rootPath) {
        this.noMatchHandler = new NoMatchHandler();

        this.stats = holder.stats;
        this.genericLoginHandler = new WebSocketsGenericLoginHandler(holder, port);

        this.webLoginHandler = new WebLoginHandler(holder, rootPath);
        this.authCookieHandler = new AuthCookieHandler(holder.sessionDao);
        this.accountHandler = new AccountHandler(holder, rootPath);
        this.devicesHandler = new DevicesHandler(holder, rootPath);
        this.dataHandler = new DataHandler(holder, rootPath);
        this.productHandler = new ProductHandler(holder, rootPath);
        this.organizationHandler = new OrganizationHandler(holder, rootPath);

        this.externalAPIHandler = new ExternalAPIHandler(holder, "/external/api");
        this.urlReWriterHandler = new UrlReWriterHandler(
            new UrlMapper("/favicon.ico", "/static/favicon.ico"),
            new UrlMapper("/", "/static/index.html"),
            new UrlStartWithMapper("/dashboard", "/static/index.html")
        );
        this.staticFileHandler = new StaticFileHandler(holder.props,
            new StaticFile("/static"),
            new StaticFileEdsWith(CSVGenerator.CSV_DIR, ".csv.gz")
        );

        this.props = holder.props;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        String uri = req.uri();

        if (uri.startsWith(StringUtils.WEBSOCKET_PATH)) {
            initWebSocketPipeline(ctx, StringUtils.WEBSOCKET_PATH);
        } else {
            initUserPipeline(ctx);
        }

        ctx.fireChannelRead(msg);
    }

    private void initUserPipeline(ChannelHandlerContext ctx) {
        ctx.pipeline()
        .addLast(webLoginHandler)
        .addLast(authCookieHandler)
        .addLast(new UploadHandler(props.jarPath, "/api/upload", "/" + ServerProperties.STATIC_FILES_FOLDER))
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
        pipeline.remove(HttpObjectAggregator.class);
        pipeline.remove(HttpServerKeepAliveHandler.class);
        pipeline.remove(ExternalAPIHandler.class);

        pipeline.remove(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }
}
