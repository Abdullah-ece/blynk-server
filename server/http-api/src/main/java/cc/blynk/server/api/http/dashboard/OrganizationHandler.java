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
import cc.blynk.core.http.annotation.SuperAdmin;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.OrganizationDTO;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.http.MediaType;
import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.forbidden;
import static cc.blynk.core.http.Response.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/organization")
@ChannelHandler.Sharable
public class OrganizationHandler extends BaseHttpHandler {

    private final UserDao userDao;
    private final OrganizationDao organizationDao;

    public OrganizationHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
        this.organizationDao = holder.organizationDao;
    }

    @GET
    @Path("/{orgId}")
    public Response get(@ContextUser User user,
                        @PathParam("orgId") int orgId) {
        Organization organization = organizationDao.getOrgByIdOrThrow(orgId);

        if (!user.isSuperAdmin()) {
            if (orgId != user.orgId) {
                log.error("User {} tries to access organization he has no access.", user.email);
                return forbidden("You are not allowed to access this organization.");
            }
        }

        String parentOrgName = null;
        if (organization.hasParentOrg()) {
            Organization parentOrg = organizationDao.getOrgById(organization.parentId);
            if (parentOrg != null && parentOrg.name != null) {
                parentOrgName = parentOrg.name;
            }
        }

        return ok(new OrganizationDTO(organization, parentOrgName));
    }

    @GET
    @Path("")
    public Response getListOfOrganizations(@ContextUser User user) {
        List<Organization> orgs = organizationDao.getAll(user)
                .stream()
                .filter(org -> org.id != user.orgId && org.parentId == user.orgId)
                .collect(Collectors.toList());

        organizationDao.calcDeviceCount(orgs);

        return ok(orgs);
    }

    @GET
    @Path("/{orgId}/users")
    public Response getUsers(@ContextUser User user, @PathParam("orgId") int orgId) {
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access organization he has no access.");
            return forbidden("You are not allowed to access this organization.");
        }

        return ok(userDao.getUsersByOrgId(orgId, user.email));
    }


    @GET
    @Path("/{orgId}/locations")
    public Response getExistingLocations(@ContextUser User user, @PathParam("orgId") int orgId)  {
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access organization he has no access.", user.email);
            return forbidden("You are not access this organization.");
        }

        List<String> existingLocations = new ArrayList<>();
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        for (Product product : org.products) {
            for (MetaField metaField : product.metaFields) {
                if ("Location Name".equalsIgnoreCase(metaField.name)) {
                    existingLocations.add(((TextMetaField) metaField).value);
                }
            }
        }

        return ok(existingLocations);
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    @Admin
    public Response create(@ContextUser User user, Organization newOrganization) {
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty.");
            return badRequest("Organization is empty.");
        }

        newOrganization.parentId = user.orgId;

        Organization parentOrg = organizationDao.getOrgByIdOrThrow(user.orgId);
        if (!parentOrg.canCreateOrgs) {
            log.error("This organization cannot have sub organizations.");
            return forbidden("This organization cannot have sub organizations.");
        }

        newOrganization = organizationDao.create(newOrganization);
        createProductsFromParentOrg(newOrganization.id, newOrganization.name, newOrganization.selectedProducts);

        return ok(newOrganization);
    }

    private void createProductsFromParentOrg(int orgId, String orgName, int[] selectedProducts) {
        for (int productId : selectedProducts) {
            if (organizationDao.hasNoProductWithParent(orgId, productId)) {
                log.debug("Cloning product for org {} and parentProductId {}.", orgName, productId);
                Product parentProduct = organizationDao.getProductByIdOrThrow(productId);
                Product newProduct = new Product(parentProduct);
                newProduct.parentId = parentProduct.id;
                organizationDao.createProduct(orgId, newProduct);
            } else {
                log.debug("Already has product for org {} with product parent id {}.", orgName, productId);
            }
        }
    }

    private void deleteRemovedProducts(int orgId, String orgName, int[] deletedProducts) {
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        for (int parentProductId : deletedProducts) {
            log.debug("Deleting product for org {} and parentProductId {}.", orgName, parentProductId);
            for (Product product : org.products) {
                if (product.parentId == parentProductId) {
                    try {
                        organizationDao.deleteProduct(org, product.id);
                        log.debug("Product was removed.");
                    } catch (ForbiddenWebException e) {
                        log.debug("Cannot delete product. {}", e.getMessage());
                    }

                }
            }
        }
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{orgId}")
    @Admin
    public Response update(@ContextUser User user, @PathParam("orgId") int orgId, Organization newOrganization) {
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty.");
            return badRequest();
        }

        Organization existingOrganization = organizationDao.getOrgByIdOrThrow(newOrganization.id);

        if (!organizationDao.hasAccess(user, newOrganization.id)) {
            log.error("User {} tries to update organization he has no access.", user.email);
            return forbidden("You are not allowed to update this organization.");
        }

        if (organizationDao.checkNameExists(newOrganization.id, newOrganization.name)) {
            throw new ForbiddenWebException("Organization with this name already exists.");
        }

        existingOrganization.update(newOrganization);

        int[] addedProducts = ArrayUtil.substruct(
                newOrganization.selectedProducts, existingOrganization.selectedProducts);
        createProductsFromParentOrg(newOrganization.id, newOrganization.name, addedProducts);

        int[] removedProducts = ArrayUtil.substruct(
                existingOrganization.selectedProducts, newOrganization.selectedProducts);
        deleteRemovedProducts(newOrganization.id, newOrganization.name, removedProducts);

        existingOrganization.selectedProducts = newOrganization.selectedProducts;

        return ok(existingOrganization);
    }

    @DELETE
    @Path("/{orgId}")
    @SuperAdmin
    public Response delete(@PathParam("orgId") int orgId) {
        if (orgId == OrganizationDao.DEFAULT_ORGANIZATION_ID) {
            log.error("Delete operation for initial organization (orgId = 1) is not allowed.");
            return forbidden();
        }

        Organization existingOrganization = organizationDao.getOrgByIdOrThrow(orgId);

        if (!organizationDao.delete(orgId)) {
            log.error("Wasn't able to remove organization with orgId {}.", orgId);
            return badRequest();
        }

        return ok();
    }

    private boolean isEmpty(Organization newOrganization) {
        return newOrganization == null || newOrganization.name == null || newOrganization.name.isEmpty();
    }

}
