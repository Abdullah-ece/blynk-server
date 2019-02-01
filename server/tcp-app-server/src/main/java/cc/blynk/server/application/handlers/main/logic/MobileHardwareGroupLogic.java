package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.group.Group;
import cc.blynk.server.core.processors.BaseProcessorHandler;
import cc.blynk.server.core.processors.WebhookProcessor;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_HARDWARE_GROUP;
import static cc.blynk.server.internal.WebByteBufUtil.deviceNotInNetwork;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split3;

/**
 * Responsible for handling incoming hardware commands from applications and forwarding it to
 * appropriate hardware.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class MobileHardwareGroupLogic extends BaseProcessorHandler {

    private static final Logger log = LogManager.getLogger(MobileHardwareGroupLogic.class);

    private final SessionDao sessionDao;
    private final DeviceDao deviceDao;

    public MobileHardwareGroupLogic(Holder holder) {
        super(holder.eventorProcessor, new WebhookProcessor(holder.asyncHttpClient,
                holder.limits.webhookPeriodLimitation,
                holder.limits.webhookResponseSizeLimitBytes,
                holder.limits.webhookFailureLimit,
                holder.stats),
                holder.deviceDao);
        this.sessionDao = holder.sessionDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        //here expecting command in format "dashId widgetId groupId vw 88 1"
        String[] split = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        int dashId = Integer.parseInt(split[0]);
        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        long widgetId = Long.parseLong(split[1]);
        Widget widget = dash.getWidgetByIdOrThrow(widgetId);

        if (!(widget instanceof DeviceTiles)) {
            log.trace("Widget is not Device Tiles.");
            return;
        }

        DeviceTiles deviceTiles = (DeviceTiles) widget;

        String body = split[3];
        char operation = body.charAt(1);

        if (operation == 'w') {
            String[] splitBody = split3(body);

            if (splitBody.length < 4) {
                log.debug("Not valid write command.");
                ctx.writeAndFlush(json(message.id, "Not valid write command format."), ctx.voidPromise());
                return;
            }

            PinType pinType = PinType.getPinType(splitBody[0].charAt(0));
            short pin = NumberUtil.parsePin(splitBody[1]);
            String value = splitBody[2];
            long now = System.currentTimeMillis();

            int groupId = Integer.parseInt(split[2]);
            Group group = deviceTiles.getGroupByIdOrThrow(groupId);
            group.updateValue(pin, pinType, value);

            DeviceStorageKey deviceStorageKey = new DeviceStorageKey(pin, pinType);
            int[] deviceIds = group.deviceIds;
            for (int deviceId : deviceIds) {
                Device device = deviceDao.getById(deviceId);
                if (device != null) {
                    device.updateValue(deviceStorageKey, value, now);
                }
            }

            Session session = sessionDao.getOrgSession(state.user.orgId);
            //sending to shared dashes and master-master apps
            //session.sendToSharedApps(ctx.channel(), dash.sharedToken, DEVICE_SYNC, message.id, message.body);
            session.sendToApps(ctx.channel(), MOBILE_HARDWARE_GROUP, message.id, message.body);
            session.sendToSelectedDeviceOnWeb(MOBILE_HARDWARE_GROUP, message.id, body, deviceIds);

            if (session.sendMessageToHardware(HARDWARE, message.id, body, deviceIds)) {
                log.debug("Device not in the network.");
                ctx.writeAndFlush(deviceNotInNetwork(message.id), ctx.voidPromise());
            }
            //processEventorAndWebhook(state.user, dash, targetId, session, pin, pinType, value, now);
        }
    }

}
