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
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.handlers.decoders.WebAppMessageDecoder;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.stats.GlobalStats;
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
import org.mockito.Mockito;

import java.net.URI;
import java.util.Collections;
import java.util.Random;

import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

public final class AppWebSocketClient extends BaseTestAppClient {

    private final SslContext sslCtx;
    private final AppWebSocketClientHandler appHandler;
    public int msgId = 0;

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
        return new ChannelInitializer<SocketChannel> () {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(
                        sslCtx.newHandler(ch.alloc(), host, port),
                        new HttpClientCodec(),
                        new HttpObjectAggregator(8192),
                        appHandler,
                        new WebAppMessageDecoder(new GlobalStats(),
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

    public void track(int deviceId) {
        send("track " + deviceId);
    }

    public void resolveEvent(int deviceId, int logEventId) {
        send("resolveEvent " + deviceId + "\0" + logEventId);
    }

    public void resolveEvent(int deviceId, int logEventId, String comment) {
        send("resolveEvent " + deviceId + "\0" + logEventId + "\0" + comment);
    }

    public void send(String line) {
        send(produceWebSocketFrame(produceMessageBaseOnUserInput(line, ++msgId)));
    }

    public void reset() {
        Mockito.reset(responseMock);
        msgId = 0;
    }
}
