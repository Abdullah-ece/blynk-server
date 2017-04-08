/*
 * Copyright 2012 The Netty Project
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
package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.Response;
import cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler;
import cc.blynk.utils.ServerProperties;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.*;

public class UploadLogic extends SimpleChannelInboundHandler<HttpObject> implements DefaultExceptionHandler {

    private static final Logger log = LogManager.getLogger(UploadLogic.class);

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);
    private final String reqPath;
    private HttpPostRequestDecoder decoder;

    public UploadLogic(String reqPath) {
        super(false);
        this.reqPath = reqPath;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (!req.uri().startsWith(reqPath)) {
                ctx.fireChannelRead(msg);
                return;
            }

            try {
              log.debug("Incoming {}", req);
                decoder = new HttpPostRequestDecoder(factory, req);
            } catch (ErrorDataDecoderException e) {
                log.error("Error creating http post request decoder.", e);
                ctx.writeAndFlush(Response.badRequest(e.getMessage()));
                return;
            }

        }

        if (decoder != null && msg instanceof HttpContent) {
                // New chunk is received
            HttpContent chunk = (HttpContent) msg;
            try {
                decoder.offer(chunk);
            } catch (ErrorDataDecoderException e) {
                log.error("Error creating http post offer.", e);
                ctx.writeAndFlush(Response.badRequest(e.getMessage()));
                return;
            }

            // example of reading only if at the end
            if (chunk instanceof LastHttpContent) {
                String pathTo = "/static/";
                try {
                    while (decoder.hasNext()) {
                        InterfaceHttpData data = decoder.next();
                        if (data != null) {
                            if (data instanceof DiskFileUpload) {
                                DiskFileUpload diskFileUpload = (DiskFileUpload) data;
                                Path tmpFile = diskFileUpload.getFile().toPath();
                                String finalName = tmpFile.getFileName().toString();

                                //this is just to make it work on team city.
                                Files.createDirectories(Paths.get(ServerProperties.staticFilesFolder));

                                Files.move(tmpFile, Paths.get(ServerProperties.staticFilesFolder, finalName), StandardCopyOption.REPLACE_EXISTING);
                                pathTo += finalName;
                            }
                            data.release();
                        }
                    }
                } catch (EndOfDataDecoderException endOfData) {
                    //ignore. that's fine.
                } catch (NoSuchFileException e) {
                    log.error("Unable to copy uploaded image to static folder. Reason : {}", e.getMessage());
                }

                if (!pathTo.equals("/static/")) {
                    ctx.writeAndFlush(Response.ok(pathTo));
                } else {
                    ctx.writeAndFlush(Response.serverError());
                }

                // destroy the decoder to release all resources
                decoder.destroy();
                decoder = null;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }
}
