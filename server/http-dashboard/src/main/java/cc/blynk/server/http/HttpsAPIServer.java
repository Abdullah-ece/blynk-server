package cc.blynk.server.http;

import cc.blynk.core.http.handlers.StaticFile;
import cc.blynk.core.http.handlers.StaticFileEdsWith;
import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.core.http.handlers.UrlReWriterHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.handlers.LetsEncryptHandler;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.dao.CSVGenerator;
import cc.blynk.server.http.web.ExternalAPIHandler;
import cc.blynk.server.http.web.HttpAndWebSocketUnificatorHandler;
import cc.blynk.utils.UrlMapper;
import cc.blynk.utils.UrlStartWithMapper;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/12/2015.
 */
public class HttpsAPIServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public HttpsAPIServer(Holder holder) {
        super(holder.props.getProperty("listen.address"),
                holder.props.getIntProperty("https.port"), holder.transportTypeHolder);

        HttpAndWebSocketUnificatorHandler httpAndWebSocketUnificatorHandler =
                new HttpAndWebSocketUnificatorHandler(holder, port, "/api");

        ExternalAPIHandler externalAPILogic = new ExternalAPIHandler(holder, "/external/api");

        UrlReWriterHandler favIconUrlRewriter = new UrlReWriterHandler(
                new UrlMapper("/favicon.ico", "/static/favicon.ico"),
                new UrlMapper("/", "/static/index.html"),
                new UrlStartWithMapper("/dashboard", "/static/index.html"));
        StaticFileHandler staticFileHandler = new StaticFileHandler(holder.props, new StaticFile("/static"),
                new StaticFileEdsWith(CSVGenerator.CSV_DIR, ".csv.gz"));

        LetsEncryptHandler letsEncryptHandler = new LetsEncryptHandler(holder.sslContextHolder.contentHolder);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                .addLast("HttpsSslContext", holder.sslContextHolder.sslCtx.newHandler(ch.alloc()))
                .addLast("HttpsServerCodec", new HttpServerCodec())
                .addLast("HttpsServerKeepAlive", new HttpServerKeepAliveHandler())
                .addLast("HttpsObjectAggregator", new HttpObjectAggregator(holder.limits.webRequestMaxSize, true))
                .addLast(new ChunkedWriteHandler())
                .addLast(letsEncryptHandler)
                .addLast(favIconUrlRewriter)
                .addLast(staticFileHandler)
                .addLast(new HttpContentCompressor())
                .addLast(externalAPILogic)
                .addLast("HttpsWebSocketUnificator", httpAndWebSocketUnificatorHandler);
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
