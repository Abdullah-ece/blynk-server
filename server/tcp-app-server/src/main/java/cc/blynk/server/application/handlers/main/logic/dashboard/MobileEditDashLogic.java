package cc.blynk.server.application.handlers.main.logic.dashboard;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.workers.timer.TimerWorker;
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
public final class MobileEditDashLogic {

    private static final Logger log = LogManager.getLogger(MobileEditDashLogic.class);

    private MobileEditDashLogic() {
    }

    //todo should accept only dash info and ignore widgets. should be fixed after migration
    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        String dashString = message.body;

        if (dashString.isEmpty()) {
            throw new JsonException("Income create dash message is empty.");
        }

        if (dashString.length() > holder.limits.profileSizeLimitBytes) {
            throw new JsonException("User dashboard is larger then limit.");
        }

        log.debug("Trying to parse user dash : {}", dashString);
        DashBoard updatedDash = JsonParser.parseDashboard(dashString, message.id);

        if (updatedDash == null) {
            throw new JsonException("Project parsing error.");
        }

        log.debug("Saving dashboard.");

        User user = state.user;

        DashBoard existingDash = user.profile.getDashByIdOrThrow(updatedDash.id);

        TimerWorker timerWorker = holder.timerWorker;
        timerWorker.deleteTimers(state.user.orgId, state.user.email, existingDash);
        updatedDash.addTimers(timerWorker, state.user.orgId, state.user.email);

        existingDash.updateFields(updatedDash);

        user.lastModifiedTs = existingDash.updatedAt;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
