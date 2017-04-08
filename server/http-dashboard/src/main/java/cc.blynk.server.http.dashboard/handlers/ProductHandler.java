package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.core.http.Response.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/product")
@ChannelHandler.Sharable
public class ProductHandler extends BaseHttpHandler {

    private final OrganizationDao organizationDao;

    public ProductHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
    }

    @GET
    @Path("")
    public Response getAll(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        Organization organization = organizationDao.getOrgById(httpSession.user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", httpSession.user.orgId, httpSession.user.email);
            return badRequest();
        }

        return ok(organization.products);
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response create(@Context ChannelHandlerContext ctx, Product product) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        product = organizationDao.addProduct(httpSession.user.orgId, product);

        if (product == null) {
            log.error("Cannot find org with id {}", httpSession.user.orgId);
            return badRequest();
        }

        return ok(product);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response update(@Context ChannelHandlerContext ctx, Product updatedProduct) {
        if (updatedProduct == null) {
            log.error("No product for update.");
            return badRequest();
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        Product existingProduct = organizationDao.getProduct(httpSession.user.orgId, updatedProduct.id);

        if (existingProduct == null) {
            log.error("Product with passed is {} not found.", updatedProduct.id);
            return badRequest();
        }

        existingProduct.update(updatedProduct);

        return ok(existingProduct);
    }

    @DELETE
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(@Context ChannelHandlerContext ctx, @PathParam("id") int productId) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        if (!httpSession.user.isAdmin()) {
            log.error("Only admins can delete products. User {} not admin.", httpSession.user.email);
            return forbidden();
        }

        int orgId = httpSession.user.orgId;
        Product existingProduct = organizationDao.getProduct(orgId, productId);

        if (existingProduct == null) {
          log.error("Product with passed is {} not found in organization with id {}.", productId, orgId);
          return badRequest();
        }

        organizationDao.deleteProduct(orgId, existingProduct);
        return ok();
    }
}
