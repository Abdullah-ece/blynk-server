package cc.blynk.server.common;

import cc.blynk.server.core.protocol.model.messages.MessageBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleGeneralException;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class BaseSimpleChannelInboundHandler<I, T> extends ChannelInboundHandlerAdapter {

    protected static final Logger log = LogManager.getLogger(BaseSimpleChannelInboundHandler.class);

    protected final Class<I> type;

    protected BaseSimpleChannelInboundHandler(Class<I> type) {
        this.type = type;
    }

    protected static int getMsgId(Object o) {
        if (o instanceof MessageBase) {
            return ((MessageBase) o).id;
        }
        return 0;
    }

    /**
     * <strong>Please keep in mind that this method will be renamed to
     * {@code messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>
     *
     * Is called for each message of type {@link I}.
     *
     * @param ctx           the {@link ChannelHandlerContext} which this SimpleChannelInboundHandler
     *                      belongs to
     * @param msg           the message to handle
     */
    public abstract void messageReceived(ChannelHandlerContext ctx, I msg);

    public abstract T getState();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        handleGeneralException(ctx, cause);
    }
}
