package cc.blynk.server.http.web;

import cc.blynk.core.http.Response;
import cc.blynk.core.http.TokenBaseHttpHandler;
import cc.blynk.core.http.annotation.Context;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.core.http.annotation.QueryParam;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.TokenValue;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.db.DBManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.ok;
import static cc.blynk.core.http.Response.serverError;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.12.15.
 */
@Path("")
@ChannelHandler.Sharable
public class ExternalAPIHandler extends TokenBaseHttpHandler {

    private static final Logger log = LogManager.getLogger(ExternalAPIHandler.class);
    private final BlockingIOProcessor blockingIOProcessor;
    private final OrganizationDao organizationDao;
    private final DBManager dbManager;

    ExternalAPIHandler(Holder holder) {
        super(holder.tokenManager, holder.sessionDao, holder.stats, "/external/api");
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.organizationDao = holder.organizationDao;
        this.dbManager = holder.dbManager;
    }

    @GET
    @Path("/{token}/logEvent")
    public Response logEvent(@Context ChannelHandlerContext ctx,
                             @PathParam("token") String token,
                             @QueryParam("code") String eventCode,
                             @QueryParam("description") String description) {

        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

        Device device = tokenValue.device;

        Product product = organizationDao.getProductByIdOrNull(device.productId);
        if (product == null) {
            log.error("Product with id {} not exists.", device.productId);
            return (badRequest("Product not exists for device."));
        }

        if (eventCode == null) {
            log.error("Event code is not provided.");
            return (badRequest("Event code is not provided."));
        }

        Event event = product.findEventByCode(eventCode.hashCode());

        if (event == null) {
            log.error("Event with code {} not found in product {}.", eventCode, product.id);
            return badRequest("Event with code not found in product.");
        }

        blockingIOProcessor.executeDB(() -> {
            try {
                long now = System.currentTimeMillis();
                dbManager.insertEvent(device.id, event.getType(), now, eventCode.hashCode(), description);
                device.dataReceivedAt = now;
                ctx.writeAndFlush(ok(), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error inserting log event.", e);
                ctx.writeAndFlush(serverError("Error inserting log event."), ctx.voidPromise());
            }
        });

        return null;
    }

}
