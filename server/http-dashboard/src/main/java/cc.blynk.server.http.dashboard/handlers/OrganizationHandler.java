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
import cc.blynk.server.core.model.web.Role;
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
            log.error("Cannot find org with id {}", httpSession.user.organizationId);
            return badRequest();
        }

        return ok(organization);
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response create(@Context ChannelHandlerContext ctx, Organization newOrganization) {
        if (newOrganization == null) {
            log.error("Organization is empty.");
            return badRequest();
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        newOrganization = organizationDao.add(newOrganization);

        if (newOrganization == null) {
            log.error("Cannot find org with id {}", httpSession.user.organizationId);
            return badRequest();
        }

        return ok(newOrganization);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response update(Organization newOrganization) {
        if (newOrganization == null) {
            log.error("Organization is empty.");
            return badRequest();
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

        Organization existingOrganization = organizationDao.getOrgById(orgId);

        if (existingOrganization == null) {
            log.error("Organization with passed is {} not found.", orgId);
            return badRequest();
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        if (httpSession.user.role == Role.SUPER_ADMIN) {
            if (!organizationDao.delete(orgId)) {
                log.error("Wasn't able to remove organization with id {}.", orgId);
                return badRequest();
            } else {
                fileManager.deleteOrg(orgId);
            }
        }

        return ok();
    }

}
