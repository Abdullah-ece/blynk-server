package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod;
import cc.blynk.server.core.model.widgets.web.BaseWebGraph;
import cc.blynk.server.core.model.widgets.web.WebSource;
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
import java.nio.ByteBuffer;
import java.util.Collection;

import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.FIFTEEN_MINUTES;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.N_DAY;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.N_MONTH;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.N_THREE_DAYS;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.N_THREE_MONTHS;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.N_TWO_WEEKS;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.N_WEEK;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.ONE_HOUR;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.ONE_YEAR;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.SIX_HOURS;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.SIX_MONTHS;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.THIRTY_MINUTES;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.THREE_HOURS;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.TWELVE_HOURS;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.TWO_DAYS;
import static cc.blynk.server.core.protocol.enums.Command.GET_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.noData;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.ByteUtils.REPORTING_RECORD_SIZE_BYTES;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class WebGetGraphDataLogic {

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

    private static byte[] convert(Collection<RawEntry> rawEntries) {
        var byteBuffer = ByteBuffer.allocate(4 + rawEntries.size() * REPORTING_RECORD_SIZE_BYTES);
        byteBuffer.putInt(rawEntries.size());
        for (var rawEntry : rawEntries) {
            byteBuffer.putDouble(rawEntry.getValue());
            byteBuffer.putLong(rawEntry.getTs());
        }
        return byteBuffer.array();
    }

    private static GraphPeriod calcGraphPeriod(long from, long to) {
        long diff = to - from;
        for (var graphPeriod : CUSTOM_DATE_PERIODS) {
            if (diff < graphPeriod.millis) {
                return graphPeriod;
            }
        }

        return GraphPeriod.ONE_YEAR;
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
        GraphPeriod graphPeriod = GraphPeriod.valueOf(messageParts[2]);

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
            ctx.writeAndFlush(noData(message.id), ctx.voidPromise());
            return;
        }

        int i = 0;
        long fromTS;
        long toTS;
        GraphSourceFunction sourceFunction;

        switch (graphPeriod) {
            case LIVE :
                sourceFunction = rawDataCacheForGraphProcessor::getLiveGraphData;
                fromTS = -1; //not used
                toTS = -1; //not used
                break;
            case CUSTOM :
                fromTS = Long.parseLong(messageParts[3]);
                toTS = Long.parseLong(messageParts[4]);
                graphPeriod = calcGraphPeriod(fromTS, toTS);
                sourceFunction = reportingDBDao::getReportingDataByTs;
                log.trace("Selected granularity fro custom range: {}", graphPeriod);
                break;
            default :
                long now = System.currentTimeMillis();
                fromTS = now - graphPeriod.millis;
                toTS = now;
                sourceFunction = reportingDBDao::getReportingDataByTs;
                break;
        }

        WebGraphRequest[] webGraphDataStreamRequests = new WebGraphRequest[numberOfStreams];
        for (WebSource webSource : baseWebGraph.sources) {
            webGraphDataStreamRequests[i++] = new WebGraphRequest(deviceId,
                    webSource.dataStream, graphPeriod, 0, webSource.sourceType, fromTS, toTS);
        }

        readGraphDataFromDB(ctx.channel(), state.user, deviceId,
                message.id, webGraphDataStreamRequests, sourceFunction);

    }

    private void readGraphDataFromDB(Channel channel, User user, int deviceId, int msgId,
                                     WebGraphRequest[] webGraphRequests, GraphSourceFunction source) {
        blockingIOProcessor.executeDB(() -> {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream(8096);
                ByteUtils.writeInt(out, deviceId);

                for (WebGraphRequest webGraphRequest : webGraphRequests) {
                    Collection<RawEntry> rawEntriesList = source.getEntries(webGraphRequest);
                    byte[] bytesOfRawEntries = convert(rawEntriesList);
                    out.write(bytesOfRawEntries);
                }

                //todo for now skipping compression
                //byte[] compressed = compress(requestedPins[0].deviceId, data);
                byte[] data = out.toByteArray();

                if (channel.isWritable()) {
                    channel.writeAndFlush(
                            makeBinaryMessage(GET_ENHANCED_GRAPH_DATA, msgId, data),
                            channel.voidPromise()
                    );
                }
            } catch (NoDataException noDataException) {
                channel.writeAndFlush(noData(msgId), channel.voidPromise());
            } catch (Exception e) {
                log.error("Error reading reporting data. For user {}. Error: {}", user.email, e.getMessage());
                channel.writeAndFlush(json(msgId, "Error reading reporting data."), channel.voidPromise());
            }
        });
    }

    private static final GraphPeriod[] CUSTOM_DATE_PERIODS = new GraphPeriod[] {
            FIFTEEN_MINUTES,
            THIRTY_MINUTES,
            ONE_HOUR,
            THREE_HOURS,
            SIX_HOURS,
            TWELVE_HOURS,
            N_DAY,
            TWO_DAYS,
            N_THREE_DAYS,
            N_WEEK,
            N_TWO_WEEKS,
            N_MONTH,
            N_THREE_MONTHS,
            SIX_MONTHS,
            ONE_YEAR
    };

    @FunctionalInterface
    public interface GraphSourceFunction {
        Collection<RawEntry> getEntries(WebGraphRequest webGraphRequest) throws Exception;
    }
}
