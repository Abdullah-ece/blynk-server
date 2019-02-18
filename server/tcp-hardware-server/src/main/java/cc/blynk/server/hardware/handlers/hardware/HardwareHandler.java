package cc.blynk.server.hardware.handlers.hardware;

import cc.blynk.server.Holder;
import cc.blynk.server.common.BaseSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.core.protocol.exceptions.BaseServerException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.hardware.handlers.hardware.logic.HardwareLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.HardwareSyncLogic;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleBaseServerException;
import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleGeneralException;
import static cc.blynk.server.internal.CommonByteBufUtil.alreadyRegistered;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 29.07.15.
 */
public final class HardwareHandler extends BaseSimpleChannelInboundHandler<StringMessage, HardwareStateHolder> {

    private final HardwareStateHolder state;
    private final HardwareLogicHolder hardwareLogicHolder;
    private final HardwareLogic hardware;

    public HardwareHandler(Holder holder, HardwareLogicHolder hardwareLogicHolder, HardwareStateHolder stateHolder) {
        super(StringMessage.class);
        this.state = stateHolder;
        this.hardwareLogicHolder = hardwareLogicHolder;

        this.hardware = new HardwareLogic(holder);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (type.isInstance(msg)) {
            try {
                messageReceived(ctx, (StringMessage) msg);
            } catch (NumberFormatException nfe) {
                log.debug("Error parsing number. {}", nfe.getMessage());
                ctx.writeAndFlush(illegalCommand(getMsgId(msg)), ctx.voidPromise());
            } catch (BaseServerException bse) {
                handleBaseServerException(ctx, bse, getMsgId(msg));
            } catch (Exception e) {
                handleGeneralException(ctx, e);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        switch (msg.command) {
            case HARDWARE:
                hardware.messageReceived(ctx, state, msg);
                break;
            case HARDWARE_LOG_EVENT:
                hardwareLogicHolder.hardwareLogEventLogic.messageReceived(ctx, state, msg);
                break;
            case PING:
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case HARDWARE_SYNC:
                HardwareSyncLogic.messageReceived(ctx, state, msg);
                break;
            case BLYNK_INTERNAL:
                hardwareLogicHolder.blynkInternalLogic.messageReceived(ctx, state, msg);
                break;
            case SET_WIDGET_PROPERTY:
                hardwareLogicHolder.setWidgetPropertyLogic.messageReceived(ctx, state, msg);
                break;
            //may when firmware is bad written
            case LOGIN:
            case HARDWARE_LOGIN:
                if (ctx.channel().isWritable()) {
                    ctx.writeAndFlush(alreadyRegistered(msg.id), ctx.voidPromise());
                }
                break;
        }
    }

    @Override
    public HardwareStateHolder getState() {
        return state;
    }
}
