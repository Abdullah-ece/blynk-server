package cc.blynk.server.application.handlers.main.logic.sharing;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileShareLogic {

    private final SessionDao sessionDao;

    public MobileShareLogic(Holder holder) {
        this.sessionDao = holder.sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                MobileStateHolder state, StringMessage message) {
        String[] splitted = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        int dashId = Integer.parseInt(splitted[0]);
        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        if ("on".equals(splitted[1])) {
            dash.isShared = true;
        } else {
            dash.isShared = false;
        }

        Session session = sessionDao.getOrgSession(state.user.orgId);
        session.sendToSharedApps(ctx.channel(), dash.sharedToken, message.command, message.id, message.body);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
