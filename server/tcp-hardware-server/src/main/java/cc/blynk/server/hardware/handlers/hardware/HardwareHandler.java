package cc.blynk.server.hardware.handlers.hardware;

import cc.blynk.server.Holder;
import cc.blynk.server.common.BaseSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.core.protocol.exceptions.BaseServerException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.hardware.handlers.hardware.logic.BridgeLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.HardwareLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.HardwareSyncLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.MailLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.PushLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.SmsLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.TwitLogic;
import cc.blynk.server.hardware.internal.BridgeForwardMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.core.protocol.enums.Command.BRIDGE;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.PUSH_NOTIFICATION;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.server.core.protocol.enums.Command.SMS;
import static cc.blynk.server.core.protocol.enums.Command.TWEET;
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
    private final Holder holder;
    private final HardwareLogicHolder hardwareLogicHolder;
    private final HardwareLogic hardware;

    //this is rare handlers, most of users don't use them, so lazy init it.
    private BridgeLogic bridge;
    private TwitLogic tweet;
    private SmsLogic sms;
    private MailLogic mailLogic;
    private PushLogic pushLogic;

    public HardwareHandler(Holder holder, HardwareLogicHolder hardwareLogicHolder, HardwareStateHolder stateHolder) {
        super(StringMessage.class);
        this.state = stateHolder;
        this.holder = holder;
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
            case BRIDGE:
                if (bridge == null) {
                    this.bridge = new BridgeLogic(holder);
                }
                bridge.messageReceived(ctx, state, msg);
                break;
            case EMAIL:
                if (this.mailLogic == null) {
                    this.mailLogic = new MailLogic(holder);
                }
                this.mailLogic.messageReceived(ctx, state, msg);
                break;
            case PUSH_NOTIFICATION:
                if (this.pushLogic == null) {
                    this.pushLogic = new PushLogic(holder);
                }
                pushLogic.messageReceived(ctx, state, msg);
                break;
            case TWEET:
                if (tweet == null) {
                    this.tweet = new TwitLogic(holder);
                }
                tweet.messageReceived(ctx, state, msg);
                break;
            case SMS:
                if (sms == null) {
                    this.sms = new SmsLogic(holder);
                }
                sms.messageReceived(ctx, state, msg);
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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof BridgeForwardMessage) {
            var bridgeForwardMessage = (BridgeForwardMessage) evt;
            var tokenValue = bridgeForwardMessage.tokenValue;
            try {
                hardware.messageReceived(ctx, bridgeForwardMessage.message, bridgeForwardMessage.orgId,
                       tokenValue.device);
            } catch (NumberFormatException nfe) {
                log.debug("Error parsing number. {}", nfe.getMessage());
                ctx.writeAndFlush(illegalCommand(bridgeForwardMessage.message.id), ctx.voidPromise());
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    @Override
    public HardwareStateHolder getState() {
        return state;
    }
}
