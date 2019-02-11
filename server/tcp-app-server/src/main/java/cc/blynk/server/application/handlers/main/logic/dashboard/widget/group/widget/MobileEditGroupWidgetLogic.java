package cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.widget;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.group.BaseGroupTemplate;
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
public final class MobileEditGroupWidgetLogic {

    private static final Logger log = LogManager.getLogger(MobileEditGroupWidgetLogic.class);

    private final int widgetSizeLimitBytes;

    public MobileEditGroupWidgetLogic(Holder holder) {
        this.widgetSizeLimitBytes = holder.limits.widgetSizeLimitBytes;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        //"dashId deviceTilesId widgetString"
        String[] split = split3(message.body);

        if (split.length < 3) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        long deviceTilesWidgetId = Long.parseLong(split[1]);
        String widgetString = split[2];

        if (widgetString == null || widgetString.isEmpty()) {
            throw new JsonException("Income widget message is empty.");
        }

        if (widgetString.length() > widgetSizeLimitBytes) {
            throw new JsonException("Widget is larger then limit.");
        }

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        DeviceTiles deviceTiles = dash.getDeviceTilesByIdOrThrow(deviceTilesWidgetId);
        Widget newWidget = JsonParser.parseWidget(widgetString, message.id);

        if (newWidget.width < 1 || newWidget.height < 1) {
            throw new JsonException("Widget has wrong dimensions.");
        }

        log.debug("Updating widget {}.", widgetString);

        for (BaseGroupTemplate groupTemplate : deviceTiles.groupTemplates) {
            groupTemplate.updateWidget(newWidget);
        }

        long now = System.currentTimeMillis();
        dash.updatedAt = now;
        user.lastModifiedTs = now;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
