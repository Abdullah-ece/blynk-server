package cc.blynk.core.http.handlers;

import cc.blynk.core.http.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 30.08.18.
 */
@ChannelHandler.Sharable
public class HttpToHttpsRedirectHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(HttpToHttpsRedirectHandler.class);
    private final String host;
    private final String rootPath;
    private final String httpsPort;

    public HttpToHttpsRedirectHandler(String host, String rootPath, String httpsPort) {
        this.host = host;
        this.rootPath = rootPath;
        this.httpsPort = httpsPort;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            String uri = req.uri();
            if (uri.startsWith(rootPath)) {
                try {
                    String portPart = (httpsPort.isEmpty() ? "" : ":" + httpsPort);
                    String redirect = "https://" + host + portPart + uri;
                    log.debug("Redirecting to {} from {} {}.", redirect, req.method().name(), uri);
                    ctx.writeAndFlush(Response.redirect(redirect), ctx.voidPromise());
                } finally {
                    ReferenceCountUtil.release(msg);
                }
            }
        }
    }

}
