package cc.blynk.server.http;

import cc.blynk.core.http.handlers.StaticFile;
import cc.blynk.core.http.handlers.StaticFileEdsWith;
import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.core.http.handlers.UrlReWriterHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.dao.CSVGenerator;
import cc.blynk.server.http.dashboard.handlers.ResetPassNotLoggedHandler;
import cc.blynk.server.http.handlers.HttpAndWebSocketUnificatorHandler;
import cc.blynk.utils.SslUtil;
import cc.blynk.utils.UrlMapper;
import cc.blynk.utils.UrlStartWithMapper;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/12/2015.
 */
public class HttpsAPIServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public HttpsAPIServer(Holder holder, boolean isUnpacked) {
        super(holder.props.getProperty("listen.address"), holder.props.getIntProperty("https.port"), holder.transportTypeHolder);

        String rootPath = holder.props.getProperty("admin.rootPath", "/dashboard");

        final SslContext sslCtx = SslUtil.initSslContext(
                holder.props.getProperty("https.cert", holder.props.getProperty("server.ssl.cert")),
                holder.props.getProperty("https.key", holder.props.getProperty("server.ssl.key")),
                holder.props.getProperty("https.key.pass", holder.props.getProperty("server.ssl.key.pass")),
                SslUtil.fetchSslProvider(holder.props));

        final HttpAndWebSocketUnificatorHandler httpAndWebSocketUnificatorHandler =
                new HttpAndWebSocketUnificatorHandler(holder, port, rootPath);
        final UrlReWriterHandler favIconUrlRewriter = new UrlReWriterHandler(
                new UrlMapper("/favicon.ico", "/static/favicon.ico"),
                new UrlMapper(rootPath, "/static/index.html"),
                new UrlStartWithMapper("/dashboard#/invite", "/static/index.html"),
                new UrlStartWithMapper("/dashboard#/resetPass", "/static/index.html"));
        final StaticFileHandler staticFileHandler = new StaticFileHandler(isUnpacked, new StaticFile("/static"),
                new StaticFileEdsWith(CSVGenerator.CSV_DIR, ".csv.gz"));
        final ResetPassNotLoggedHandler resetPassNotLoggedHandler = new ResetPassNotLoggedHandler(holder, rootPath);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("HttpsSslContext", sslCtx.newHandler(ch.alloc()));
                pipeline.addLast("HttpsServerCodec", new HttpServerCodec());
                pipeline.addLast("HttpsObjectAggregator", new HttpObjectAggregator(10 * 1024 * 1024, true));
                pipeline.addLast(new ChunkedWriteHandler());
                pipeline.addLast(favIconUrlRewriter);
                pipeline.addLast(staticFileHandler);
                pipeline.addLast(resetPassNotLoggedHandler);
                pipeline.addLast("HttpsWebSocketUnificator", httpAndWebSocketUnificatorHandler);
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
