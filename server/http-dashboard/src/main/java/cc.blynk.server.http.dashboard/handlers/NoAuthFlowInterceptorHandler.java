package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.server.Holder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.04.17.
 */
@ChannelHandler.Sharable
public class NoAuthFlowInterceptorHandler extends ChannelInboundHandlerAdapter {

    private final String invitationLandingUri;
    private final String invitationCreateAccUri;
    private final String resetPathLandingUri;


    public NoAuthFlowInterceptorHandler(Holder holder) {
        String host = holder.props.getProperty("reset-pass.host");
        this.invitationLandingUri = "/dashboard/invite";
        this.invitationCreateAccUri = "";
        this.resetPathLandingUri = "";
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            return;
        }

        FullHttpRequest req = (FullHttpRequest) msg;

        String uri = req.uri();
        if (uri.startsWith(invitationLandingUri)) {

        }

        ctx.fireChannelRead(req);
    }

}
