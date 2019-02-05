package cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.template;

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
public final class MobileEditGroupTemplateLogic {

    private static final Logger log = LogManager.getLogger(MobileEditGroupTemplateLogic.class);

    public MobileEditGroupTemplateLogic(Holder holder) {
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        String[] split = split3(message.body);

        if (split.length < 3) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        long widgetId = Long.parseLong(split[1]);
        String groupTemplateString = split[2];

        if (groupTemplateString == null || groupTemplateString.isEmpty()) {
            throw new JsonException("Income group template message is empty.");
        }

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        Widget widget = dash.getWidgetByIdOrThrow(widgetId);

        if (!(widget instanceof DeviceTiles)) {
            throw new JsonException("Income widget id is not DeviceTiles.");
        }

        DeviceTiles deviceTiles = (DeviceTiles) widget;
        BaseGroupTemplate newGroupTemplate = JsonParser.parseGroupTemplate(groupTemplateString, message.id);

        log.debug("Updating group template {}.", groupTemplateString);

        int existingGroupTemplateIndex = deviceTiles.getGroupTemplateIndexByIdOrThrow(newGroupTemplate.id);
        deviceTiles.replaceGroupTemplate(newGroupTemplate, existingGroupTemplateIndex);

        dash.updatedAt = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
