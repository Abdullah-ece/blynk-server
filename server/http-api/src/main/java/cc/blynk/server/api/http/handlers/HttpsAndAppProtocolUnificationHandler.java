package cc.blynk.server.api.http.handlers;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.HttpAndWebSocketUnificatorHandler;
import cc.blynk.server.application.handlers.main.AppChannelStateHandler;
import cc.blynk.server.application.handlers.main.auth.AppLoginHandler;
import cc.blynk.server.application.handlers.main.auth.GetServerHandler;
import cc.blynk.server.application.handlers.main.auth.RegisterHandler;
import cc.blynk.server.application.handlers.sharing.auth.AppShareLoginHandler;
import cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler;
import cc.blynk.server.core.protocol.handlers.decoders.AppMessageDecoder;
import cc.blynk.server.core.protocol.handlers.encoders.AppMessageEncoder;
import cc.blynk.server.handlers.common.UserNotLoggedHandler;
import cc.blynk.utils.HttpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.01.18.
 */
public class HttpsAndAppProtocolUnificationHandler extends ByteToMessageDecoder implements DefaultExceptionHandler {

    private static final Logger log = LogManager.getLogger(HttpsAndAppProtocolUnificationHandler.class);

    private final Holder holder;
    private final AppChannelStateHandler appChannelStateHandler;
    private final RegisterHandler registerHandler;
    private final AppLoginHandler appLoginHandler;
    private final AppShareLoginHandler appShareLoginHandler;
    private final UserNotLoggedHandler userNotLoggedHandler;
    private final GetServerHandler getServerHandler;

    private final HttpAndWebSocketUnificatorHandler httpAndWebSocketUnificatorHandler;

    public HttpsAndAppProtocolUnificationHandler(Holder holder,
                                                 AppChannelStateHandler appChannelStateHandler,
                                                 RegisterHandler registerHandler,
                                                 AppLoginHandler appLoginHandler,
                                                 AppShareLoginHandler appShareLoginHandler,
                                                 UserNotLoggedHandler userNotLoggedHandler,
                                                 GetServerHandler getServerHandler,
                                                 HttpAndWebSocketUnificatorHandler httpAndWebSocketUnificatorHandler) {
        this.holder = holder;
        this.appChannelStateHandler = appChannelStateHandler;
        this.registerHandler = registerHandler;
        this.appLoginHandler = appLoginHandler;
        this.appShareLoginHandler = appShareLoginHandler;
        this.userNotLoggedHandler = userNotLoggedHandler;
        this.getServerHandler = getServerHandler;

        this.httpAndWebSocketUnificatorHandler = httpAndWebSocketUnificatorHandler;

        log.debug("app.socket.idle.timeout = 600 for new protocol");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Will use the first 4 bytes to detect a protocol.
        if (in.readableBytes() < 4) {
            return;
        }

        int readerIndex = in.readerIndex();
        long httpHeader4Bytes = in.getUnsignedInt(readerIndex);

        ChannelPipeline pipeline = ctx.pipeline();
        buildPipeline(pipeline, httpHeader4Bytes).remove(this);
    }

    private ChannelPipeline buildPipeline(ChannelPipeline pipeline, long httpHeader4Bytes) {
        if (HttpUtil.isHttp(httpHeader4Bytes)) {
            return buildHTTPipeline(pipeline);
        }
        return buildBlynkPipeline(pipeline);
    }

    private ChannelPipeline buildHTTPipeline(ChannelPipeline pipeline) {
        log.trace("HTTPS connection detected.", pipeline.channel());
        return pipeline
                .addLast("HttpsServerCodec", new HttpServerCodec())
                .addLast("HttpsServerKeepAlive", new HttpServerKeepAliveHandler())
                .addLast("HttpsObjectAggregator",
                        new HttpObjectAggregator(holder.limits.webRequestMaxSize, true))
                .addLast("HttpChunkedWrite", new ChunkedWriteHandler())
                .addLast("HttpUrlMapper", httpAndWebSocketUnificatorHandler.urlReWriterHandler)
                .addLast("HttpStaticFile", httpAndWebSocketUnificatorHandler.staticFileHandler)
                .addLast(new HttpContentCompressor())
                .addLast(httpAndWebSocketUnificatorHandler.externalAPIHandler)
                .addLast("HttpsWebSocketUnificator", httpAndWebSocketUnificatorHandler);
    }

    private ChannelPipeline buildBlynkPipeline(ChannelPipeline pipeline) {
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleUnexpectedException(ctx, cause);
    }
}
