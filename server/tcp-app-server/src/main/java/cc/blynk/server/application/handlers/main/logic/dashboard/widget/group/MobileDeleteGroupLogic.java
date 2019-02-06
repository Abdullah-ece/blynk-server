package cc.blynk.server.application.handlers.main.logic.dashboard.widget.group;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split3;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileDeleteGroupLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteGroupLogic.class);

    public MobileDeleteGroupLogic() {
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                MobileStateHolder state, StringMessage message) {
        String[] split = split3(message.body);

        if (split.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        long widgetId = Long.parseLong(split[1]);
        long groupId = Long.parseLong(split[2]);

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        DeviceTiles deviceTiles = dash.getDeviceTilesByIdOrThrow(widgetId);

        log.debug("Deleting group dashId : {}, widgetId : {}, groupId : {}.",
                dash, widgetId, groupId);

        deviceTiles.deleteGroupById(groupId);
        dash.updatedAt = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
