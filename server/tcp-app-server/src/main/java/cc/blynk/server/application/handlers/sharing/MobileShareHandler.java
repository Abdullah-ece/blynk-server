package cc.blynk.server.application.handlers.sharing;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.MobileLogicHolder;
import cc.blynk.server.application.handlers.main.logic.LoadSharedProfileGzippedLogic;
import cc.blynk.server.application.handlers.main.logic.MobileAddPushLogic;
import cc.blynk.server.application.handlers.main.logic.MobileLogoutLogic;
import cc.blynk.server.application.handlers.sharing.auth.MobileShareStateHolder;
import cc.blynk.server.application.handlers.sharing.logic.MobileShareHardwareLogic;
import cc.blynk.server.common.JsonBasedSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.stats.GlobalStats;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.DASH_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.LOGOUT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_PUSH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.core.protocol.enums.Command.PING;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class MobileShareHandler extends JsonBasedSimpleChannelInboundHandler<StringMessage, MobileShareStateHolder> {

    public final MobileShareStateHolder state;
    private final GlobalStats stats;
    private final MobileLogicHolder mobileLogicHolder;

    private final MobileShareHardwareLogic hardwareApp;

    public MobileShareHandler(Holder holder, MobileLogicHolder mobileLogicHolder, MobileShareStateHolder state) {
        super(StringMessage.class);
        this.state = state;
        this.stats = holder.stats;
        this.mobileLogicHolder = mobileLogicHolder;

        //holds state
        this.hardwareApp = new MobileShareHardwareLogic(holder);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.stats.incrementMobileStat();
        switch (msg.command) {
            case HARDWARE:
                hardwareApp.messageReceived(ctx, state, msg);
                break;
            case MOBILE_LOAD_PROFILE_GZIPPED:
                LoadSharedProfileGzippedLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_ADD_PUSH_TOKEN:
                MobileAddPushLogic.messageReceived(ctx, state, msg);
                break;
            case GET_SUPERCHART_DATA:
                mobileLogicHolder.mobileGetSuperChartDataLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICES:
                mobileLogicHolder.mobileGetOrgDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case DEVICE_SYNC:
                mobileLogicHolder.mobileDeviceSyncLogic.messageReceived(ctx, msg);
                break;
            case DASH_SYNC:
                mobileLogicHolder.mobileDashSyncLogic.messageReceived(ctx, state, msg);
                break;
            case LOGOUT :
                MobileLogoutLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_DELETE_DEVICE_DATA:
                mobileLogicHolder.mobileDeleteOrgDeviceDataLogic.messageReceived(ctx, state, msg);
                break;
        }
    }

    @Override
    public MobileShareStateHolder getState() {
        return state;
    }
}
