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
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileCreateGroupWidgetLogic {

    private static final Logger log = LogManager.getLogger(MobileCreateGroupWidgetLogic.class);

    private final int widgetSizeLimitBytes;

    public MobileCreateGroupWidgetLogic(Holder holder) {
        this.widgetSizeLimitBytes = holder.limits.widgetSizeLimitBytes;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        //"dashId deviceTilesWidgetId groupId widget_json"
        String[] split = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (split.length < 4) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        long deviceTilesWidgetId = Long.parseLong(split[1]);
        long groupTemplateId = Long.parseLong(split[2]);
        String widgetString = split[3];

        if (widgetString == null || widgetString.isEmpty()) {
            throw new JsonException("Income widget message is empty.");
        }

        if (widgetString.length() > widgetSizeLimitBytes) {
            throw new JsonException("Widget is larger then limit.");
        }

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        Widget newWidget = JsonParser.parseWidget(widgetString, message.id);

        if (newWidget.width < 1 || newWidget.height < 1) {
            throw new JsonException("Widget has wrong dimensions.");
        }

        log.debug("Creating new widget {} for dashId {}.", widgetString, dashId);

        DeviceTiles deviceTiles = dash.getDeviceTilesByIdOrThrow(deviceTilesWidgetId);
        BaseGroupTemplate groupTemplate = deviceTiles.getGroupTemplateByIdOrThrow(groupTemplateId);
        groupTemplate.addWidget(newWidget);

        long now = System.currentTimeMillis();
        dash.updatedAt = now;
        user.lastModifiedTs = now;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
