package cc.blynk.server.application.handlers.main.logic.graph;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.profile.Profile;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.model.widgets.outputs.graph.Superchart;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.exceptions.NoDataException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.protocol.model.messages.WebJsonMessage;
import cc.blynk.server.core.reporting.MobileGraphRequest;
import cc.blynk.server.core.reporting.raw.RawDataCacheForGraphProcessor;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.db.dao.RawEntry;
import cc.blynk.server.db.dao.ReportingDBDao;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.core.protocol.enums.Response.SERVER_ERROR;
import static cc.blynk.server.db.dao.RawEntry.convert;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;
import static cc.blynk.server.internal.WebByteBufUtil.noData;
import static cc.blynk.utils.ByteUtils.compress;
import static cc.blynk.utils.StringUtils.split2Device;
import static io.netty.util.internal.EmptyArrays.EMPTY_BYTES;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileGetSuperChartDataLogic {

    private static final Logger log = LogManager.getLogger(MobileGetSuperChartDataLogic.class);

    private final DeviceDao deviceDao;
    private final ReportingDBDao reportingDBDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final RawDataCacheForGraphProcessor rawDataCacheForGraphProcessor;

    public MobileGetSuperChartDataLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.reportingDBDao = holder.reportingDBManager.reportingDBDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.rawDataCacheForGraphProcessor = holder.reportingDBManager.rawDataCacheForGraphProcessor;
    }

    private static boolean hasData(byte[][] data) {
        for (byte[] pinData : data) {
            if (pinData.length > 0) {
                return true;
            }
        }
        return false;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                MobileStateHolder state, StringMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (messageParts.length < 3) {
            throw new JsonException("Wrong income message format.");
        }

        int targetId = -1;
        String[] dashIdAndTargetIdString = split2Device(messageParts[0]);
        if (dashIdAndTargetIdString.length == 2) {
            targetId = Integer.parseInt(dashIdAndTargetIdString[1]);
        }
        int dashId = Integer.parseInt(dashIdAndTargetIdString[0]);

        long widgetId = Long.parseLong(messageParts[1]);
        Period period = Period.valueOf(messageParts[2]);
        int page = 0;
        if (messageParts.length == 4) {
            page = Integer.parseInt(messageParts[3]);
        }

        Profile profile = state.user.profile;
        DashBoard dash = profile.getDashByIdOrThrow(dashId);
        Widget widget = dash.getWidgetById(widgetId);

        //special case for device tiles widget.
        if (widget == null) {
            DeviceTiles deviceTiles = dash.getWidgetByType(DeviceTiles.class);
            if (deviceTiles != null) {
                widget = deviceTiles.getWidgetById(widgetId);
            }
        }

        if (!(widget instanceof Superchart)) {
            throw new JsonException("Passed wrong widget id.");
        }

        Superchart superchart = (Superchart) widget;

        int numberOfStreams = superchart.dataStreams.length;
        if (numberOfStreams == 0) {
            log.debug("No data streams for enhanced graph with id {}.", widgetId);
            ctx.writeAndFlush(noData(message.id), ctx.voidPromise());
            return;
        }

        MobileGraphRequest[] requestedPins = new MobileGraphRequest[superchart.dataStreams.length];

        int i = 0;
        for (GraphDataStream graphDataStream : superchart.dataStreams) {
            //special case, for device tiles widget targetID may be overrided
            int targetIdUpdated = graphDataStream.getTargetId(targetId);
            Device device = deviceDao.getById(targetIdUpdated);
            if (device == null) {
                requestedPins[i] = MobileGraphRequest.EMPTY_REQUEST;
            } else {
                requestedPins[i] = new MobileGraphRequest(dashId, device.id,
                        graphDataStream.dataStream, period, page);
            }
            i++;
        }

        readGraphData(ctx, state.user, requestedPins, message.id);
    }

    private void readGraphData(ChannelHandlerContext ctx, User user,
                               MobileGraphRequest[] requestedPins, int msgId) {
        blockingIOProcessor.executeHistory(() -> {
            try {
                byte[][] values = new byte[requestedPins.length][];

                for (int i = 0; i < requestedPins.length; i++) {
                    MobileGraphRequest mobileGraphRequest = requestedPins[i];
                    log.debug("Getting data for graph pin : {}.", mobileGraphRequest);
                    if (mobileGraphRequest.isValid()) {
                        if (mobileGraphRequest.isLiveData()) {
                            values[i] = rawDataCacheForGraphProcessor.getLiveGraphData(mobileGraphRequest);
                        } else {
                            List<RawEntry> rawEntriesList = reportingDBDao.getReportingDataByTs(mobileGraphRequest);
                            values[i] = convert(rawEntriesList);
                        }

                    } else {
                        values[i] = EMPTY_BYTES;
                    }
                }

                if (!hasData(values)) {
                    throw new NoDataException();
                }

                byte[] compressed = compress(requestedPins[0].dashId, values);

                if (ctx.channel().isWritable()) {
                    ctx.writeAndFlush(
                            makeBinaryMessage(GET_SUPERCHART_DATA, msgId, compressed),
                            ctx.voidPromise()
                    );
                }
            } catch (NoDataException noDataException) {
                ctx.writeAndFlush(noData(msgId), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error reading reporting data. For user {}. Error: {}", user.email, e.getMessage());
                ctx.writeAndFlush(new WebJsonMessage(msgId, "Error reading reporting data.", SERVER_ERROR),
                        ctx.voidPromise());
            }
        });
    }

}
