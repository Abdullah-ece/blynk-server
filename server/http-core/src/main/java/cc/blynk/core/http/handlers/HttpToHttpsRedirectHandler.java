package cc.blynk.core.http.handlers;

import cc.blynk.core.http.Response;
import cc.blynk.utils.properties.ServerProperties;
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
    private final String rootPath;
    private final String httpsUrl;

    public HttpToHttpsRedirectHandler(ServerProperties props) {
        this.rootPath = props.rootPath;
        this.httpsUrl = props.httpsServerUrl;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            String uri = req.uri();
            if ("/".equals(uri) || uri.startsWith(rootPath)) {
                try {
                    String redirect = httpsUrl + uri;
                    log.debug("Redirecting to {} from {} {}.", redirect, req.method().name(), uri);
                    ctx.writeAndFlush(Response.redirect(redirect), ctx.voidPromise());
                } finally {
                    ReferenceCountUtil.release(msg);
                }
            }
        }
    }

}
