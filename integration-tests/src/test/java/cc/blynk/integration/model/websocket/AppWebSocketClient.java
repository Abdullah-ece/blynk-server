/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cc.blynk.integration.model.websocket;

import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.server.Limits;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.api.http.dashboard.dto.RoleDTO;
import cc.blynk.server.common.handlers.logic.timeline.ResolveEventDTO;
import cc.blynk.server.common.handlers.logic.timeline.TimelineDTO;
import cc.blynk.server.common.handlers.logic.timeline.TimelineResponseDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.dto.OtaDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.UserInviteDTO;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.processors.rules.RuleGroup;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.web.handlers.logic.organization.dto.CountDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.LocationDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.OrganizationsHierarchyDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.SetAuthTokenDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.TokenDTO;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.properties.ServerProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.LOGOUT;
import static cc.blynk.server.core.protocol.enums.Command.RESET_PASSWORD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CAN_DELETE_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CAN_INVITE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_DEVICES_META_IN_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_DEVICE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_OWN_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_RULE_GROUP;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_USER_INFO;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE_COUNT_FOR_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE_TIMELINE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORGS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_HIERARCHY;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_USERS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCTS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCT_LOCATIONS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ROLES;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_TEMP_SECURE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_USER_COUNTERS_BY_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_INVITE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_LOGIN_VIA_INVITE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_GET_FIRMWARE_INFO;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_START;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_STOP;
import static cc.blynk.server.core.protocol.enums.Command.WEB_RESOLVE_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SET_AUTH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_TRACK_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_TRACK_ORG;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR_STRING;

public final class AppWebSocketClient extends BaseTestAppClient {

    private final SslContext sslCtx;
    private final AppWebSocketClientHandler appHandler;

    public AppWebSocketClient(String host, int port, String path) throws Exception {
        super(host, port, new Random(), new ServerProperties(Collections.emptyMap()));

        URI uri = new URI("wss://" + host + ":" + port + path);
        this.sslCtx = SslContextBuilder.forClient()
                .sslProvider(SslProvider.JDK)
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        this.appHandler = new AppWebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
    }

