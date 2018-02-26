package cc.blynk.server.application.handlers.main;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.WebAppStateHolder;
import cc.blynk.server.application.handlers.main.logic.WebAppHardwareLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.StateHolderBase;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.handlers.BaseSimpleChannelInboundHandler;
import cc.blynk.server.handlers.common.PingLogic;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.PING;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class WebAppHandler extends BaseSimpleChannelInboundHandler<StringMessage> {

    public final WebAppStateHolder state;
    private final WebAppHardwareLogic webAppHardwareLogic;

    private final GlobalStats stats;

    public WebAppHandler(Holder holder, WebAppStateHolder state) {
        super(StringMessage.class, holder.limits);
        this.webAppHardwareLogic = new WebAppHardwareLogic(holder);

        this.state = state;
        this.stats = holder.stats;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.stats.incrementAppStat();
        switch (msg.command) {
            case HARDWARE :
                webAppHardwareLogic.messageReceived(ctx, state, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;
        }
    }

    @Override
    public StateHolderBase getState() {
        return state;
    }
}
