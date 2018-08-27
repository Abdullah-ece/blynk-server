package cc.blynk.server.common;

import cc.blynk.server.core.protocol.exceptions.BaseServerException;
import cc.blynk.server.internal.WebByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleBaseServerException;
import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleGeneralException;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class WebBaseSimpleChannelInboundHandler<I> extends BaseSimpleChannelInboundHandler<I> {

    public WebBaseSimpleChannelInboundHandler(Class<I> type) {
        super(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (type.isInstance(msg)) {
            try {
                messageReceived(ctx, (I) msg);
            } catch (NumberFormatException nfe) {
                log.debug("Error parsing number. {}", nfe.getMessage());
                ctx.writeAndFlush(WebByteBufUtil.json(getMsgId(msg), "Error parsing number. "
                        + nfe.getMessage()), ctx.voidPromise());
            } catch (BaseServerException bse) {
                handleBaseServerException(ctx, bse, getMsgId(msg));
            } catch (Exception e) {
                handleGeneralException(ctx, e);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }
}
