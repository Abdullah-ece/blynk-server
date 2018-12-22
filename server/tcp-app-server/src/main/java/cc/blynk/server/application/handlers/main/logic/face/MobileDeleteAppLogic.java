package cc.blynk.server.application.handlers.main.logic.face;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileDeleteAppLogic {

    private final TimerWorker timerWorker;

    public MobileDeleteAppLogic(Holder holder) {
        this.timerWorker = holder.timerWorker;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        var id = message.body;

        var user = state.user;

        var existingAppIndex = user.profile.getAppIndexById(id);

        if (existingAppIndex == -1) {
            throw new JsonException("App with passed is not exists.");
        }

        var projectIds = user.profile.apps[existingAppIndex].projectIds;

        var result = new ArrayList<DashBoard>();
        for (DashBoard dash : user.profile.dashBoards) {
            if (ArrayUtil.contains(projectIds, dash.id)) {
                timerWorker.deleteTimers(state.user.orgId, state.user.email, dash);
            } else {
                result.add(dash);
            }
        }

        user.profile.dashBoards = result.toArray(new DashBoard[0]);
        user.profile.apps = ArrayUtil.remove(user.profile.apps, existingAppIndex, App.class);
        user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
