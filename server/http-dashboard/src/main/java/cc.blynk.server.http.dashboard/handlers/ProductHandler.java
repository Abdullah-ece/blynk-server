package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.ProductDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.web.product.Product;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/product")
@ChannelHandler.Sharable
public class ProductHandler extends BaseHttpHandler {

    private final ProductDao productDao;

    public ProductHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.productDao = holder.productDao;
    }

    @GET
    @Path("/list")
    public Response getAllProductsOfOwnOrganization(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        return ok(httpSession.user.organization.products);
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response createProduct(@Context ChannelHandlerContext ctx, Product product) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        product = productDao.createProduct(product);
        httpSession.user.organization.products.add(product);
        return ok(product);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response updateProduct(@Context ChannelHandlerContext ctx, Product updatedProduct) {
        if (updatedProduct == null) {
            log.error("No product for update.");
            return badRequest();
        }

        Product existingProduct = productDao.getById(updatedProduct.id);

        if (existingProduct == null) {
            log.error("Product with passed is {} not found.", updatedProduct.id);
            return badRequest();
        }

        existingProduct.update(updatedProduct);

        return ok(existingProduct);
    }

}
