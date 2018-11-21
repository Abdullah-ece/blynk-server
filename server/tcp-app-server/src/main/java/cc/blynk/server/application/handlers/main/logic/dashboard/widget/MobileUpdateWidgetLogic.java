package cc.blynk.server.application.handlers.main.logic.dashboard.widget;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.controls.Timer;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.ui.Tabs;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportingWidget;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileUpdateWidgetLogic {

    private static final Logger log = LogManager.getLogger(MobileUpdateWidgetLogic.class);

    private MobileUpdateWidgetLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        String widgetString = split[1];

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

        log.debug("Updating widget {}.", widgetString);

        Widget prevWidget = null;
        DeviceTiles deviceTiles = null;

        long deviceTilesId = -1;
        long deviceTilesTemplateId = -1;

        long widgetId = newWidget.id;
        for (Widget widget : dash.widgets) {
            if (widget.id == widgetId) {
                prevWidget = widget;
                break;
            }
            if (widget instanceof DeviceTiles) {
                deviceTiles = (DeviceTiles) widget;
                for (TileTemplate tileTemplate : deviceTiles.templates) {
                    for (Widget tileTemplateWidget : tileTemplate.widgets) {
                        if (tileTemplateWidget.id == widgetId) {
                            prevWidget = tileTemplateWidget;
                            deviceTilesId = deviceTiles.id;
                            deviceTilesTemplateId = tileTemplate.id;
                            break;
                        }
                    }
                }
            }
        }

        if (prevWidget == null) {
            throw new JsonException("Widget with passed id not found.");
        }

        if (!prevWidget.getClass().equals(newWidget.getClass())) {
            throw new JsonException("Widget class was changed.");
        }

        if (prevWidget instanceof Notification) {
            Notification prevNotif = (Notification) prevWidget;
            Notification newNotif = (Notification) newWidget;
            newNotif.iOSTokens.putAll(prevNotif.iOSTokens);
            newNotif.androidTokens.putAll(prevNotif.androidTokens);
        }

        //do not update template, tile fields for DeviceTiles.
        if (newWidget instanceof DeviceTiles) {
            DeviceTiles prevDeviceTiles = (DeviceTiles) prevWidget;
            DeviceTiles newDeviceTiles = (DeviceTiles) newWidget;
            newDeviceTiles.tiles = prevDeviceTiles.tiles;
            newDeviceTiles.templates = prevDeviceTiles.templates;
        }

        if (newWidget instanceof ReportingWidget) {
            ReportingWidget prevReporting = (ReportingWidget) prevWidget;
            ReportingWidget newReporting = (ReportingWidget) newWidget;
            newReporting.reports = prevReporting.reports;
        }

        TimerWorker timerWorker = holder.timerWorker;
        if (deviceTilesId != -1) {
            TileTemplate tileTemplate = deviceTiles.getTileTemplateByWidgetIdOrThrow(newWidget.id);
            if (newWidget instanceof Tabs) {
                Tabs newTabs = (Tabs) newWidget;
                tileTemplate.widgets = MobileDeleteWidgetLogic.deleteTabs(timerWorker,
                        user, state.user.email, dash.id, deviceTilesId, deviceTilesTemplateId,
                        tileTemplate.widgets, newTabs.tabs.length - 1);
            }
            tileTemplate.widgets = ArrayUtil.copyAndReplace(
                    tileTemplate.widgets, newWidget, tileTemplate.getWidgetIndexByIdOrThrow(newWidget.id));
        } else {
            if (newWidget instanceof Tabs) {
                Tabs newTabs = (Tabs) newWidget;
                dash.widgets = MobileDeleteWidgetLogic.deleteTabs(timerWorker,
                        user, state.user.email, dash.id, deviceTilesId, deviceTilesTemplateId,
                        dash.widgets, newTabs.tabs.length - 1);
            }
            dash.widgets = ArrayUtil.copyAndReplace(
                    dash.widgets, newWidget, dash.getWidgetIndexByIdOrThrow(newWidget.id));
        }

        dash.updatedAt = System.currentTimeMillis();
        user.lastModifiedTs = dash.updatedAt;

        if (prevWidget instanceof Timer) {
            timerWorker.delete(state.user.orgId,
                    state.user.email, (Timer) prevWidget, dashId, deviceTilesId, deviceTilesTemplateId);
        } else if (prevWidget instanceof Eventor) {
            timerWorker.delete(state.user.orgId,
                    state.user.email, (Eventor) prevWidget, dashId);
        }

        if (newWidget instanceof Timer) {
            timerWorker.add(state.user.orgId,
                    state.user.email, (Timer) newWidget, dashId, deviceTilesId, deviceTilesTemplateId);
        } else if (newWidget instanceof Eventor) {
            timerWorker.add(state.user.orgId,
                    state.user.email, (Eventor) newWidget, dashId);
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
