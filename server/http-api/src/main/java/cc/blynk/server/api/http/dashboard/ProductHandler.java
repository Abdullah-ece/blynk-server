package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Admin;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.ContextUser;
import cc.blynk.core.http.annotation.DELETE;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.PUT;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.WebException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.http.MediaType;
import io.netty.channel.ChannelHandler;

import java.util.List;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.forbidden;
import static cc.blynk.core.http.Response.notFound;
import static cc.blynk.core.http.Response.ok;

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
    public Response getAll(@ContextUser User user) {
        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            return badRequest();
        }

        organizationDao.calcDeviceCount(organization);

        return ok(organization.products);
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@ContextUser User user, @PathParam("id") int productId) {
        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            return badRequest();
        }

        Product product = organizationDao.getProductByIdOrThrow(productId);

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

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response create(ProductAndOrgIdDTO productAndOrgIdDTO) {
        Product product = productAndOrgIdDTO.product;

        if (product == null || product.notValid()) {
            log.error("Product is empty or has not name. {}", product);
            return badRequest("Product is empty or has not name.");
        }

        Organization organization = organizationDao.getOrgByIdOrThrow(productAndOrgIdDTO.orgId);

        if (organization.isSubOrg()) {
            log.error("You can't create products for sub organizations.");
            throw new ForbiddenWebException("You can't create products for sub organizations.");
        }

        if (!organization.isValidProductName(product)) {
            log.error("Organization {} already has product with name {}.", organization.name, product.name);
            return badRequest("Product with this name already exists.");
        }

        if (!product.isValidEvents()) {
            throw new WebException("Events with this event codes are not allowed.");
        }

        product = organizationDao.createProduct(productAndOrgIdDTO.orgId, product);

        return ok(product);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response updateProduct(ProductAndOrgIdDTO productAndOrgIdDTO) {
        Product updatedProduct = productAndOrgIdDTO.product;

        if (updatedProduct == null) {
            log.error("No product for update.");
            return badRequest();
        }

        if (updatedProduct.notValid()) {
            log.error("Product is not valid.", updatedProduct);
            return badRequest();
        }

        Organization organization = organizationDao.getOrgByIdOrThrow(productAndOrgIdDTO.orgId);

        if (!organization.isValidProductName(updatedProduct)) {
            log.error("Organization {} already has product with name {}.", organization.name, updatedProduct.name);
            return badRequest("Product with this name already exists.");
        }

        Product existingProduct = organizationDao.getProduct(productAndOrgIdDTO.orgId, updatedProduct.id);

        if (!existingProduct.webDashboard.equals(updatedProduct.webDashboard)) {
            log.info("Dashboard was changed. Updating all devices.");
            List<Device> devices = deviceDao.getAllByProductId(updatedProduct.id);
            long now = System.currentTimeMillis();
            for (Device device : devices) {
                device.webDashboard.update(updatedProduct.webDashboard);
                device.updatedAt = now;
            }
            log.info("{} devices updated with new dashboard.", devices.size());
        }

        existingProduct.update(updatedProduct);

        return ok(existingProduct);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/updateDevices")
    public Response updateProductAndDevices(@ContextUser User user, ProductAndOrgIdDTO productAndOrgIdDTO) {
        Product updatedProduct = productAndOrgIdDTO.product;
        if (updatedProduct == null) {
            log.error("No product for update.");
            return badRequest();
        }

        Product existingProduct = organizationDao.getProduct(productAndOrgIdDTO.orgId, updatedProduct.id);

        if (updatedProduct.notValid()) {
            log.error("Product is not valid.", updatedProduct);
            return badRequest();
        }

        existingProduct.update(updatedProduct);

        List<Device> devices = deviceDao.getAllByProductId(updatedProduct.id);
        long now = System.currentTimeMillis();
        for (Device device : devices) {
            device.updateMetaFields(updatedProduct.metaFields);

            MetaField[] addedMetaFields = ArrayUtil.substruct(updatedProduct.metaFields, device.metaFields)
                    .toArray(new MetaField[0]);
            device.addMetaFields(addedMetaFields);

            MetaField[] deletedMetaFields = ArrayUtil.substruct(device.metaFields, updatedProduct.metaFields)
                    .toArray(new MetaField[0]);
            device.deleteMetaFields(deletedMetaFields);

            device.metadataUpdatedAt = now;
            device.metadataUpdatedBy = user.email;
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
