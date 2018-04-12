package cc.blynk.server.application.handlers.main;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.logic.web.GetWebGraphDataLogic;
import cc.blynk.server.application.handlers.main.logic.web.TrackDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.web.WebAppHardwareLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.StateHolderBase;
import cc.blynk.server.core.session.WebAppStateHolder;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.handlers.BaseSimpleChannelInboundHandler;
import cc.blynk.server.handlers.common.PingLogic;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.GET_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.TRACK_DEVICE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class WebAppHandler extends BaseSimpleChannelInboundHandler<StringMessage> {

    public final WebAppStateHolder state;
    private final WebAppHardwareLogic webAppHardwareLogic;
    private final GetWebGraphDataLogic getWebGraphDataLogic;

    private final GlobalStats stats;

    public WebAppHandler(Holder holder, WebAppStateHolder state) {
        super(StringMessage.class);
        this.webAppHardwareLogic = new WebAppHardwareLogic(holder);
        this.getWebGraphDataLogic = new GetWebGraphDataLogic(holder);

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
            case TRACK_DEVICE :
                TrackDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case GET_ENHANCED_GRAPH_DATA :
                getWebGraphDataLogic.messageReceived(ctx, state, msg);
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
