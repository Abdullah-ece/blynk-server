package cc.blynk.integration.tcp;

import cc.blynk.integration.BaseTest;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.server.core.dao.PinNameUtil;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphType;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.model.widgets.outputs.graph.Superchart;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportingWidget;
import cc.blynk.server.core.model.widgets.ui.reporting.source.DeviceReportSource;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportDataStream;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportSource;
import cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate;
import cc.blynk.server.core.protocol.model.messages.BinaryMessage;
import cc.blynk.server.core.protocol.model.messages.ResponseMessage;
import cc.blynk.server.core.protocol.model.messages.common.HardwareMessage;
import cc.blynk.utils.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.createDevice;
import static cc.blynk.integration.TestUtil.illegalCommand;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.ONE_HOUR;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.SIX_HOURS;
import static cc.blynk.server.core.protocol.enums.Response.NO_DATA;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class HistoryGraphTest extends SingleServerInstancePerTest {

    private static String blynkTempDir;

    @BeforeClass
    public static void initTempFolder() {
        blynkTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "blynk").toString();
    }

    @Before
    public void cleanStorage() {
        holder.reportingDBManager.averageAggregator.getMinute().clear();
        holder.reportingDBManager.averageAggregator.getHourly().clear();
        holder.reportingDBManager.averageAggregator.getDaily().clear();
        holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.clear();
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDevice() throws Exception {
        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 88, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        assertEquals(0, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));

        assertEquals(1, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(1, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDevice2() throws Exception {
        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        //no live
        superchart.selectedPeriods = new Period[] {
                ONE_HOUR, SIX_HOURS
        };
        DataStream dataStream = new DataStream((short) 88, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        assertEquals(0, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));

        assertEquals(1, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDeviceTiles() throws Exception {
        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;

        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(ok(1));

        int[] deviceIds = new int[] {0};

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        GraphDataStream graphDataStream = new GraphDataStream(
                null, GraphType.LINE, 0, -1,
                new DataStream((short) 88, PinType.VIRTUAL),
                AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        TileTemplate tileTemplate = new PageTileTemplate(1,
                new Widget[]{superchart}, deviceIds, "name", "name", "iconName", BoardType.ESP8266, new DataStream((short)1, PinType.VIRTUAL),
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);

        clientPair.appClient.createTemplate(1, widgetId, tileTemplate);
        clientPair.appClient.verifyResult(ok(2));

        assertEquals(0, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));

        assertEquals(1, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(1, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDeviceTilesWith2Pins() throws Exception {
        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;

        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(ok(1));

        int[] deviceIds = new int[] {0};

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        GraphDataStream graphDataStream = new GraphDataStream(
                null, GraphType.LINE, 0, -1,
                new DataStream((short) 88, PinType.VIRTUAL),
                AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(
                null, GraphType.LINE, 0, -1,
                new DataStream((short) 89, PinType.VIRTUAL),
                AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream,
                graphDataStream2
        };

        TileTemplate tileTemplate = new PageTileTemplate(1,
                new Widget[]{superchart}, deviceIds, "name", "name", "iconName", BoardType.ESP8266, new DataStream((short)1, PinType.VIRTUAL),
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);

        clientPair.appClient.createTemplate(1, widgetId, tileTemplate);
        clientPair.appClient.verifyResult(ok(2));

        assertEquals(0, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));
        clientPair.hardwareClient.send("hardware vw 89 112");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(2, b("1-0 vw 89 112"))));


        assertEquals(2, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(2, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(2, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(2, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenPinAssignedToReporting() throws Exception {
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 432;
        reportingWidget.width = 8;
        reportingWidget.height = 4;
        reportingWidget.reportSources = new ReportSource[] {
                new DeviceReportSource(
                        new ReportDataStream[] {new ReportDataStream((short) 88, PinType.VIRTUAL, null, false)},
                        new int[] {0}
                )
        };

        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        assertEquals(0, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));

        assertEquals(1, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenPinAssignedToReporting2() throws Exception {
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 432;
        reportingWidget.width = 8;
        reportingWidget.height = 4;
        reportingWidget.reportSources = new ReportSource[] {
                new TileTemplateReportSource(
                        new ReportDataStream[] {new ReportDataStream((short) 88, PinType.VIRTUAL, null, false)},
                        0,
                        new int[] {0}
                )
        };

        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        assertEquals(0, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));

        assertEquals(1, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenPinAssignedToReporting3() throws Exception {
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 432;
        reportingWidget.width = 8;
        reportingWidget.height = 4;
        reportingWidget.reportSources = new ReportSource[] {
                new TileTemplateReportSource(
                        new ReportDataStream[] {new ReportDataStream((short) 88, PinType.VIRTUAL, null, false),
                                                new ReportDataStream((short) 89, PinType.VIRTUAL, null, false)},
                        0,
                        new int[] {0}
                )
        };

        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        assertEquals(0, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(0, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());

        clientPair.hardwareClient.send("hardware vw 89 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 89 111"))));

        assertEquals(1, holder.reportingDBManager.averageAggregator.getMinute().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getHourly().size());
        assertEquals(1, holder.reportingDBManager.averageAggregator.getDaily().size());
        assertEquals(0, holder.reportingDBManager.rawDataCacheForGraphProcessor.rawStorage.size());
        assertEquals(0, holder.reportingDBManager.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void testGetLIVEGraphDataForEnhancedGraph() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }


        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 88, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, NO_DATA)));

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(1, bb.getInt());
        assertEquals(111D, bb.getDouble(), 0.1);
        assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);

        for (int i = 1; i <= 60; i++) {
            clientPair.hardwareClient.send("hardware vw 88 " + i);
        }

        verify(clientPair.appClient.responseMock, timeout(10000)).channelRead(any(), eq(new HardwareMessage(61, b("1-0 vw 88 60"))));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(60, bb.getInt());
        for (int i = 1; i <= 60; i++) {
            assertEquals(i, bb.getDouble(), 0.1);
            assertEquals(System.currentTimeMillis(), bb.getLong(), 10000);
        }
    }

    @Test
    public void testNoLiveDataWhenNoGraph() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 88, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(2, NO_DATA)));
    }

    @Test
    public void testNoLiveDataWhenNoGraph2() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 88, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(2, NO_DATA)));

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(2, b("1-0 vw 88 111"))));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(1, bb.getInt());
        assertEquals(111D, bb.getDouble(), 0.1);
        assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);

        clientPair.appClient.deleteWidget(1, 432);
        clientPair.appClient.verifyResult(ok(2));

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(3, b("1-0 vw 88 111"))));

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(3));

        clientPair.appClient.send("getenhanceddata 1" + b(" 432 LIVE"));
        clientPair.appClient.reset();
        graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(1, bb.getInt());
        assertEquals(111D, bb.getDouble(), 0.1);
        assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);
    }

    @Test
    public void testGetLIVEGraphDataForEnhancedGraphWithPaging() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }


        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 88, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, NO_DATA)));

        clientPair.hardwareClient.send("hardware vw 88 111");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 88 111"))));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(1, bb.getInt());
        assertEquals(111D, bb.getDouble(), 0.1);
        assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);

        for (int i = 1; i <= 60; i++) {
            clientPair.hardwareClient.send("hardware vw 88 " + i);
        }

        verify(clientPair.appClient.responseMock, timeout(10000)).channelRead(any(), eq(new HardwareMessage(61, b("1-0 vw 88 60"))));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.LIVE);
        graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(60, bb.getInt());
        for (int i = 1; i <= 60; i++) {
            assertEquals(i, bb.getDouble(), 0.1);
            assertEquals(System.currentTimeMillis(), bb.getLong(), 10000);
        }
    }


    @Test
    public void testPagingWorksForGetEnhancedHistoryDataPartialData() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }

        Path pinReportingDataPath = Paths.get(tempDir, "data", getUserName(),
                PinNameUtil.generateFilename(PinType.DIGITAL, (short) 8, GraphGranularityType.MINUTE));

        try (DataOutputStream dos = new DataOutputStream(
                    Files.newOutputStream(pinReportingDataPath, CREATE, APPEND))) {
            for (int i = 1; i <= 61; i++) {
                dos.writeDouble(i);
                dos.writeLong(i * 1000);
            }
            dos.flush();
        }

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 8, PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.ONE_HOUR, 1);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);


        assertEquals(1, bb.getInt());
        assertEquals(1, bb.getInt());
        assertEquals(1D, bb.getDouble(), 0.1);
        assertEquals(1000, bb.getLong());
    }

    @Test
    public void testPagingWorksForGetEnhancedHistoryDataFullData() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }

        Path pinReportingDataPath = Paths.get(tempDir, "data", getUserName(),
                PinNameUtil.generateFilename(PinType.DIGITAL, (short) 8, GraphGranularityType.MINUTE));

        try (DataOutputStream dos = new DataOutputStream(
                Files.newOutputStream(pinReportingDataPath, CREATE, APPEND))) {
            for (int i = 1; i <= 120; i++) {
                dos.writeDouble(i);
                dos.writeLong(i * 1000);
            }
            dos.flush();
        }

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 8, PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.ONE_HOUR, 1);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);


        assertEquals(1, bb.getInt());
        assertEquals(60, bb.getInt());
        for (int i = 1; i <= 60; i++) {
            assertEquals(i, bb.getDouble(), 0.1);
            assertEquals(i * 1000, bb.getLong());
        }
    }

    @Test
    public void testPagingWorksForGetEnhancedHistoryDataFullDataAndSecondPage() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }

        Path pinReportingDataPath = Paths.get(tempDir, "data", getUserName(),
                PinNameUtil.generateFilename(PinType.DIGITAL, (short) 8, GraphGranularityType.MINUTE));

        try (DataOutputStream dos = new DataOutputStream(
                Files.newOutputStream(pinReportingDataPath, CREATE, APPEND))) {
            for (int i = 1; i <= 120; i++) {
                dos.writeDouble(i);
                dos.writeLong(i * 1000);
            }
            dos.flush();
        }

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 8, PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.ONE_HOUR, 5);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, NO_DATA)));
    }

    @Test
    public void testPagingWorksForGetEnhancedHistoryDataWhenNoData() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");

        Path userReportFolder = Paths.get(tempDir, "data", getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }

        Path pinReportingDataPath = Paths.get(tempDir, "data", getUserName(),
                PinNameUtil.generateFilename(PinType.DIGITAL, (short) 8, GraphGranularityType.MINUTE));

        try (DataOutputStream dos = new DataOutputStream(
                Files.newOutputStream(pinReportingDataPath, CREATE, APPEND))) {
            for (int i = 1; i <= 60; i++) {
                dos.writeDouble(i);
                dos.writeLong(i * 1000);
            }
            dos.flush();
        }

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 8, PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(1));
        clientPair.appClient.reset();

        clientPair.appClient.getSuperChartData(1, 432, Period.ONE_HOUR, 1);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, NO_DATA)));
    }

    private static String getFileNameByMask(String pattern) {
        File dir = new File(blynkTempDir);
        File[] files = dir.listFiles((dir1, name) -> name.startsWith(pattern));
        return latest(files).getName();
    }

    private static File latest(File[] files) {
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }

    @Test
    public void testExportDataFromHistoryGraph() throws Exception {
        clientPair.appClient.send("export 1");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(illegalCommand(1)));

        clientPair.appClient.send("export 1 666");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(illegalCommand(2)));

        clientPair.appClient.send("export 1 1");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(illegalCommand(3)));

        clientPair.appClient.send("export 1 191600");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(4, NO_DATA)));

        //generate fake reporting data
        Path userReportDirectory = Paths.get(holder.props.getProperty("data.folder"), "data", getUserName());
        Files.createDirectories(userReportDirectory);
        Path userReportFile = Paths.get(userReportDirectory.toString(),
                PinNameUtil.generateFilename(PinType.ANALOG, (short) 7, GraphGranularityType.MINUTE));
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);

        clientPair.appClient.send("export 1 191600");
        verify(holder.mailWrapper, timeout(1000)).sendHtml(eq(getUserName()), eq("History graph data for project My Dashboard"), contains("/" + getUserName() + "_1_0_a7_"));
    }

    @Test
    public void testGeneratedCSVIsCorrect() throws Exception {
        //generate fake reporting data
        Path userReportDirectory = Paths.get(holder.props.getProperty("data.folder"), "data", getUserName());
        Files.createDirectories(userReportDirectory);
        String filename = PinNameUtil.generateFilename(PinType.ANALOG, (short) 7, GraphGranularityType.MINUTE);
        Path userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);

        clientPair.appClient.send("export 1 191600");
        clientPair.appClient.verifyResult(ok(1));

        String csvFileName = getFileNameByMask(getUserName() + "_1_0_a7_");
        verify(holder.mailWrapper, timeout(1000)).sendHtml(eq(getUserName()), eq("History graph data for project My Dashboard"), contains(csvFileName));

        try (InputStream fileStream = new FileInputStream(Paths.get(blynkTempDir, csvFileName).toString());
             InputStream gzipStream = new GZIPInputStream(fileStream);
             BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream))) {

            String[] lineSplit = buffered.readLine().split(",");
            assertEquals(1.1D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(1, Long.parseLong(lineSplit[1]));
            assertEquals(0, Long.parseLong(lineSplit[2]));

            lineSplit = buffered.readLine().split(",");
            assertEquals(2.2D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(2, Long.parseLong(lineSplit[1]));
            assertEquals(0, Long.parseLong(lineSplit[2]));
        }
    }

    @Test
    public void testGeneratedCSVIsCorrectForMultiDevicesNoData() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;
        device1.status = Status.OFFLINE;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.verifyResult(ok(2));

        clientPair.appClient.reset();

        clientPair.appClient.send("export 1-200000 191600");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, NO_DATA)));
    }

    @Test
    public void testGeneratedCSVIsCorrectForMultiDevicesAndEnhancedGraph() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;
        device1.status = Status.OFFLINE;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 8, PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 200_000, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(3));

        clientPair.appClient.reset();

        //generate fake reporting data
        Path userReportDirectory = Paths.get(holder.props.getProperty("data.folder"), "data", getUserName());
        Files.createDirectories(userReportDirectory);

        String filename = PinNameUtil.generateFilename(PinType.DIGITAL, (short) 8, GraphGranularityType.MINUTE);
        Path userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);

        filename = PinNameUtil.generateFilename(PinType.DIGITAL, (short) 8, GraphGranularityType.MINUTE);
        userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 11.1, 11L);
        FileUtils.write(userReportFile, 12.2, 12L);

        clientPair.appClient.send("export 1 432");
        clientPair.appClient.verifyResult(ok(1));

        String csvFileName = getFileNameByMask(getUserName() + "_1_200000_d8_");
        verify(holder.mailWrapper, timeout(1000)).sendHtml(eq(getUserName()), eq("History graph data for project My Dashboard"), contains(csvFileName));

        try (InputStream fileStream = new FileInputStream(Paths.get(blynkTempDir, csvFileName).toString());
             InputStream gzipStream = new GZIPInputStream(fileStream);
             BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream))) {

            //first device
            String[] lineSplit = buffered.readLine().split(",");
            assertEquals(1.1D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(1, Long.parseLong(lineSplit[1]));
            assertEquals(0, Long.parseLong(lineSplit[2]));

            lineSplit = buffered.readLine().split(",");
            assertEquals(2.2D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(2, Long.parseLong(lineSplit[1]));
            assertEquals(0, Long.parseLong(lineSplit[2]));

            //second device
            lineSplit = buffered.readLine().split(",");
            assertEquals(11.1D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(11, Long.parseLong(lineSplit[1]));
            assertEquals(1, Long.parseLong(lineSplit[2]));

            lineSplit = buffered.readLine().split(",");
            assertEquals(12.2D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(12, Long.parseLong(lineSplit[1]));
            assertEquals(1, Long.parseLong(lineSplit[2]));
        }
    }

    @Test
    public void testGeneratedCSVIsCorrectForMultiDevices() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;
        device1.status = Status.OFFLINE;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        Superchart superchart = new Superchart();
        superchart.id = 191600;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 7, PinType.ANALOG);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0,
                200_000, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream,
        };

        clientPair.appClient.updateWidget(1, superchart);
        clientPair.appClient.verifyResult(ok(3));

        clientPair.appClient.reset();

        //generate fake reporting data
        Path userReportDirectory = Paths.get(holder.props.getProperty("data.folder"), "data", getUserName());
        Files.createDirectories(userReportDirectory);

        String filename = PinNameUtil.generateFilename(PinType.ANALOG, (short) 7, GraphGranularityType.MINUTE);
        Path userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);

        filename = PinNameUtil.generateFilename(PinType.ANALOG, (short) 7, GraphGranularityType.MINUTE);
        userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 11.1, 11L);
        FileUtils.write(userReportFile, 12.2, 12L);

        clientPair.appClient.send("export 1 191600");
        clientPair.appClient.verifyResult(ok(1));

        String csvFileName = getFileNameByMask(getUserName() + "_1_200000_a7_");
        verify(holder.mailWrapper, timeout(1000)).sendHtml(eq(getUserName()), eq("History graph data for project My Dashboard"), contains(csvFileName));

        try (InputStream fileStream = new FileInputStream(Paths.get(blynkTempDir, csvFileName).toString());
             InputStream gzipStream = new GZIPInputStream(fileStream);
             BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream))) {

            //first device
            String[] lineSplit = buffered.readLine().split(",");
            assertEquals(1.1D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(1, Long.parseLong(lineSplit[1]));
            assertEquals(0, Long.parseLong(lineSplit[2]));

            lineSplit = buffered.readLine().split(",");
            assertEquals(2.2D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(2, Long.parseLong(lineSplit[1]));
            assertEquals(0, Long.parseLong(lineSplit[2]));

            //second device
            lineSplit = buffered.readLine().split(",");
            assertEquals(11.1D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(11, Long.parseLong(lineSplit[1]));
            assertEquals(1, Long.parseLong(lineSplit[2]));

            lineSplit = buffered.readLine().split(",");
            assertEquals(12.2D, Double.parseDouble(lineSplit[0]), 0.001D);
            assertEquals(12, Long.parseLong(lineSplit[1]));
            assertEquals(1, Long.parseLong(lineSplit[2]));
        }
    }
}
