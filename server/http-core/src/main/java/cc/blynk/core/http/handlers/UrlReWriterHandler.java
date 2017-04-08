package cc.blynk.core.http.handlers;

import cc.blynk.utils.UrlMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.05.16.
 */
@ChannelHandler.Sharable
public class UrlReWriterHandler extends ChannelInboundHandlerAdapter {

    private final UrlMapper[] mappers;

    public UrlReWriterHandler(UrlMapper... mappers) {
        this.mappers = mappers;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            String mapTo = isMatch(request.uri());
            if (mapTo != null) {
                request.setUri(mapTo);
            }
        }

        super.channelRead(ctx, msg);
    }

    private String isMatch(String uri) {
        for (UrlMapper urlMapper : mappers) {
            if (urlMapper.isMatch(uri)) {
                return urlMapper.mapTo(uri);
            }
        }
        return null;
    }

}
