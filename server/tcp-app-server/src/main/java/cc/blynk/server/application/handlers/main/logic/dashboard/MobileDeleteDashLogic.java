package cc.blynk.server.application.handlers.main.logic.dashboard;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.SharedTokenManager;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileDeleteDashLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteDashLogic.class);

    private final TimerWorker timerWorker;
    private final ReportScheduler reportScheduler;
    private final SharedTokenManager sharedTokenManager;

    public MobileDeleteDashLogic(Holder holder) {
        this.timerWorker = holder.timerWorker;
        this.reportScheduler = holder.reportScheduler;
        this.sharedTokenManager = holder.sharedTokenManager;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                MobileStateHolder state, StringMessage message) {
        int dashId = Integer.parseInt(message.body);

        deleteDash(state, dashId);
        state.user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

    private void deleteDash(MobileStateHolder state, int dashId) {
        User user = state.user;
        int index = user.profile.getDashIndexOrThrow(dashId);

        log.debug("Deleting dashboard {}.", dashId);

        DashBoard dash = user.profile.dashBoards[index];

        timerWorker.deleteTimers(state.user.orgId, state.user.email, dash);
        reportScheduler.cancelStoredFuture(user, dashId);
        sharedTokenManager.deleteSharedToken(dash.sharedToken);

        user.profile.dashBoards = ArrayUtil.remove(user.profile.dashBoards, index, DashBoard.class);
    }

}
