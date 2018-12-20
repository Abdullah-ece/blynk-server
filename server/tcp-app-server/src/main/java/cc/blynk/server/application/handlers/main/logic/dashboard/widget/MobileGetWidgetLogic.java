package cc.blynk.server.application.handlers.main.logic.dashboard.widget;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.protocol.enums.Command.GET_WIDGET;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileGetWidgetLogic {

    private static final Logger log = LogManager.getLogger(MobileGetWidgetLogic.class);

    private final OrganizationDao organizationDao;

    public MobileGetWidgetLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        var split = split2(message.body);

        if (split.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        var dashId = Integer.parseInt(split[0]);
        var widgetId = Long.parseLong(split[1]);

        var user = state.user;
        var dash = user.profile.getDashByIdOrThrow(dashId);

        var widget = dash.getWidgetByIdOrThrow(widgetId);

        if (widget instanceof DeviceTiles) {
            List<Device> devices = organizationDao.getDevices(state);

            DeviceTiles deviceTiles = (DeviceTiles) widget;
            deviceTiles.recreateTiles(devices);

            for (Device device : devices) {
                for (var entry : device.pinStorage.values.entrySet()) {
                    DeviceStorageKey key = entry.getKey();
                    PinStorageValue value = entry.getValue();
                    deviceTiles.updateIfSame(device.id, key.pin, key.pinType, value.lastValue());
                }
            }
        }

        if (ctx.channel().isWritable()) {
            var widgetString = JsonParser.toJson(widget);
            ctx.writeAndFlush(
                    makeUTF8StringMessage(GET_WIDGET, message.id, widgetString),
                    ctx.voidPromise()
            );
            log.debug("Get widget {} for {}.", widget.id, user.email);
            log.trace(widgetString);
        }
    }

}
