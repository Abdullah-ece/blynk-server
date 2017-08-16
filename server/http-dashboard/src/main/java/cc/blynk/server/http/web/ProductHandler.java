package cc.blynk.server.http.web;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.http.web.model.WebProductAndOrgId;
import cc.blynk.utils.ArrayUtil;
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
    private final DeviceDao deviceDao;

    public ProductHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
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
    public Response getProductById(@ContextUser User user, @PathParam("id") int productId) {
        Organization organization = organizationDao.getOrgById(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            return badRequest();
        }

        Product product = organizationDao.getProductById(productId);

        if (product == null) {
            log.error("Cannot find product with id {} for org {}", productId, organization.name);
            return badRequest();
        }

        int orgId = organizationDao.getOrganizationIdByProductId(product.id);
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access product he has no access.", user.email);
            return forbidden("You are not allowed to get this product.");
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

        if (organization.isSubOrg()) {
            log.error("You can't create products for sub organizations.");
            throw new ForbiddenWebException("You can't create products for sub organizations.");
        }

        if (!organization.isValidProductName(product)) {
            log.error("Organization {} already has product with name {}.", organization.name, product.name);
            return badRequest("Product with this name already exists.");
        }

        product.checkEvents();

        product = organizationDao.createProduct(webProductAndOrgId.orgId, product);

        return ok(product);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response updateProduct(WebProductAndOrgId webProductAndOrgId) {
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

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/updateDevices")
    public Response updateProductAndDevices(WebProductAndOrgId webProductAndOrgId) {
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
            device.updateMetaFields(updatedProduct.metaFields);

            MetaField[] addedMetaFields = ArrayUtil.substruct(updatedProduct.metaFields, device.metaFields);
            device.addMetaFields(addedMetaFields);

            MetaField[] deletedMetaFields = ArrayUtil.substruct(device.metaFields, updatedProduct.metaFields);
            device.deleteMetaFields(deletedMetaFields);
        }

        return ok(existingProduct);
    }

    @GET
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/canDeleteProduct/{id}")
    @Admin
    public Response canDeleteProduct(@PathParam("id") int productId) {
        if (deviceDao.productHasDevices(productId)) {
            return forbidden();
        }
        return ok();
    }

    @DELETE
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Admin
    public Response delete(@ContextUser User user, @PathParam("id") int productId) {
        if (organizationDao.deleteProduct(user, productId)) {
            return ok();
        } else {
            return notFound();
        }
    }
}
