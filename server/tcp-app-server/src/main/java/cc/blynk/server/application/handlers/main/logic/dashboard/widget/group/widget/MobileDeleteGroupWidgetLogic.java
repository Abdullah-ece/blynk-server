package cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.widget;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.controls.Timer;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.group.BaseGroupTemplate;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.workers.timer.TimerWorker;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split3;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileDeleteGroupWidgetLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteGroupWidgetLogic.class);

    public MobileDeleteGroupWidgetLogic() {
    }

    /**
     * Removes all widgets with tabId greater than lastTabIndex
     */
    static Widget[] deleteTabs(TimerWorker timerWorker, User user, String userKey,
                               int dashId, long deviceTilesId, long templateId,
                               Widget[] widgets, int lastTabIndex) {
        ArrayList<Widget> zeroTabWidgets = new ArrayList<>();
        for (Widget widgetToDelete : widgets) {
            if (widgetToDelete.tabId > lastTabIndex) {
                if (widgetToDelete instanceof Timer) {
                    timerWorker.delete(user.orgId, userKey, (Timer) widgetToDelete, dashId, deviceTilesId, templateId);
                } else if (widgetToDelete instanceof Eventor) {
                    timerWorker.delete(user.orgId, userKey, (Eventor) widgetToDelete, dashId);
                }
            } else {
                zeroTabWidgets.add(widgetToDelete);
            }
        }

        return zeroTabWidgets.toArray(new Widget[0]);
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        //"dashId deviceTilesId widgetId"
        String[] split = split3(message.body);

        if (split.length < 3) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        long deviceTileWidgetId = Long.parseLong(split[1]);
        long widgetId = Long.parseLong(split[2]);

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        log.debug("Removing widget with id {} for dashId {}.", widgetId, dashId);

        DeviceTiles deviceTiles = dash.getDeviceTilesByIdOrThrow(deviceTileWidgetId);
        for (BaseGroupTemplate groupTemplate : deviceTiles.groupTemplates) {
            groupTemplate.deleteWidgetById(widgetId);
        }

        long now = System.currentTimeMillis();
        dash.updatedAt = now;
        user.lastModifiedTs = now;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
