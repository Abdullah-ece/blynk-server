package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
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
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.http.MediaType;
import io.netty.channel.ChannelHandler;

import java.util.List;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.forbidden;
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
            return forbidden("You are not allowed to getOrgSession this product.");
        }

        return ok(product);
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response create(ProductAndOrgIdDTO productAndOrgIdDTO) {
        if (productAndOrgIdDTO.product == null) {
            log.error("Product is empty.");
            return badRequest("Product is empty.");
        }
        Product product = productAndOrgIdDTO.product.toProduct();

        product.validate();

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
        if (productAndOrgIdDTO.product == null) {
            log.error("Product is empty.");
            return badRequest("Product is empty.");
        }
        Product updatedProduct = productAndOrgIdDTO.product.toProduct();

        updatedProduct.validate();

        Organization organization = organizationDao.getOrgByIdOrThrow(productAndOrgIdDTO.orgId);

        if (!organization.isValidProductName(updatedProduct)) {
            log.error("Organization {} already has product with name {}.", organization.name, updatedProduct.name);
            return badRequest("Product with this name already exists.");
        }

        Product existingProduct = organizationDao.getProductOrThrow(productAndOrgIdDTO.orgId, updatedProduct.id);

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

    @GET
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/canDeleteProduct/{id}")
    public Response canDeleteProduct(@PathParam("id") int productId) {
        if (deviceDao.productHasDevices(productId)) {
            return forbidden();
        }
        return ok();
    }

    @DELETE
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(@ContextUser User user, @PathParam("id") int productId) {
        //if (organizationDao.deleteProduct(user, productId)) {
            return ok();
        //} else {
        //    return notFound();
        //}
    }
}
