package cc.blynk.server.http;

import cc.blynk.core.http.handlers.StaticFile;
import cc.blynk.core.http.handlers.StaticFileEdsWith;
import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.core.http.handlers.UrlReWriterHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.handlers.LetsEncryptHandler;
import cc.blynk.server.api.http.logic.HttpAPILogic;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.dao.CSVGenerator;
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

    public HttpsAPIServer(Holder holder, boolean isUnpacked) {
        super(holder.props.getProperty("listen.address"), holder.props.getIntProperty("https.port"), holder.transportTypeHolder);

        String rootPath = holder.props.getProperty("admin.rootPath", "/dashboard");

        final HttpAndWebSocketUnificatorHandler httpAndWebSocketUnificatorHandler =
                new HttpAndWebSocketUnificatorHandler(holder, port, rootPath);
        final UrlReWriterHandler favIconUrlRewriter = new UrlReWriterHandler(
                new UrlMapper("/favicon.ico", "/static/favicon.ico"),
                new UrlMapper(rootPath, "/static/index.html"),
                new UrlStartWithMapper("/dashboard#/invite", "/static/index.html"),
                new UrlStartWithMapper("/dashboard#/resetPass", "/static/index.html"));
        final StaticFileHandler staticFileHandler = new StaticFileHandler(isUnpacked, new StaticFile("/static"),
                new StaticFileEdsWith(CSVGenerator.CSV_DIR, ".csv.gz"));
        final LetsEncryptHandler letsEncryptHandler = new LetsEncryptHandler(holder.sslContextHolder.contentHolder);

        final HttpAPILogic httpAPILogic = new HttpAPILogic(holder);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                .addLast("HttpsSslContext", holder.sslContextHolder.sslCtx.newHandler(ch.alloc()))
                .addLast("HttpsServerCodec", new HttpServerCodec())
                .addLast("HttpsServerKeepAlive", new HttpServerKeepAliveHandler())
                .addLast("HttpsObjectAggregator", new HttpObjectAggregator(10 * 1024 * 1024, true))
                .addLast(letsEncryptHandler)
                .addLast(new ChunkedWriteHandler())
                .addLast(favIconUrlRewriter)
                .addLast(staticFileHandler)
                .addLast(new HttpContentCompressor())
                .addLast(httpAPILogic)
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