    private static WebSocketFrame produceWebSocketFrame(MessageBase msg) {
        byte[] data = msg.getBytes();
        ByteBuf bb = ByteBufAllocator.DEFAULT.buffer(3 + data.length);
        bb.writeByte(msg.command);
        bb.writeShort(msg.id);
        bb.writeBytes(data);
        return new BinaryWebSocketFrame(bb);
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<> () {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(
                        sslCtx.newHandler(ch.alloc(), host, port),
                        new HttpClientCodec(),
                        new HttpObjectAggregator(8192),
                        appHandler,
                        new WebClientAppMessageDecoder(new GlobalStats(),
                                new Limits(new ServerProperties(Collections.emptyMap()))
                        )
                );
            }
        };
    }

    @Override
    public void start() {
        super.start();
        startHandshake();
    }

    private void startHandshake() {
        appHandler.startHandshake(channel);
        try {
            appHandler.handshakeFuture().sync();
            this.channel.pipeline().addLast(responseMock);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void login(User user, String type, String version) {
        //user.pass is hashed password here actually
        send(LOGIN, user.email + BODY_SEPARATOR + user.pass + BODY_SEPARATOR + type + BODY_SEPARATOR + version);
    }

    public void login(User user) {
        login(user, "ws", "1.0.0");
    }

    public void login(String email, String pass) {
        send(LOGIN, email + StringUtils.BODY_SEPARATOR + SHA256Util.makeHash(pass, email));
    }

    public void trackDevice(int deviceId) {
        send(WEB_TRACK_DEVICE, deviceId);
    }

    public void resolveEvent(int deviceId, long logEventId) {
        resolveEvent(deviceId, logEventId, null);
    }

    public void resolveEvent(int deviceId, long logEventId, String comment) {
        send(WEB_RESOLVE_EVENT, new ResolveEventDTO(deviceId, logEventId, comment));
    }

    public void editRuleGroup(RuleGroup ruleGroup) {
        send(WEB_EDIT_RULE_GROUP, JsonParser.toJson(ruleGroup));
    }

    public void createOrganization(Organization organization) {
        send(WEB_CREATE_ORG, "-1" + BODY_SEPARATOR_STRING + organization);
    }

    public void canDeleteProduct(int productId) {
        send(WEB_CAN_DELETE_PRODUCT, productId);
    }

    public void createProduct(Product product) {
        createProduct(new ProductDTO(product));
    }
    public void updateProduct(int orgId, Product product) {
        updateProduct(orgId, new ProductDTO(product));
    }

    public void createProduct(ProductDTO product) {
        send(WEB_CREATE_PRODUCT, new ProductAndOrgIdDTO(-1, product));
    }

    public void updateProduct(int orgId, ProductDTO product) {
        send(WEB_EDIT_PRODUCT, new ProductAndOrgIdDTO(orgId, product));
    }

    public void updateDevicesMeta(int orgId, ProductDTO product) {
        send(WEB_EDIT_DEVICES_META_IN_PRODUCT, new ProductAndOrgIdDTO(orgId, product));
    }

    public void getProducts(int orgId) {
        send(WEB_GET_PRODUCTS, orgId);
    }

    public void getProduct(int productId) {
        send(WEB_GET_PRODUCT, + productId);
    }

    public void inviteUser(int orgId, String email, String name, int roleId) {
        send(WEB_INVITE_USER, "" + orgId + BODY_SEPARATOR_STRING + new UserInviteDTO(email, name, roleId));
    }

    public void canInviteUser(String email) {
        send(WEB_CAN_INVITE_USER, email);
    }

    public void loginViaInvite(String token, String passHash) {
        send(WEB_LOGIN_VIA_INVITE, token + BODY_SEPARATOR_STRING + passHash);
    }

    public ProductDTO parseProductDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), ProductDTO.class);
    }

    public ProductDTO[] parseProductDTOs(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), ProductDTO[].class);
    }

    public RoleDTO parseRoleDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), RoleDTO.class);
    }

    public RoleDTO[] parseRoleDTOs(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), RoleDTO[].class);
    }

    public Map<Integer, Integer> parseUserCountersPerRole(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), new TypeReference<HashMap<Integer, Integer>>() {});
    }

    public void deleteProduct(int productId) {
        send(WEB_DELETE_PRODUCT, productId);
    }

    public void updateAccount(User user) {
        send(WEB_EDIT_ACCOUNT, user);
    }

    public void getAccount() {
        send(WEB_GET_ACCOUNT);
    }

    public void logout() {
        send(LOGOUT);
    }

    public void deleteDevice(int orgId, int deviceId) {
        send(WEB_DELETE_DEVICE, "" + orgId + BODY_SEPARATOR + deviceId);
    }

    public void createDevice(Device device) {
        send(WEB_CREATE_DEVICE, "" + -1 + BODY_SEPARATOR + device);
    }

    public void updateDevice(int orgId, Device device) {
        send(WEB_EDIT_DEVICE, "" + orgId + BODY_SEPARATOR + device);
    }

    public void getDevice(int orgId, int deviceId) {
        send(WEB_GET_DEVICE, "" + orgId + BODY_SEPARATOR_STRING + deviceId);
    }

    public void getDevices(int orgId) {
        send(WEB_GET_DEVICES, orgId);
    }

    public void getProductLocations(int productId) {
        send(WEB_GET_PRODUCT_LOCATIONS, productId);
    }

    public void getProductLocations(int productId, String searchString) {
        send(WEB_GET_PRODUCT_LOCATIONS, "" + productId + BODY_SEPARATOR + searchString);
    }

    public void getMetafield(int deviceId, int metafieldId) {
        send(WEB_GET_METAFIELD, "" + deviceId + BODY_SEPARATOR + metafieldId);
    }

    public void deleteOrg(int orgId) {
        send(WEB_DELETE_ORG, orgId);
    }

    public void getOrganization() {
        send(WEB_GET_ORG);
    }

    public void getOrganization(int orgId) {
        send(WEB_GET_ORG, orgId);
    }

    public void getOrganizationHierarchy() {
        send(WEB_GET_ORG_HIERARCHY);
    }

    public void getOrganizations(int orgId) {
        send(WEB_GET_ORGS, orgId);
    }

    public void getOrgUsers(int orgId) {
        send(WEB_GET_ORG_USERS, orgId);
    }

    public void editOwnOrg(OrganizationDTO organizationDTO) {
        send(WEB_EDIT_OWN_ORG, organizationDTO);
    }

    public void deleteUser(int orgId, String... users) {
        send(WEB_DELETE_USER, "" + orgId + StringUtils.BODY_SEPARATOR_STRING
                + JsonParser.valueToJsonAsString(Arrays.asList(users)));
    }

    public void getTimeline(int deviceId,
                            EventType eventType, Boolean isResolved,
                            long form, long to,
                            int offset, int limit) {
        send(WEB_GET_DEVICE_TIMELINE, new TimelineDTO(deviceId, eventType, isResolved, form, to, offset, limit));
    }

    public void updateDeviceMetafield(int deviceId, MetaField metaField) {
        send(WEB_EDIT_DEVICE_METAFIELD, "" + deviceId + BODY_SEPARATOR_STRING + metaField);
    }

    public void updateUserInfo(int orgId, User user) {
        send(WEB_EDIT_USER_INFO, "" + orgId + BODY_SEPARATOR_STRING + user.toString());
    }

    public void getTempSecureToken() {
        send(WEB_GET_TEMP_SECURE_TOKEN);
    }

    public void createRole(RoleDTO roleDTO) {
        send(WEB_CREATE_ROLE, roleDTO);
    }

    public void updateRole(RoleDTO roleDTO) {
        send(WEB_EDIT_ROLE, roleDTO);
    }

    public void deleteRole(int roleId) {
        send(WEB_DELETE_ROLE, roleId);
    }

    public void getRoles() {
        send(WEB_GET_ROLES);
    }

    public void getRole(int roleId) {
        send(WEB_GET_ROLE, roleId);
    }

    public void getUserCountersByRole() {
        send(WEB_GET_USER_COUNTERS_BY_ROLE);
    }

    public void setAuthToken(int deviceId, String newToken) {
        send(WEB_SET_AUTH_TOKEN, JsonParser.toJson(new SetAuthTokenDTO(deviceId, newToken)));
    }

    public void getDeviceCount(int orgId) {
        send(WEB_GET_DEVICE_COUNT_FOR_ORG, "" + orgId);
    }

    public void trackOrg(int orgId) {
        send(WEB_TRACK_ORG, "" + orgId);
    }

    public TimelineResponseDTO parseTimelineResponse(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), TimelineResponseDTO.class);
    }

    public User[] parseUsers(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), User[].class);
    }

    public OrganizationsHierarchyDTO parseOrganizationHierarchyDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), OrganizationsHierarchyDTO.class);
    }

    public OrganizationDTO parseOrganizationDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.parseOrgDTO(getBody(expectedMessageOrder), 0);
    }

    public OrganizationDTO[] parseOrganizations(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), OrganizationDTO[].class);
    }

    public Device parseDevice(int expectedMessageOrder) throws Exception {
        return JsonParser.parseDevice(getBody(expectedMessageOrder), 0);
    }

    public DeviceDTO[] parseDevicesDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), DeviceDTO[].class);
    }

    public LocationDTO[] parseLocationsDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), LocationDTO[].class);
    }

    public User parseAccount(int expectedMessageOrder) throws Exception {
        return JsonParser.parseUserFromString(getBody(expectedMessageOrder));
    }

    public MetaField parseMetafield(int expectedMessageOrder) throws Exception {
        return JsonParser.parseMetafield(getBody(expectedMessageOrder), 0);
    }

    public String parseString(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), String.class);
    }

    public TokenDTO parseToken(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), TokenDTO.class);
    }

    public CountDTO parseCountDTO(int expectedMessageOrder) throws Exception {
        return JsonParser.MAPPER.readValue(getBody(expectedMessageOrder), CountDTO.class);
    }

    @Override
    public void send(short command) {
        send(produceWebSocketFrame(produceMessage(command, ++msgId, "")));
    }

    @Override
    public void send(short command, Object body) {
        send(produceWebSocketFrame(produceMessage(command, ++msgId, body.toString())));
    }

    public void hardware(int deviceId, String body) {
        send(HARDWARE, "" + deviceId + BODY_SEPARATOR_STRING + body);
    }

    public void resetPass(String command) {
        send(RESET_PASSWORD, command);
    }

    public void resetPass(String command, String email, String appName) {
        send(RESET_PASSWORD, command + BODY_SEPARATOR_STRING + email + BODY_SEPARATOR_STRING + appName);
    }

    public void resetPassReset(String token, String hash) {
        send(RESET_PASSWORD, "reset" + BODY_SEPARATOR_STRING + token + BODY_SEPARATOR_STRING + hash);
    }

    public void getGraphData(int deviceId, long widgetId, Period period) {
        send(GET_SUPERCHART_DATA, "" + deviceId + BODY_SEPARATOR_STRING + widgetId + BODY_SEPARATOR_STRING + period);
    }

    public void getGraphDataCustom(int deviceId, long widgetId, long from, long to) {
        send(GET_SUPERCHART_DATA, "" + deviceId + BODY_SEPARATOR_STRING
                + widgetId + BODY_SEPARATOR_STRING + Period.CUSTOM
                + BODY_SEPARATOR_STRING + from + BODY_SEPARATOR_STRING + to);
    }

    public void getOTAInfo(String firmwareUrl) {
        if (firmwareUrl == null) {
            send(WEB_OTA_GET_FIRMWARE_INFO);
        } else {
            send(WEB_OTA_GET_FIRMWARE_INFO, firmwareUrl);
        }
    }

    public void otaStart(OtaDTO otaDTO) {
        send(WEB_OTA_START, otaDTO);
    }

    public void otaStop(OtaDTO otaDTO) {
        send(WEB_OTA_STOP, otaDTO);
    }

    public void getDevicesByReferenceMetafield(int deviceId, int metafieldId) {
        send(WEB_GET_DEVICES_BY_REFERENCE_METAFIELD, "" + deviceId + BODY_SEPARATOR + metafieldId);
    }


}
