package cc.blynk.server.application.handlers.main.logic.dashboard;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.StringUtils;
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
public final class MobileCreateDashLogic {

    private static final Logger log = LogManager.getLogger(MobileCreateDashLogic.class);

    private final TimerWorker timerWorker;
    private final int profileSizeLimitBytes;
    private final int dashboardsLimit;

    public MobileCreateDashLogic(Holder holder) {
        this.timerWorker = holder.timerWorker;
        this.profileSizeLimitBytes = holder.limits.profileSizeLimitBytes;
        this.dashboardsLimit = holder.limits.dashboardsLimit;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                MobileStateHolder state, StringMessage message) {
        boolean generateTokensForDevices = true;
        final String dashString;
        if (message.body.startsWith("no_token")) {
            generateTokensForDevices = false;
            dashString = StringUtils.split2(message.body)[1];
        } else {
            dashString = message.body;
        }

        if (dashString == null || dashString.isEmpty()) {
            throw new JsonException("Income create dash message is empty.");
        }

        if (dashString.length() > profileSizeLimitBytes) {
            throw new JsonException("User dashboard is larger then limit.");
        }

        log.debug("Trying to parse user newDash : {}", dashString);
        DashBoard newDash = JsonParser.parseDashboard(dashString, message.id);

        User user = state.user;
        if (user.profile.dashBoards.length >= dashboardsLimit) {
            throw new JsonException("Dashboards limit reached.");
        }

        for (DashBoard dashBoard : user.profile.dashBoards) {
            if (dashBoard.id == newDash.id) {
                throw new JsonException("Dashboard already exists.");
            }
        }

        log.info("Creating new dashboard.");

        if (newDash.createdAt == 0) {
            newDash.createdAt = System.currentTimeMillis();
        }

        user.profile.dashBoards = ArrayUtil.add(user.profile.dashBoards, newDash, DashBoard.class);
        user.lastModifiedTs = System.currentTimeMillis();

        newDash.addTimers(timerWorker, state.user.orgId, state.user.email);

        if (!generateTokensForDevices) {
            newDash.eraseWidgetValues();
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
