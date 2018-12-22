package cc.blynk.integration.model.websocket;

import cc.blynk.server.Limits;
import cc.blynk.server.core.protocol.enums.Command;
import cc.blynk.server.core.protocol.handlers.decoders.WSMessageDecoder;
import cc.blynk.server.core.protocol.model.messages.BinaryMessage;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.ResponseMessage;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.internal.QuotaLimitChecker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

import static cc.blynk.server.core.protocol.enums.Command.GET_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.core.protocol.model.messages.MessageFactory.produce;
import static cc.blynk.server.internal.WebByteBufUtil.quotaLimit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.04.18.
 */
public class WebClientAppMessageDecoder extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(WSMessageDecoder.class);

    private final GlobalStats stats;
    private final QuotaLimitChecker limitChecker;

    WebClientAppMessageDecoder(GlobalStats globalStats, Limits limits) {
        this.stats = globalStats;
        this.limitChecker = new QuotaLimitChecker(limits.userQuotaLimit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.trace("In webappdecoder: {}. Channel: {}.", msg, ctx.channel());
        if (msg instanceof BinaryWebSocketFrame) {
            try {
                ByteBuf in = ((BinaryWebSocketFrame) msg).content();

                short command = in.readUnsignedByte();
                int messageId = in.readUnsignedShort();

                if (limitChecker.quotaReached()) {
                    if (ctx.channel().isWritable()) {
                        ctx.channel().writeAndFlush(quotaLimit(messageId), ctx.voidPromise());
                    }
                    return;
                }

                MessageBase message;
                if (command == Command.RESPONSE) {
                    message = new ResponseMessage(messageId, (int) in.readUnsignedInt());
                } else {
                    int length = in.capacity() - 3;

                    ByteBuf buf = in.readSlice(length);
                    if (command == GET_ENHANCED_GRAPH_DATA) {
                        byte[] bytes = new byte[buf.readableBytes()];
                        buf.readBytes(bytes);
                        message = new BinaryMessage(messageId, command, bytes);
                    } else {
                        message = produce(messageId, command, buf.toString(StandardCharsets.UTF_8));
                    }
                }

                log.trace("Incoming websocket msg {}", message);
                stats.markWithoutGlobal(Command.WEB_SOCKETS);
                ctx.fireChannelRead(message);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof WebSocketHandshakeException) {
            log.debug("Web Socket Handshake Exception.", cause);
        }
    }

}