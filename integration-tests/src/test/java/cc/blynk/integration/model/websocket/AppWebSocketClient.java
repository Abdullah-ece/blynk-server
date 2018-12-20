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
import cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.web.handlers.logic.device.timeline.TimelineDTO;
import cc.blynk.server.web.handlers.logic.device.timeline.TimelineResponseDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.CountDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.LocationDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.OrganizationsHierarchyDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.SetAuthTokenDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.TokenDTO;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.properties.ServerProperties;
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
import java.util.Random;

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
        send("login " + user.email + BODY_SEPARATOR + user.pass + BODY_SEPARATOR + type + BODY_SEPARATOR + version);
    }

    public void login(User user) {
        login(user, "ws", "1.0.0");
    }

    public void login(String email, String pass) {
        send("login " + email + StringUtils.BODY_SEPARATOR + SHA256Util.makeHash(pass, email));
    }

    public void trackDevice(int deviceId) {
        send("trackDevice " + deviceId);
    }

    public void resolveEvent(int deviceId, long logEventId) {
        send("resolveEvent " + deviceId + "\0" + logEventId);
    }

    public void resolveEvent(int deviceId, long logEventId, String comment) {
        send("resolveEvent " + deviceId + "\0" + logEventId + "\0" + comment);
    }

    public void createOrganization(int orgId, Organization organization) {
        send("webCreateOrg " + orgId + StringUtils.BODY_SEPARATOR_STRING + organization);
    }

    public void canDeleteProduct(int orgId, int productId) {
        send("webCanDeleteProduct " + orgId + BODY_SEPARATOR_STRING + productId);
    }

    public void canDeleteProduct(int productId) {
        send("webCanDeleteProduct " + productId);
    }

    //todo remove in future
    public void createProduct(int orgId, Product product) {
        createProduct(orgId, new ProductDTO(product));
    }
    public void updateProduct(int orgId, Product product) {
        updateProduct(orgId, new ProductDTO(product));
    }
    public void updateDevicesMeta(int orgId, Product product) {
        updateDevicesMeta(orgId, new ProductDTO(product));
    }

    public void createProduct(int orgId, ProductDTO product) {
        send("webCreateProduct " + new ProductAndOrgIdDTO(orgId, product));
    }

    public void updateProduct(int orgId, ProductDTO product) {
        send("webUpdateProduct " + new ProductAndOrgIdDTO(orgId, product));
    }

    public void updateDevicesMeta(int orgId, ProductDTO product) {
        send("webUpdateDevicesMeta " + new ProductAndOrgIdDTO(orgId, product));
    }

    public void getProducts(int orgId) {
        send("webGetProducts " + orgId);
    }

    public void getProduct(int productId) {
        send("webGetProduct " + productId);
    }

    public void inviteUser(int orgId, String email, String name, int roleId) {
        send("webInviteUser " + orgId + BODY_SEPARATOR_STRING + new UserInviteDTO(email, name, roleId));
    }

    public void canInviteUser(String email) {
        send("canInviteUser " + email);
    }

    public void loginViaInvite(String token, String passHash) {
        send("webLoginViaInvite " + token + BODY_SEPARATOR_STRING + passHash);
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

    public void deleteProduct(int productId) {
        send("webDeleteProduct " + productId);
    }

    public void updateAccount(User user) {
        send("updateAccount " + user);
    }

    public void getAccount() {
        send("getAccount");
    }

    public void logout() {
        send("logout");
    }

    public void deleteDevice(int orgId, int deviceId) {
        send("webDeleteDevice " + orgId + BODY_SEPARATOR + deviceId);
    }

    public void createDevice(int orgId, Device device) {
        send("webCreateDevice " + orgId + BODY_SEPARATOR + device);
    }

    public void updateDevice(int orgId, Device device) {
        send("webUpdateDevice " + orgId + BODY_SEPARATOR + device);
    }

    public void getDevice(int orgId, int deviceId) {
        send("webGetDevice " + orgId + BODY_SEPARATOR_STRING + deviceId);
    }

    public void getDevices(int orgId) {
        send("webGetDevices " + orgId);
    }

    public void getProductLocations(int productId) {
        send("webGetProductLocations " + productId);
    }

    public void getProductLocations(int productId, String searchString) {
        send("webGetProductLocations " + productId + BODY_SEPARATOR + searchString);
    }

    public void getMetafield(int deviceId, int metafieldId) {
        send("webGetMetafield " + deviceId + BODY_SEPARATOR + metafieldId);
    }

    public void deleteOrg(int orgId) {
        send("webDeleteOrg " + orgId);
    }

    public void getOrganization(int orgId) {
        send("webGetOrg " + orgId);
    }

    public void getOrganizationHierarchy() {
        send("getOrganizationHierarchy");
    }

    public void getOrganizations(int orgId) {
        send("webGetOrgs " + orgId);
    }

    public void getOrgUsers(int orgId) {
        send("webGetOrgUsers " + orgId);
    }

    public void getOrgLocations(int orgId) {
        send("webGetOrgLocations " + orgId);
    }

    public void editOwnOrg(OrganizationDTO organizationDTO) {
        send("webEditOwnOrg " + organizationDTO);
    }

    public void deleteUser(int orgId, String... users) {
        send("webDeleteUser " + orgId + StringUtils.BODY_SEPARATOR_STRING
                + JsonParser.valueToJsonAsString(Arrays.asList(users)));
    }

    public void getTimeline(int orgId, int deviceId,
                            EventType eventType, Boolean isResolved,
                            long form, long to,
                            int offset, int limit) {
        send("webgetdevicetimeline " +
                new TimelineDTO(orgId, deviceId, eventType, isResolved, form, to, offset, limit).toString());
    }

    public void updateDeviceMetafield(int deviceId, MetaField metaField) {
        send("webUpdateDeviceMetafield " + deviceId + BODY_SEPARATOR_STRING + metaField);
    }

    public void updateUserInfo(int orgId, User user) {
        send("WebUpdateUserInfo " + orgId + BODY_SEPARATOR_STRING + user.toString());
    }

    public void getTempSecureToken() {
        send("webGetTempSecureToken");
    }

    public void createRole(RoleDTO roleDTO) {
        send("webCreateRole " + roleDTO.toString());
    }

    public void updateRole(RoleDTO roleDTO) {
        send("webUpdateRole " + roleDTO.toString());
    }

    public void deleteRole(int roleId) {
        send("webDeleteRole " + roleId);
    }

    public void getRoles() {
        send("webGetRoles");
    }

    public void getRole(int roleId) {
        send("webGetRole " + roleId);
    }

    public void setAuthToken(int deviceId, String newToken) {
        send("webSetAuthToken " + JsonParser.toJson(new SetAuthTokenDTO(deviceId, newToken)));
    }

    public void getDeviceCount(int orgId) {
        send("webGetDeviceCountForOrg " + orgId);
    }

    public void trackOrg(int orgId) {
        send("webTrackOrg " + orgId);
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

    public void send(String line) {
        send(produceWebSocketFrame(produceMessageBaseOnUserInput(line, ++msgId)));
    }

    public void getGraphData(int deviceId, long widgetId, GraphPeriod graphPeriod) {
        send("getenhanceddata " + deviceId + BODY_SEPARATOR_STRING + widgetId + BODY_SEPARATOR_STRING + graphPeriod);
    }

    public void getGraphDataCustom(int deviceId, long widgetId, long from, long to) {
        send("getenhanceddata " + deviceId + BODY_SEPARATOR_STRING
                + widgetId + BODY_SEPARATOR_STRING + GraphPeriod.CUSTOM
                + BODY_SEPARATOR_STRING + from + BODY_SEPARATOR_STRING + to);
    }

    public void getOTAInfo(String firmwareUrl) {
        if (firmwareUrl == null) {
            send("webOtaGetFirmwareInfo");
        } else {
            send("webOtaGetFirmwareInfo " + firmwareUrl);
        }
    }

    public void otaStart(OtaDTO otaDTO) {
        send("webStartOta " + otaDTO);
    }

    public void otaStop(OtaDTO otaDTO) {
        send("webStopOta " + otaDTO);
    }

}
