package cc.blynk.server.application.handlers.main.logic.web;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.ReportingDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod;
import cc.blynk.server.core.model.widgets.web.BaseWebGraph;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.server.core.protocol.exceptions.NoDataException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.reporting.GraphPinRequest;
import cc.blynk.server.core.session.WebAppStateHolder;
import cc.blynk.utils.ByteUtils;
import cc.blynk.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;

import static cc.blynk.server.core.protocol.enums.Command.GET_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.noData;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.serverError;
import static cc.blynk.utils.ByteUtils.REPORTING_RECORD_SIZE_BYTES;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class GetWebGraphDataLogic {

    private static final Logger log = LogManager.getLogger(GetWebGraphDataLogic.class);

    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDao reportingDao;
    private final DeviceDao deviceDao;

    public GetWebGraphDataLogic(Holder holder) {
        this.reportingDao = holder.reportingDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (messageParts.length < 3) {
            throw new IllegalCommandException("Wrong income message format.");
        }

        int deviceId = Integer.parseInt(messageParts[0]);
        long widgetId = Long.parseLong(messageParts[1]);
        GraphPeriod graphPeriod = GraphPeriod.valueOf(messageParts[2]);

        //todo check user has access
        Device device = deviceDao.getById(deviceId);
        if (device == null) {
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            log.debug("Device with passed id {} not found.", deviceId);
            return;
        }

        Widget widget = device.webDashboard.getWidgetById(widgetId);

        if (!(widget instanceof BaseWebGraph)) {
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            log.debug("Widget with passed id {} is not graph for deviceId {}.", widgetId, deviceId);
            return;
        }

        BaseWebGraph graph = (BaseWebGraph) widget;

        int numberOfStreams = graph.sources.length;
        if (numberOfStreams == 0) {
            log.debug("No data streams for web graph with id {} for deviceId {}.", widgetId, deviceId);
            ctx.writeAndFlush(noData(message.id), ctx.voidPromise());
            return;
        }

        GraphPinRequest[] requestedPins = new GraphPinRequest[numberOfStreams];

        int i = 0;
        for (WebSource webSource : graph.sources) {
            requestedPins[i++] = new GraphPinRequest(0, deviceId,
                    webSource.dataStream, graphPeriod, 0, webSource.sourceType);
        }

        readGraphData(ctx.channel(), state.user, requestedPins, message.id);
    }

    private void readGraphData(Channel channel, User user, GraphPinRequest[] requestedPins, int msgId) {
        blockingIOProcessor.executeHistory(() -> {
            try {
                byte[][] allPinsData = reportingDao.getReportingData(user, requestedPins);

                ByteArrayOutputStream out = new ByteArrayOutputStream(8096);
                ByteUtils.writeInt(out, requestedPins[0].deviceId);
                for (byte[] data : allPinsData) {
                    ByteUtils.writeInt(out, data.length / REPORTING_RECORD_SIZE_BYTES);
                    out.write(data);
                }

                //todo for now skipping compression
                //byte[] compressed = compress(requestedPins[0].deviceId, data);
                byte[] compressed = out.toByteArray();

                if (channel.isWritable()) {
                    channel.writeAndFlush(
                            makeBinaryMessage(GET_ENHANCED_GRAPH_DATA, msgId, compressed),
                            channel.voidPromise()
                    );
                }
            } catch (NoDataException noDataException) {
                channel.writeAndFlush(noData(msgId), channel.voidPromise());
            } catch (Exception e) {
                log.error("Error reading reporting data. For user {}. Error: {}", user.email, e.getMessage());
                channel.writeAndFlush(serverError(msgId), channel.voidPromise());
            }
        });
    }

}
