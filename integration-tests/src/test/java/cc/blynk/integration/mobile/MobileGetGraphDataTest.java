package cc.blynk.integration.mobile;

import cc.blynk.integration.BaseTest;
import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphType;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.model.widgets.outputs.graph.Superchart;
import cc.blynk.server.core.protocol.model.messages.BinaryMessage;
import cc.blynk.server.core.reporting.average.AggregationKey;
import cc.blynk.server.core.reporting.average.AggregationValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
import static cc.blynk.server.core.reporting.average.AverageAggregatorProcessor.MINUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 17.01.19.
 */
@RunWith(MockitoJUnitRunner.class)
public class MobileGetGraphDataTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Before
    public void cleanReportingDB() throws Exception {
        holder.reportingDBManager.executeSQL("DELETE FROM reporting_average_minute");
        holder.reportingDBManager.executeSQL("DELETE FROM reporting_average_hourly");
        holder.reportingDBManager.executeSQL("DELETE FROM reporting_average_daily");
    }

    @Test
    public void testGetGraphDataForSuperChartWithEmptyDataStream() throws Exception {
        String user = getUserName();

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(user, "1");
        appClient.verifyResult(ok(1));

        //Step 1. Create minimal project with 1 widget.
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        GraphDataStream graphDataStream = new GraphDataStream(null,
                GraphType.LINE, 0, 0, null, null, 0, null, null, null,
                0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        appClient.createWidget(dashBoard.id, superchart);
        appClient.verifyResult(ok(3));

        appClient.getSuperChartData(dashBoard.id, superchart.id, Period.DAY);
        appClient.verifyResult(webJson(4, "No data.", 17));
    }

    @Test
    public void testGetGraphDataForEnhancedGraphWithWrongDataStream() throws Exception {
        String user = getUserName();

        //device is mandatory for any test
        Device device = createProductAndDevice(user);

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(user, "1");
        appClient.verifyResult(ok(1));

        //Step 1. Create minimal project with 1 widget.
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 8, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        appClient.createWidget(dashBoard.id, superchart);
        appClient.verifyResult(ok(3));

        long now = System.currentTimeMillis();
        for (int point = 0; point < Period.ONE_HOUR.numberOfPoints; point++) {
            AggregationValue aggregationValue = new AggregationValue();
            aggregationValue.update((double) point);
            AggregationKey aggregationKey = new AggregationKey(device.id, dataStream.pinType, dataStream.pin, now / MINUTE);
            Map<AggregationKey, AggregationValue> data = new HashMap<>();
            data.put(aggregationKey, aggregationValue);
            holder.reportingDBManager.reportingDBDao.insert(data, GraphGranularityType.MINUTE);
        }

        appClient.getSuperChartData(dashBoard.id, superchart.id, Period.DAY);
        appClient.verifyResult(webJson(4, "No data.", 17));
    }

    @Test
    public void testGetGraphDataForSuperChart() throws Exception {
        String user = getUserName();

        //device is mandatory for any test
        Device device = createProductAndDevice(user);

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(user, "1");
        appClient.verifyResult(ok(1));

        //Step 1. Create minimal project with 1 widget.
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 4, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null,
                GraphType.LINE, 0, device.id, dataStream, null, 0, null, null, null, 0, 0,
                false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream
        };

        appClient.createWidget(dashBoard.id, superchart);
        appClient.verifyResult(ok(3));
        appClient.reset();

        long now = System.currentTimeMillis();
        for (int point = 0; point < Period.ONE_HOUR.numberOfPoints; point++) {
            AggregationValue aggregationValue = new AggregationValue();
            aggregationValue.update((double) point);
            AggregationKey aggregationKey = new AggregationKey(device.id, dataStream.pinType, dataStream.pin, now / MINUTE);
            Map<AggregationKey, AggregationValue> data = new HashMap<>();
            data.put(aggregationKey, aggregationValue);
            holder.reportingDBManager.reportingDBDao.insert(data, GraphGranularityType.MINUTE);
        }

        appClient.getSuperChartData(dashBoard.id, superchart.id, Period.ONE_HOUR);
        BinaryMessage graphDataResponse = appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(dashBoard.id, bb.getInt());
        assertEquals(60, bb.getInt());
        for (int point = 0; point < 60; point++) {
            assertEquals(point, bb.getDouble(), 0.1);
            assertEquals((now / MINUTE) * MINUTE, bb.getLong(), 1000);
        }
    }

    @Test
    public void testGetGraphDataForSuperChartAnd2Streams() throws Exception {
        String user = getUserName();

        //device is mandatory for any test
        Device device = createProductAndDevice(user);

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(user, "1");
        appClient.verifyResult(ok(1));

        //Step 1. Create minimal project with 1 widget.
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream((short) 4, PinType.VIRTUAL);
        DataStream dataStream2 = new DataStream((short) 5, PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null,
                GraphType.LINE, 0, device.id, dataStream, null, 0, null, null, null, 0, 0,
                false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null,
                GraphType.LINE, 0, device.id, dataStream2, null, 0, null, null, null, 0, 0,
                false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[] {
                graphDataStream,
                graphDataStream2
        };

        appClient.createWidget(dashBoard.id, superchart);
        appClient.verifyResult(ok(3));
        appClient.reset();

        long now = System.currentTimeMillis();
        Map<AggregationKey, AggregationValue> data = Map.of(
            new AggregationKey(device.id, dataStream.pinType, dataStream.pin, now / MINUTE), new AggregationValue(1.11D)
        );
        Map<AggregationKey, AggregationValue> data2 = Map.of(
            new AggregationKey(device.id, dataStream.pinType, dataStream.pin, now / MINUTE), new AggregationValue(1.22D)
        );

        holder.reportingDBManager.reportingDBDao.insert(data, GraphGranularityType.MINUTE);
        holder.reportingDBManager.reportingDBDao.insert(data2, GraphGranularityType.MINUTE);

        appClient.getSuperChartData(dashBoard.id, superchart.id, Period.ONE_HOUR);
        BinaryMessage graphDataResponse = appClient.getBinaryBody();

        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(dashBoard.id, bb.getInt());
        assertEquals(2, bb.getInt());
        assertEquals(1.11D, bb.getDouble(), 0.1);
        assertEquals((now / MINUTE) * MINUTE, bb.getLong());
        assertEquals(1.22D, bb.getDouble(), 0.1);
        assertEquals((now / MINUTE) * MINUTE, bb.getLong());
        assertEquals(0, bb.getInt());
    }

    private Device createProductAndDevice(String user) throws Exception {
        AppWebSocketClient client = loggedDefaultClient(user, "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);
        return createdDevice;
    }

}
