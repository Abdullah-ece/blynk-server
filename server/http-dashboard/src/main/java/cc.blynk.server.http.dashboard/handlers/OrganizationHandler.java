package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.web.Organization;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.core.http.Response.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/organization")
@ChannelHandler.Sharable
public class OrganizationHandler extends BaseHttpHandler {

    private final OrganizationDao organizationDao;
    private final FileManager fileManager;

    public OrganizationHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
        this.fileManager = holder.fileManager;
    }

    @GET
    @Path("")
    public Response get(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        Organization organization = organizationDao.getOrgById(httpSession.user.organizationId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}.", httpSession.user.organizationId, httpSession.user.email);
            return badRequest();
        }

        return ok(organization);
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response create(@Context ChannelHandlerContext ctx, Organization newOrganization) {
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty.");
            return badRequest();
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        if (!httpSession.user.isSuperAdmin()) {
            log.error("Only super admin can create organization. User {} is not super admin.", httpSession.user.email);
            return unauthorized();
        }

        return ok(organizationDao.add(newOrganization));
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response update(@Context ChannelHandlerContext ctx, Organization newOrganization) {
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty.");
            return badRequest();
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        if (!httpSession.user.isAdmin()) {
            log.error("Only admins can edit organization. User {} is not admin.", httpSession.user.email);
            return unauthorized();
        }

        Organization existingOrganization = organizationDao.getOrgById(newOrganization.id);

        if (existingOrganization == null) {
            log.error("Organization with passed is {} not found.", newOrganization.id);
            return badRequest();
        }

        existingOrganization.update(newOrganization);

        return ok(existingOrganization);
    }

    @DELETE
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(@Context ChannelHandlerContext ctx, @PathParam("id") int orgId) {
        if (orgId == OrganizationDao.DEFAULT_ORGANIZATION_ID) {
            log.error("Delete operation for initial organization (id = 1) is not allowed.");
            return unauthorized();
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        if (!httpSession.user.isSuperAdmin()) {
            log.error("Only super admins can delete organization. User {} is not super admin.", httpSession.user.email);
            return unauthorized();
        }

        Organization existingOrganization = organizationDao.getOrgById(orgId);

        if (existingOrganization == null) {
            log.error("Organization with passed is {} not found.", orgId);
            return badRequest();
        }

        if (!organizationDao.delete(orgId)) {
            log.error("Wasn't able to remove organization with id {}.", orgId);
            return badRequest();
        } else {
            fileManager.deleteOrg(orgId);
        }

        return ok();
    }

    private boolean isEmpty(Organization newOrganization) {
        return newOrganization == null || newOrganization.name == null || newOrganization.name.isEmpty();
    }

}
