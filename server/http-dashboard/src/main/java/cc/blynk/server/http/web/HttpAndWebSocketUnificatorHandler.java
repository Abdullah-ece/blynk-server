package cc.blynk.server.http.web;

import cc.blynk.core.http.handlers.NoMatchHandler;
import cc.blynk.core.http.handlers.UploadHandler;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.handlers.LetsEncryptHandler;
import cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Utility handler used to define what protocol should be handled
 * on same port : http or websockets.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.02.17.
 */
@ChannelHandler.Sharable
public class HttpAndWebSocketUnificatorHandler extends ChannelInboundHandlerAdapter implements DefaultExceptionHandler {

    private final AuthCookieHandler authCookieHandler;

    private final NoMatchHandler noMatchHandler;

    private final WebLoginHandler webLoginHandler;
    private final AccountHandler accountHandler;
    private final DevicesHandler devicesHandler;
    private final DataHandler dataHandler;
    private final ProductHandler productHandler;
    private final OrganizationHandler organizationHandler;

    private final ServerProperties props;

    public HttpAndWebSocketUnificatorHandler(Holder holder, String rootPath) {
        this.noMatchHandler = new NoMatchHandler();

        this.webLoginHandler = new WebLoginHandler(holder, rootPath);
        this.authCookieHandler = new AuthCookieHandler(holder.sessionDao);
        this.accountHandler = new AccountHandler(holder, rootPath);
        this.devicesHandler = new DevicesHandler(holder, rootPath);
        this.dataHandler = new DataHandler(holder, rootPath);
        this.productHandler = new ProductHandler(holder, rootPath);
        this.organizationHandler = new OrganizationHandler(holder, rootPath);

        this.props = holder.props;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        initUserPipeline(ctx);
        ctx.fireChannelRead(msg);
    }

    private void initUserPipeline(ChannelHandlerContext ctx) {
        ctx.pipeline()
        .addLast(webLoginHandler)
        .addLast(authCookieHandler)
        .addLast(new UploadHandler(props.jarPath, "/api/upload", "/" + ServerProperties.STATIC_FILES_FOLDER))
        .addLast(accountHandler)
        .addLast(devicesHandler)
        .addLast(dataHandler)
        .addLast(productHandler)
        .addLast(organizationHandler)
        .addLast(noMatchHandler)
        .remove(this)
        .remove(LetsEncryptHandler.class);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }
}
