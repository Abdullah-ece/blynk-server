package cc.blynk.server.application.handlers.main.logic.dashboard.widget;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.controls.Timer;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
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
 * Created on 01.02.16.
 */
public final class MobileCreateWidgetLogic {

    private static final Logger log = LogManager.getLogger(MobileCreateWidgetLogic.class);

    private MobileCreateWidgetLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        //format is "dashId widget_json" or "dashId widgetId templateId widget_json"
        String[] split = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (split.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);

        long widgetAddToId;
        long templateIdAddToId;
        String widgetString;
        if (split.length == 4) {
            widgetAddToId = Long.parseLong(split[1]);
            templateIdAddToId = Long.parseLong(split[2]);
            widgetString = split[3];
        } else {
            widgetAddToId = -1;
            templateIdAddToId = -1;
            widgetString = split[1];
        }

        if (widgetString == null || widgetString.isEmpty()) {
            throw new JsonException("Income widget message is empty.");
        }

        if (widgetString.length() > holder.limits.widgetSizeLimitBytes) {
            throw new JsonException("Widget is larger then limit.");
        }

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        Widget newWidget = JsonParser.parseWidget(widgetString, message.id);

        if (newWidget.width < 1 || newWidget.height < 1) {
            throw new JsonException("Widget has wrong dimensions.");
        }

        log.debug("Creating new widget {} for dashId {}.", widgetString, dashId);

        for (Widget widget : dash.widgets) {
            if (widget.id == newWidget.id) {
                throw new JsonException("Widget with same id already exists.");
            }
            if (widget instanceof DeviceTiles) {
                Widget widgetInTiles = ((DeviceTiles) widget).getWidgetById(newWidget.id);
                if (widgetInTiles != null) {
                    throw new JsonException("Widget with same id already exists.");
                }
            }
        }

        int price = newWidget.getPrice();
        if (user.notEnoughEnergy(price)) {
            log.debug("Not enough energy.");
            throw new JsonException("Not enough energy.");
        }
        user.subtractEnergy(price);

        //widget could be added to project or to other widget like DeviceTiles
        if (widgetAddToId == -1) {
            dash.widgets = ArrayUtil.add(dash.widgets, newWidget, Widget.class);
        } else {
            //right now we can only add to DeviceTiles widget
            DeviceTiles deviceTiles = (DeviceTiles) dash.getWidgetByIdOrThrow(widgetAddToId);
            TileTemplate tileTemplate = deviceTiles.getTileTemplateByIdOrThrow(templateIdAddToId);
            tileTemplate.widgets = ArrayUtil.add(tileTemplate.widgets, newWidget, Widget.class);
        }

        dash.updatedAt = System.currentTimeMillis();
        user.lastModifiedTs = dash.updatedAt;

        TimerWorker timerWorker = holder.timerWorker;
        if (newWidget instanceof Timer) {
            timerWorker.add(state.user.orgId,
                    state.user.email, (Timer) newWidget, dashId, widgetAddToId, templateIdAddToId);
        } else if (newWidget instanceof Eventor) {
            timerWorker.add(state.user.orgId, state.user.email, (Eventor) newWidget, dashId);
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
