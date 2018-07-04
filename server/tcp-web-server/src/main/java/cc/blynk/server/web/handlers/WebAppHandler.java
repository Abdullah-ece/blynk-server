package cc.blynk.server.web.handlers;

import cc.blynk.server.Holder;
import cc.blynk.server.common.BaseSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.StateHolderBase;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.web.handlers.logic.GetAccountLogic;
import cc.blynk.server.web.handlers.logic.GetWebGraphDataLogic;
import cc.blynk.server.web.handlers.logic.ResolveWebEventLogic;
import cc.blynk.server.web.handlers.logic.TrackDeviceLogic;
import cc.blynk.server.web.handlers.logic.UpdateAccountLogic;
import cc.blynk.server.web.handlers.logic.WebAppHardwareLogic;
import cc.blynk.server.web.handlers.logic.WebCreateDeviceLogic;
import cc.blynk.server.web.handlers.logic.WebGetDeviceLogic;
import cc.blynk.server.web.handlers.logic.WebGetDevicesLogic;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.GET_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.RESOLVE_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.TRACK_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_ACCOUNT;

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
    private final ResolveWebEventLogic resolveWebEventLogic;
    private final WebCreateDeviceLogic webCreateDeviceLogic;
    private final WebGetDevicesLogic webGetDevicesLogic;
    private final WebGetDeviceLogic webGetDeviceLogic;

    private final GlobalStats stats;

    public WebAppHandler(Holder holder, WebAppStateHolder state) {
        super(StringMessage.class);
        this.webAppHardwareLogic = new WebAppHardwareLogic(holder);
        this.getWebGraphDataLogic = new GetWebGraphDataLogic(holder);
        this.resolveWebEventLogic = new ResolveWebEventLogic(holder);
        this.webCreateDeviceLogic = new WebCreateDeviceLogic(holder);
        this.webGetDevicesLogic = new WebGetDevicesLogic(holder);
        this.webGetDeviceLogic = new WebGetDeviceLogic(holder);

        this.state = state;
        this.stats = holder.stats;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.stats.incrementAppStat();
        switch (msg.command) {
            case WEB_GET_ACCOUNT:
                GetAccountLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_UPDATE_ACCOUNT:
                UpdateAccountLogic.messageReceived(ctx, state, msg);
                break;
            case HARDWARE :
                webAppHardwareLogic.messageReceived(ctx, state, msg);
                break;
            case TRACK_DEVICE :
                TrackDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case GET_ENHANCED_GRAPH_DATA :
                getWebGraphDataLogic.messageReceived(ctx, state, msg);
                break;
            case RESOLVE_EVENT :
                resolveWebEventLogic.messageReceived(ctx, state, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case WEB_CREATE_DEVICE :
                webCreateDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICES :
                webGetDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE :
                webGetDeviceLogic.messageReceived(ctx, state, msg);
                break;
        }
    }

    @Override
    public StateHolderBase getState() {
        return state;
    }
}
