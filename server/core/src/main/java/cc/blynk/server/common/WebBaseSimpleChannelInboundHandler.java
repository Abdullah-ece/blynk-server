package cc.blynk.server.common;

import cc.blynk.server.core.protocol.exceptions.BaseServerException;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.internal.WebByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class WebBaseSimpleChannelInboundHandler<I, T> extends BaseSimpleChannelInboundHandler<I, T> {

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
            } catch (JsonException bse) {
                log.debug("Error processing request. Reason : {}", bse.getMessage());
                ctx.writeAndFlush(WebByteBufUtil.json(bse.msgId, bse.getMessage()), ctx.voidPromise());
            } catch (BaseServerException bse) {
                log.debug("Error processing request. Reason : {}", bse.getMessage());
                ctx.writeAndFlush(WebByteBufUtil.json(getMsgId(msg), bse.getMessage()), ctx.voidPromise());
            } catch (Exception e) {
                log.debug("Unexpected error.", e);
                ctx.writeAndFlush(WebByteBufUtil.json(getMsgId(msg), e.getMessage()), ctx.voidPromise());
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }
}
