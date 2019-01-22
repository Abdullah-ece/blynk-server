package cc.blynk.server.application.handlers.main.logic.graph;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.Superchart;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2Device;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileDeleteSuperChartDataLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteSuperChartDataLogic.class);

    private MobileDeleteSuperChartDataLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       User user, StringMessage message) {
        String[] messageParts = StringUtils.split3(message.body);

        if (messageParts.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        String[] dashIdAndDeviceId = split2Device(messageParts[0]);
        int dashId = Integer.parseInt(dashIdAndDeviceId[0]);
        long widgetId = Long.parseLong(messageParts[1]);
        int streamIndex = -1;
        if (message.body.length() == 3) {
            streamIndex = Integer.parseInt(messageParts[2]);
        }
        int targetId = -1;
        if (dashIdAndDeviceId.length == 2) {
            targetId = Integer.parseInt(dashIdAndDeviceId[1]);
        }

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        Widget widget = dash.getWidgetById(widgetId);
        if (widget == null) {
            widget = dash.getWidgetByIdInDeviceTilesOrThrow(widgetId);
        }
        Superchart superchart = (Superchart) widget;

        if (streamIndex == -1 || streamIndex > superchart.dataStreams.length - 1) {
            delete(holder, ctx, message.id, targetId, superchart.dataStreams);
        } else {
            delete(holder, ctx, message.id, targetId, superchart.dataStreams[streamIndex]);
        }
    }

    private static void delete(Holder holder, ChannelHandlerContext ctx, int msgId,
                               int targetId, GraphDataStream... dataStreams) {
        holder.blockingIOProcessor.executeHistory(() -> {
            try {
                for (GraphDataStream graphDataStream : dataStreams) {
                    int targetIdUpdated = graphDataStream.getTargetId(targetId);
                    Device device = holder.deviceDao.getById(targetIdUpdated);

                    DataStream dataStream = graphDataStream.dataStream;
                    if (device != null && dataStream != null && dataStream.pinType != null) {
                        int deviceId = device.id;
                        holder.reportingDBManager
                                .reportingDBDao.delete(deviceId, dataStream.pin, dataStream.pinType);
                        holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage
                                .remove(new BaseReportingKey(deviceId, dataStream.pinType, dataStream.pin));
                    }
                }
                ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
            } catch (Exception e) {
                log.debug("Error removing superchart data. Reason : {}.", e.getMessage());
                ctx.writeAndFlush(json(msgId, "Error removing superchart data."), ctx.voidPromise());
            }
        });
    }
}
