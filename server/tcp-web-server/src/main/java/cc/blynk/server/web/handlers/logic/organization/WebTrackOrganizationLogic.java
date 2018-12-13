package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebTrackOrganizationLogic {

    private static final Logger log = LogManager.getLogger(WebTrackOrganizationLogic.class);

    private WebTrackOrganizationLogic() {
    }

    public static void messageReceived(Holder holder,
                                       ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        //todo security check
        int orgId = Integer.parseInt(message.body);
        holder.organizationDao.getOrgByIdOrThrow(orgId);
        state.selectedOrgId = orgId;
        log.trace("Selecting webapp org {} for {}.", orgId, state.user.email);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
