package cc.blynk.server.http.web;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.*;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.http.web.model.WebProductAndOrgId;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final UserDao userDao;
    private final DeviceDao deviceDao;

    public ProductHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
        this.userDao = holder.userDao;
        this.deviceDao = holder.deviceDao;
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

        return ok(calcDeviceCount(organization));
    }

    @GET
    @Path("/{id}")
    public Response getAll(@Context ChannelHandlerContext ctx, @PathParam("id") int productId) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        Organization organization = organizationDao.getOrgById(httpSession.user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", httpSession.user.orgId, httpSession.user.email);
            return badRequest();
        }

        //Map<Integer, Integer> productIdCount = productDeviceCount(organization);
        //product.deviceCount = productIdCount.getOrDefault(productId, 0);

        Product product = organization.getProduct(productId);
        if (product == null) {
            log.error("Cannot find product with id {}", productId);
            return badRequest();
        }

        return ok(product);
    }

    private Product[] calcDeviceCount(Organization org) {
        Map<Integer, Integer> productIdCount = productDeviceCount();
        for (Product product : org.products) {
            product.deviceCount = productIdCount.getOrDefault(product.id, 0);
        }
        return org.products;
    }

    private Map<Integer, Integer> productDeviceCount() {
        Map<Integer, Integer> productIdCount =  new HashMap<>();
        for (Device device : deviceDao.getAll()) {
            Integer count = productIdCount.getOrDefault(device.productId, 0);
            productIdCount.put(device.productId, count + 1);
        }
        return productIdCount;
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response create(WebProductAndOrgId webProductAndOrgId) {
        Product product = webProductAndOrgId.product;

        if (product == null || product.notValid()) {
            log.error("Product is empty or has not name. {}", product);
            return badRequest("Product is empty or has not name.");
        }

        Organization organization = organizationDao.getOrgById(webProductAndOrgId.orgId);

        if (!organization.isValidProductName(product)) {
            log.error("Organization {} already has product with name {}.", organization.name, product.name);
            return badRequest("Product with this name already exists.");
        }

        product = organizationDao.createProduct(webProductAndOrgId.orgId, product);

        return ok(product);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response updateProduct(@Context ChannelHandlerContext ctx, WebProductAndOrgId webProductAndOrgId) {
        Product updatedProduct = webProductAndOrgId.product;

        if (updatedProduct == null) {
            log.error("No product for update.");
            return badRequest();
        }

        if (updatedProduct.notValid()) {
            log.error("Product is not valid.", updatedProduct);
            return badRequest();
        }

        Organization organization = organizationDao.getOrgById(webProductAndOrgId.orgId);

        if (!organization.isValidProductName(updatedProduct)) {
            log.error("Organization {} already has product with name {}.", organization.name, updatedProduct.name);
            return badRequest("Product with this name already exists.");
        }

        Product existingProduct = organizationDao.getProduct(webProductAndOrgId.orgId, updatedProduct.id);
        existingProduct.update(updatedProduct);

        return ok(existingProduct);
    }

    //todo cover with test
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/updateDevices")
    public Response updateProductAndDevices(@Context ChannelHandlerContext ctx, WebProductAndOrgId webProductAndOrgId) {
        Product updatedProduct = webProductAndOrgId.product;
        if (updatedProduct == null) {
            log.error("No product for update.");
            return badRequest();
        }

        Product existingProduct = organizationDao.getProduct(webProductAndOrgId.orgId, updatedProduct.id);

        if (updatedProduct.notValid()) {
            log.error("Product is not valid.", updatedProduct);
            return badRequest();
        }

        existingProduct.update(updatedProduct);

        //todo persist?
        List<Device> devices = deviceDao.getAllByProductId(updatedProduct.id);
        for (Device device : devices) {
            device.metaFields = updatedProduct.copyMetaFields();
        }

        return ok(existingProduct);
    }

    @DELETE
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Admin
    public Response delete(@Context ChannelHandlerContext ctx, @PathParam("id") int productId) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        if (organizationDao.deleteProduct(httpSession.user, productId)) {
            return ok();
        } else {
            return notFound();
        }
    }
}
