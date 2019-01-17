package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.model.widgets.web.BaseWebGraph;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.exceptions.NoDataException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.reporting.WebGraphRequest;
import cc.blynk.server.core.reporting.raw.RawDataCacheForGraphProcessor;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.db.dao.RawEntry;
import cc.blynk.server.db.dao.ReportingDBDao;
import cc.blynk.utils.ByteUtils;
import cc.blynk.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebGetGraphDataLogic {

    private static final Logger log = LogManager.getLogger(WebGetGraphDataLogic.class);

    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBDao reportingDBDao;
    private final DeviceDao deviceDao;
    private final RawDataCacheForGraphProcessor rawDataCacheForGraphProcessor;

    public WebGetGraphDataLogic(Holder holder) {
        this.reportingDBDao = holder.reportingDBManager.reportingDBDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.deviceDao = holder.deviceDao;
        this.rawDataCacheForGraphProcessor = holder.reportingDiskDao.rawDataCacheForGraphProcessor;
    }

    private void readGraphDataFromDB(Channel channel, User user, int deviceId, int msgId,
                                     WebGraphRequest[] webGraphRequests, GraphSourceFunction source) {
        blockingIOProcessor.executeDB(() -> {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream(8096);
                ByteUtils.writeInt(out, deviceId);

                for (WebGraphRequest webGraphRequest : webGraphRequests) {
                    Collection<RawEntry> rawEntriesList = source.getEntries(webGraphRequest);
                    byte[] bytesOfRawEntries = RawEntry.convert(rawEntriesList);
                    out.write(bytesOfRawEntries);
                }

                //todo for now skipping compression
                //byte[] compressed = compress(requestedPins[0].deviceId, data);
                byte[] data = out.toByteArray();

                if (channel.isWritable()) {
                    channel.writeAndFlush(
                            makeBinaryMessage(GET_SUPERCHART_DATA, msgId, data),
                            channel.voidPromise()
                    );
                }
            } catch (NoDataException noDataException) {
                channel.writeAndFlush(json(msgId, "No data."), channel.voidPromise());
            } catch (Exception e) {
                log.error("Error reading reporting data. For user {}. Error: {}", user.email, e.getMessage());
                channel.writeAndFlush(json(msgId, "Error reading reporting data."), channel.voidPromise());
            }
        });
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (messageParts.length < 3) {
            log.error("Wrong income message format for {}.", state.user.email);
            ctx.writeAndFlush(json(message.id, "Wrong income message format."), ctx.voidPromise());
            return;
        }

        int deviceId = Integer.parseInt(messageParts[0]);
        long widgetId = Long.parseLong(messageParts[1]);
        Period period = Period.valueOf(messageParts[2]);

        //todo check user has access
        Device device = deviceDao.getByIdOrThrow(deviceId);
        if (device == null) {
            log.debug("Device with passed id {} not found.", deviceId);
            ctx.writeAndFlush(json(message.id, "Device with passed id not found."), ctx.voidPromise());
            return;
        }

        Widget widget = device.webDashboard.getWidgetById(widgetId);

        if (!(widget instanceof BaseWebGraph)) {
            log.debug("Widget with passed id {} is not graph for deviceId {}.", widgetId, deviceId);
            ctx.writeAndFlush(json(message.id, "Widget with passed id is not graph."), ctx.voidPromise());
            return;
        }

        BaseWebGraph baseWebGraph = (BaseWebGraph) widget;

        int numberOfStreams = baseWebGraph.sources.length;
        if (numberOfStreams == 0) {
            log.debug("No data streams for web graph with id {} for deviceId {}.", widgetId, deviceId);
            throw new JsonException("No data streams for web graph.");
        }

        int i = 0;
        long fromTS;
        long toTS;
        GraphSourceFunction sourceFunction;

        switch (period) {
            case LIVE :
                sourceFunction = rawDataCacheForGraphProcessor::getLiveGraphData;
                fromTS = -1; //not used
                toTS = -1; //not used
                break;
            case CUSTOM :
                fromTS = Long.parseLong(messageParts[3]);
                toTS = Long.parseLong(messageParts[4]);
                period = Period.calcGraphPeriod(fromTS, toTS);
                sourceFunction = reportingDBDao::getReportingDataByTs;
                log.trace("Selected granularity fro custom range: {}", period);
                break;
            default :
                long now = System.currentTimeMillis();
                fromTS = now - period.millis;
                toTS = now;
                sourceFunction = reportingDBDao::getReportingDataByTs;
                break;
        }

        WebGraphRequest[] webGraphDataStreamRequests = new WebGraphRequest[numberOfStreams];
        for (WebSource webSource : baseWebGraph.sources) {
            webGraphDataStreamRequests[i++] = new WebGraphRequest(deviceId,
                    webSource.dataStream, period, 0, webSource.sourceType, fromTS, toTS);
        }

        readGraphDataFromDB(ctx.channel(), state.user, deviceId,
                message.id, webGraphDataStreamRequests, sourceFunction);

    }

    @FunctionalInterface
    public interface GraphSourceFunction {
        Collection<RawEntry> getEntries(WebGraphRequest webGraphRequest) throws Exception;
    }
}
