package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.UserInvite;
import cc.blynk.server.db.dao.InvitationTokensDBDao;
import cc.blynk.server.db.model.InvitationToken;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.utils.FileLoaderUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.validators.BlynkEmailValidator;
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

    private final String INVITE_TEMPLATE;
    private final MailWrapper mailWrapper;
    private final String inviteURL;
    private final InvitationTokensDBDao invitationTokensDBDao;
    private final BlockingIOProcessor blockingIOProcessor;

    public OrganizationHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
        this.fileManager = holder.fileManager;

        this.INVITE_TEMPLATE = FileLoaderUtil.readInviteMailBody();
        //in one week token will expire
        this.mailWrapper = holder.mailWrapper;
        String host = holder.props.getProperty("reset-pass.host");
        this.inviteURL = "https://" + host +  "/invite?token=";
        this.invitationTokensDBDao = holder.dbManager.invitationTokensDBDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
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
    @SuperAdmin
    public Response create(Organization newOrganization) {
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty.");
            return badRequest();
        }

        return ok(organizationDao.add(newOrganization));
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    @Admin
    public Response update(Organization newOrganization) {
        if (isEmpty(newOrganization)) {
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
    @SuperAdmin
    public Response delete(@PathParam("id") int orgId) {
        if (orgId == OrganizationDao.DEFAULT_ORGANIZATION_ID) {
            log.error("Delete operation for initial organization (id = 1) is not allowed.");
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

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/invite")
    @Admin
    public Response sendInviteEmail(@Context ChannelHandlerContext ctx, UserInvite userInvite) {
        if (BlynkEmailValidator.isNotValidEmail(userInvite.email)) {
            log.error("{} email has not valid format.", userInvite.email);
            return badRequest("Invalid email.");
        }

        Organization org = organizationDao.getOrgById(userInvite.orgId);
        if (org == null) {
            log.error("Organization with passed id {} not exists.", userInvite.orgId);
            return badRequest("Wrong organization id.");
        }

        userInvite.email = userInvite.email.toLowerCase();

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        //if user is not super admin, check organization is correct
        if (!httpSession.user.isSuperAdmin()) {
            if (httpSession.user.organizationId != userInvite.orgId) {
                log.error("{} user (orgId = {}) tries to send invite to another organization = {}", httpSession.user.email, httpSession.user.organizationId, userInvite.orgId);
                return unauthorized();
            }
        }


        String token = TokenGeneratorUtil.generateNewToken();
        log.info("Trying to send invitation email to {}.", userInvite.email);

        blockingIOProcessor.execute(() -> {
            Response response;
            try {
                invitationTokensDBDao.insert(new InvitationToken(token, userInvite.email, userInvite.name, userInvite.role.name()));
                String message = INVITE_TEMPLATE.replace("{link}", inviteURL + token);
                mailWrapper.sendHtml(userInvite.email, "Invitation to Blynk dashboard.", message);
                log.info("Invitation sent to {}. From {}", userInvite.email, httpSession.user.email);
                response = ok();
            } catch (Exception e) {
                log.error("Error generating invitation email.", e);
                response = serverError("Error generating invitation email.");
            }
            ctx.writeAndFlush(response);
        });

        return noResponse();
    }

    private boolean isEmpty(Organization newOrganization) {
        return newOrganization == null || newOrganization.name == null || newOrganization.name.isEmpty();
    }

}
