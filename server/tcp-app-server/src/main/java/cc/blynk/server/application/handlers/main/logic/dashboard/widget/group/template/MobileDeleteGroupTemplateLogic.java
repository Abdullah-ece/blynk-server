package cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.template;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.widgets.Widget;
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
 * Created on 01.02.16.
 */
public final class MobileDeleteGroupTemplateLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteGroupTemplateLogic.class);

    private final DeviceDao deviceDao;

    public MobileDeleteGroupTemplateLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        String[] split = split3(message.body);

        if (split.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        long widgetId = Long.parseLong(split[1]);
        long groupTemplateId = Long.parseLong(split[2]);

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        Widget widget = dash.getWidgetByIdOrThrow(widgetId);

        if (!(widget instanceof DeviceTiles)) {
            throw new JsonException("Income widget id is not DeviceTiles.");
        }

        DeviceTiles deviceTiles = (DeviceTiles) widget;

        log.debug("Deleting group template dashId : {}, widgetId : {}, groupTemplateId : {}.",
                dash, widgetId, groupTemplateId);

        deviceTiles.deleteGroupTemplateById(groupTemplateId);
        deviceTiles.deleteGroupByTemplateId(groupTemplateId);
        dash.updatedAt = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
