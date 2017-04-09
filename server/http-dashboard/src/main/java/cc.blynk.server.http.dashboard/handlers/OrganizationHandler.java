package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.*;
import cc.blynk.server.core.model.AppName;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.UserInvite;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.utils.FileLoaderUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.net.URLEncoder;

import static cc.blynk.core.http.Response.*;

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
    private final FileManager fileManager;
    private final DBManager dbManager;

    private final String INVITE_TEMPLATE;
    private final MailWrapper mailWrapper;
    private final String inviteURL;
    private final BlockingIOProcessor blockingIOProcessor;
    private final TokensPool tokensPool;

    public OrganizationHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
        this.organizationDao = holder.organizationDao;
        this.fileManager = holder.fileManager;
        this.dbManager = holder.dbManager;

        this.INVITE_TEMPLATE = FileLoaderUtil.readInviteMailBody();
        //in one week token will expire
        this.mailWrapper = holder.mailWrapper;
        String host = holder.props.getProperty("reset-pass.host");
        this.inviteURL = "https://" + host + rootPath + "#/invite?token=";
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.tokensPool = holder.tokensPool;
    }

    @GET
    @Path("/{id}")
    public Response get(@Context ChannelHandlerContext ctx, @PathParam("id") int orgId) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        Organization organization = organizationDao.getOrgById(orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}.", httpSession.user.orgId, httpSession.user.email);
            return badRequest("Cannot find organization with passed id.");
        }

        if (!httpSession.user.isSuperAdmin()) {
            if (orgId != httpSession.user.orgId) {
                log.error("User {} tries to access organization he has no access.");
                return forbidden("You are not allowed to access this organization.");
            }
        }

        return ok(organization);
    }


    @GET
    @Path("/{id}/users")
    public Response getUsers(@Context ChannelHandlerContext ctx, @PathParam("id") int orgId) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        Organization organization = organizationDao.getOrgById(orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}.", httpSession.user.orgId, httpSession.user.email);
            return badRequest("Cannot find organization with passed id.");
        }

        if (!httpSession.user.isSuperAdmin()) {
            if (orgId != httpSession.user.orgId) {
                log.error("User {} tries to access organization he has no access.");
                return forbidden("You are not allowed to access this organization.");
            }
        }

        return ok(userDao.getUsersByOrgId(orgId));
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{id}/users/delete")
    @Admin
    public Response deleteUsers(@Context ChannelHandlerContext ctx, @PathParam("id") int orgId, String[] emailsToDelete) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        Organization organization = organizationDao.getOrgById(orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}.", httpSession.user.orgId, httpSession.user.email);
            return badRequest("Cannot find organization with passed id.");
        }

        if (!httpSession.user.isSuperAdmin()) {
            if (orgId != httpSession.user.orgId) {
                log.error("User {} tries to access organization he has no access.");
                return forbidden("You are not allowed to access this organization.");
            }
        }

        String appName = httpSession.user.appName;

        for (String email : emailsToDelete) {
            UserKey userKey = new UserKey(email, appName);
            User user = userDao.getByName(userKey);
            if (user != null && user.orgId == orgId) {
                log.info("Deleting {} user.", email);
                userDao.delete(userKey);
                fileManager.delete(userKey);
                dbManager.deleteUser(userKey);
                sessionDao.deleteUser(userKey);
            }
        }

        return ok();
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
    @Path("/{id}")
    @Admin
    public Response update(@Context ChannelHandlerContext ctx, @PathParam("id") int orgId, Organization newOrganization) {
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty.");
            return badRequest();
        }

        Organization existingOrganization = organizationDao.getOrgById(orgId);

        if (existingOrganization == null) {
            log.error("Organization with passed is {} not found.", newOrganization.id);
            return badRequest();
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        if (!httpSession.user.isSuperAdmin()) {
            if (orgId != httpSession.user.orgId) {
                log.error("User {} tries to update organization he has no access.", httpSession.user.email);
                return forbidden("You are not allowed to update this organization.");
            }
        }

        existingOrganization.update(newOrganization);

        return ok(existingOrganization);
    }

    @DELETE
    @Path("/{id}")
    @SuperAdmin
    public Response delete(@PathParam("id") int orgId) {
        if (orgId == OrganizationDao.DEFAULT_ORGANIZATION_ID) {
            log.error("Delete operation for initial organization (id = 1) is not allowed.");
            return forbidden();
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
    @Path("/{id}/invite")
    @Admin
    public Response sendInviteEmail(@Context ChannelHandlerContext ctx, @PathParam("id") int orgId, UserInvite userInvite) {
        if (orgId == 0 || userInvite.isNotValid() || BlynkEmailValidator.isNotValidEmail(userInvite.email)) {
            log.error("Invalid invitation. Probably {} email has not valid format.", userInvite.email);
            return badRequest("Invalid invitation.");
        }

        Organization org = organizationDao.getOrgById(orgId);
        if (org == null) {
            log.error("Organization with passed id {} not exists.", orgId);
            return badRequest("Wrong organization id.");
        }

        userInvite.email = userInvite.email.toLowerCase();

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        //if user is not super admin, check organization is correct
        if (!httpSession.user.isSuperAdmin()) {
            if (httpSession.user.orgId != orgId) {
                log.error("{} user (orgId = {}) tries to send invite to another organization = {}", httpSession.user.email, httpSession.user.orgId, orgId);
                return forbidden();
            }
        }

        User invitedUser = userDao.invite(userInvite, orgId, AppName.BLYNK);

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("Trying to send invitation email to {}.", userInvite.email);

        blockingIOProcessor.execute(() -> {
            Response response;
            try {
                tokensPool.addToken(token, invitedUser);
                String message = INVITE_TEMPLATE
                        .replace("{name}", userInvite.name)
                        .replace("{role}", userInvite.role.name().toLowerCase())
                        .replace("{product_name}", org.name)
                        .replace("{link}", inviteURL + token + "&email=" + URLEncoder.encode(userInvite.email, "UTF-8"));
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
