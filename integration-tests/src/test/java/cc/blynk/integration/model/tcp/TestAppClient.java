package cc.blynk.integration.model.tcp;

import cc.blynk.client.handlers.decoders.AppClientMessageDecoder;
import cc.blynk.integration.model.SimpleClientHandler;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DashboardSettings;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.profile.Profile;
import cc.blynk.server.core.model.profile.ProfileSettings;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.model.widgets.ui.reporting.Report;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.protocol.handlers.encoders.MobileMessageEncoder;
import cc.blynk.server.core.protocol.model.messages.BinaryMessage;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Random;
import java.util.StringJoiner;

import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_PUSH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DEACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DEVICE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_PROFILE_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_PROJECT_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EXPORT_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_PROVISION_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_REGISTER;
import static cc.blynk.utils.AppNameUtil.BLYNK;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR_STRING;
import static cc.blynk.utils.StringUtils.DEVICE_SEPARATOR;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public class TestAppClient extends BaseTestAppClient {

    public TestAppClient(String host, int port) {
        super(host, port, Mockito.mock(Random.class), new ServerProperties(Collections.emptyMap()));
    }

    public TestAppClient(ServerProperties properties) {
        this("localhost", properties.getHttpsPort(), properties, new NioEventLoopGroup());
    }

    public TestAppClient(String host, ServerProperties properties) {
        this(host, properties.getHttpsPort(), properties, new NioEventLoopGroup());
    }

    public TestAppClient(String host, int port, ServerProperties properties) {
        this(host, port, properties, new NioEventLoopGroup());
    }

    public TestAppClient(String host, int port, ServerProperties properties, NioEventLoopGroup nioEventLoopGroup) {
        super(host, port, Mockito.mock(Random.class), properties);
        this.nioEventLoopGroup = nioEventLoopGroup;
    }

    public Device parseDevice() throws Exception {
        return parseDevice(1);
    }

    public Profile parseProfile(int expectedMessageOrder) throws Exception {
        return JsonParser.parseProfileFromString(getBody(expectedMessageOrder));
    }

    public Device parseDevice(int expectedMessageOrder) throws Exception {
        return JsonParser.parseDevice(getBody(expectedMessageOrder), 0);
    }

    public void getDevices() {
        send(MOBILE_GET_DEVICES);
    }

    public void getDevices(int dashId) {
        send(MOBILE_GET_DEVICES, dashId);
    }

    public Device[] parseDevices() throws Exception {
        return parseDevices(1);
    }

    public Device[] parseDevices(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), Device[].class);
    }

    public DeviceDTO[] parseDevicesDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), DeviceDTO[].class);
    }

    public App parseApp(int expectedMessageOrder) throws Exception {
        return JsonParser.parseApp(getBody(expectedMessageOrder), 0);
    }

    public DashBoard parseDash(int expectedMessageOrder) throws Exception {
        return JsonParser.parseDashboard(getBody(expectedMessageOrder), 0);
    }

    public Report parseReportFromResponse(int expectedMessageOrder) throws Exception {
        return JsonParser.parseReport(getBody(expectedMessageOrder), 0);
    }

    public DeviceDTO parseDeviceDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), DeviceDTO.class);
    }

    public BinaryMessage getBinaryBody() throws Exception {
        ArgumentCaptor<BinaryMessage> objectArgumentCaptor = ArgumentCaptor.forClass(BinaryMessage.class);
        verify(responseMock, timeout(1000)).channelRead(any(), objectArgumentCaptor.capture());
        return objectArgumentCaptor.getValue();
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                        sslCtx.newHandler(ch.alloc(), host, port),
                        new AppClientMessageDecoder(),
                        new MobileMessageEncoder(new GlobalStats()),
                        responseMock
                );
            }
        };
    }

    public void createDevice(Device device) {
        send(MOBILE_CREATE_DEVICE, device);
    }

    public void updateDevice(int dashId, Device device) {
        send(MOBILE_EDIT_DEVICE, "" + dashId + BODY_SEPARATOR + device.toString());
    }

    public void deleteDevice(int dashId, int deviceId) {
        send(MOBILE_DELETE_DEVICE, "" + dashId + BODY_SEPARATOR + deviceId);
    }

    public void createWidget(int dashId, Widget widget) throws Exception {
        createWidget(dashId, JsonParser.MAPPER.writeValueAsString(widget));
    }

    public void createWidget(int dashId, long widgetId, long templateId, String widgetJson) {
        send(MOBILE_CREATE_WIDGET, "" + dashId + BODY_SEPARATOR + widgetId
                + BODY_SEPARATOR + templateId + BODY_SEPARATOR + widgetJson);
    }

    public void createWidget(int dashId, long widgetId, long templateId, Widget widget) throws Exception {
        send(MOBILE_CREATE_WIDGET, "" + dashId + BODY_SEPARATOR + widgetId
                + BODY_SEPARATOR + templateId + BODY_SEPARATOR + JsonParser.MAPPER.writeValueAsString(widget));
    }

    public void createWidget(int dashId, String widgetJson) {
        send(MOBILE_CREATE_WIDGET, "" + dashId + BODY_SEPARATOR + widgetJson);
    }

    public void updateWidget(int dashId, Widget widget) throws Exception {
        updateWidget(dashId, JsonParser.MAPPER.writeValueAsString(widget));
    }

    public void updateWidget(int dashId, String widgetJson) {
        send(MOBILE_EDIT_WIDGET, "" + dashId + BODY_SEPARATOR + widgetJson);
    }

    public void deleteWidget(int dashId, long widgetId) {
        send(MOBILE_DELETE_WIDGET, "" + dashId + " " + widgetId);
    }

    public void activate(int dashId) {
        send(MOBILE_ACTIVATE_DASHBOARD, dashId);
    }

    public void deactivate(int dashId) {
        send(MOBILE_DEACTIVATE_DASHBOARD, dashId);
    }

    public void updateDash(DashBoard dashBoard) {
        updateDash(dashBoard.toString());
    }

    public void updateDash(String dashJson) {
        send(MOBILE_EDIT_DASH, dashJson);
    }

    public void deleteDash(int dashId) {
        send(MOBILE_DELETE_DASH, dashId);
    }

    public void createDash(DashBoard dashBoard) {
        createDash(dashBoard.toString());
    }

    public void createDash(String dashJson) {
        send(MOBILE_CREATE_DASH, dashJson);
    }

    public void register(String email, String pass, String appName) {
        send(MOBILE_REGISTER, email + BODY_SEPARATOR + SHA256Util.makeHash(pass, email) + BODY_SEPARATOR + appName);
    }

    public void loginNoHash(String email, String pass) {
        send(LOGIN, email + BODY_SEPARATOR +pass
                + BODY_SEPARATOR + "Android" + BODY_SEPARATOR + "2.27.0" + BODY_SEPARATOR + BLYNK);
    }

    public void login(String email, String pass) {
        login(email, pass, "Android", "2.27.0", BLYNK);
    }

    public void login(String email, String pass, String os, String version) {
        login(email, pass, os, version, BLYNK);
    }

    public void login(String email, String pass, String os, String version, String appName) {
        send(LOGIN, email + BODY_SEPARATOR + SHA256Util.makeHash(pass, email)
                + BODY_SEPARATOR + os + BODY_SEPARATOR + version + BODY_SEPARATOR + appName);
    }

    public void sync(int dashId) {
        send(DEVICE_SYNC, dashId);
    }

    public void sync(int dashId, int deviceId) {
        send(DEVICE_SYNC, "" + dashId + DEVICE_SEPARATOR + deviceId);
    }

    public void deleteDeviceData(int dashId, int deviceId) {
        send(MOBILE_DELETE_DEVICE_DATA, "" + dashId + DEVICE_SEPARATOR + deviceId);
    }

    public void deleteDeviceData(int dashId, int deviceId, String... pins) {
        StringJoiner sj = new StringJoiner(BODY_SEPARATOR_STRING);
        for (String pin : pins) {
            sj.add(pin);
        }
        send(MOBILE_DELETE_DEVICE_DATA, "" + dashId + DEVICE_SEPARATOR + deviceId + BODY_SEPARATOR + sj.toString());
    }

    public void getEnhancedGraphData(int dashId, long widgetId, Period period) {
        send(GET_SUPERCHART_DATA, "" + dashId + BODY_SEPARATOR + widgetId + BODY_SEPARATOR + period.name());
    }

    public void getEnhancedGraphData(int dashId, long widgetId, Period period, int page) {
        send(GET_SUPERCHART_DATA, "" + dashId + BODY_SEPARATOR + widgetId + BODY_SEPARATOR + period.name() + BODY_SEPARATOR + page);
    }

    public void createTemplate(int dashId, long widgetId, TileTemplate tileTemplate) throws Exception {
        createTemplate(dashId, widgetId, JsonParser.MAPPER.writeValueAsString(tileTemplate));
    }

    public void createTemplate(int dashId, long widgetId, String tileTemplate) {
        send(MOBILE_CREATE_TILE_TEMPLATE, "" + dashId + BODY_SEPARATOR + widgetId + BODY_SEPARATOR + tileTemplate);
    }

    public void updateTemplate(int dashId, long widgetId, TileTemplate tileTemplate) throws Exception {
        send(MOBILE_EDIT_TILE_TEMPLATE, "" + dashId + BODY_SEPARATOR + widgetId + BODY_SEPARATOR
                + JsonParser.MAPPER.writeValueAsString(tileTemplate));
    }

    public void getDevice(int deviceId) {
        send(MOBILE_GET_DEVICE, "" + deviceId);
    }

    public void getDevice(int deviceId, boolean includeInProvisionOnly) {
        send(MOBILE_GET_DEVICE, "" + deviceId + BODY_SEPARATOR + includeInProvisionOnly);
    }

    public void updateDeviceMetafield(int deviceId, MetaField metaField) {
        send(MOBILE_EDIT_DEVICE_METAFIELD, "" + deviceId + BODY_SEPARATOR + metaField.toString());
    }

    public void updateDeviceMetafields(int deviceId, MetaField[] metaFields) {
        send(MOBILE_EDIT_DEVICE_METAFIELD, "" + deviceId + BODY_SEPARATOR + JsonParser.toJson(metaFields));
    }

    public void createReport(int dashId, Report report) {
        createReport(dashId, report.toString());
    }

    public void createReport(int dashId, String report) {
        send(MOBILE_CREATE_REPORT, "" + dashId + BODY_SEPARATOR + report);
    }

    public void updateReport(int dashId, Report report) {
        send(MOBILE_EDIT_REPORT, "" + dashId + BODY_SEPARATOR + report.toString());
    }

    public void getDevicesByReferenceMetafield(int deviceId, int metafieldId) {
        send(MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD, "" + deviceId + BODY_SEPARATOR + metafieldId);
    }

    public void getWidget(int dashId, long widgetId) {
        send(MOBILE_GET_WIDGET, "" + dashId + BODY_SEPARATOR + widgetId);
    }

    public Widget parseWidget(int expectedMessageOrder) throws Exception {
        return JsonParser.parseWidget(getBody(expectedMessageOrder));
    }

    public void deleteReport(int dashId, int reportId) {
        send(MOBILE_DELETE_REPORT, "" + dashId + BODY_SEPARATOR + reportId);
    }

    public void exportReport(int dashId, int reportId) {
        send(MOBILE_EXPORT_REPORT, "" + dashId + BODY_SEPARATOR + reportId);
    }

    public void getDevice(int dashId, int deviceId) {
        send(MOBILE_GET_DEVICE, "" + dashId + BODY_SEPARATOR + deviceId);
    }

    public void send(String line, int id) {
        send(produceMessageBaseOnUserInput(line, id));
    }

    public void getProvisionToken(Device device) {
        send(MOBILE_GET_PROVISION_TOKEN, device);
    }

    public void addPushToken(String uid, String token) {
        send(MOBILE_ADD_PUSH_TOKEN, uid + BODY_SEPARATOR + token);
    }

    public void editProfileSettings(ProfileSettings profileSettings) {
        send(MOBILE_EDIT_PROFILE_SETTINGS, profileSettings);
    }

    public void editDashboarSettings(int dashId, DashboardSettings dashboardSettings) {
        send(MOBILE_EDIT_PROJECT_SETTINGS, "" + dashId + BODY_SEPARATOR + dashboardSettings);
    }

    public void createApp(App app) {
        send(MOBILE_CREATE_APP, app);
    }

    public void loadProfileGzipped() {
        send(MOBILE_LOAD_PROFILE_GZIPPED);
    }

    public void loadProfileGzipped(int dashId) {
        send(MOBILE_LOAD_PROFILE_GZIPPED, dashId);
    }

    public void addEnergy(int energy, String transactionId) {
        send(MOBILE_ADD_ENERGY, "" + energy + BODY_SEPARATOR + transactionId);
    }

    public void replace(SimpleClientHandler simpleClientHandler) {
        this.channel.pipeline().removeLast();
        this.channel.pipeline().addLast(simpleClientHandler);
    }

}
