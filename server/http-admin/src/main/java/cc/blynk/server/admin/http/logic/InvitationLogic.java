package cc.blynk.server.admin.http.logic;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Role;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.db.dao.InvitationTokensDBDao;
import cc.blynk.server.db.model.InvitationToken;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.utils.FileLoaderUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.core.http.Response.*;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 03.12.15.
 */
@Path("")
@ChannelHandler.Sharable
public class InvitationLogic extends BaseHttpHandler {

    private static final Logger log = LogManager.getLogger(InvitationLogic.class);

    private final String INVITE_TEMPLATE;
    private final MailWrapper mailWrapper;
    private final String inviteURL;
    private final InvitationTokensDBDao invitationTokensDBDao;
    private final BlockingIOProcessor blockingIOProcessor;

    public InvitationLogic(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.INVITE_TEMPLATE = FileLoaderUtil.readInviteMailBody();
        //in one week token will expire
        this.mailWrapper = holder.mailWrapper;
        String host = holder.props.getProperty("reset-pass.http.host");
        this.inviteURL = "http://" + host + rootPath + "/invite?token=";
        this.invitationTokensDBDao = holder.dbManager.invitationTokensDBDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/invite")
    public Response sendInviteEmail(@Context ChannelHandlerContext ctx,
                                @FormParam("email") String email,
                                @FormParam("name") String name,
                                @FormParam("role") String role) {

        if (BlynkEmailValidator.isNotValidEmail(email)) {
            log.error("{} email has not valid format.", email);
            return badRequest(email + " email has not valid format.");
        }

        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();

        if (httpSession == null) {
            log.error("Not logged");
            return unauthorized("Not logged.");
        }

        User user = httpSession.user;

        if (user == null || user.role == Role.STAFF) {
            log.error("Not rights to perform invitation.");
            return forbidden();
        }

        String token = TokenGeneratorUtil.generateNewToken();
        log.info("Trying to send invitation email to {}.", email);

        blockingIOProcessor.execute(() -> {
            Response response;
            try {
                invitationTokensDBDao.insert(new InvitationToken(token, email, name, role));
                String message = INVITE_TEMPLATE.replace("{link}", inviteURL + token);
                mailWrapper.sendHtml(email, "Invitation to Blynk dashboard.", message);
                log.info("Invitation sent to {}. From {}", email, user.email);
                response = ok();
            } catch (Exception e) {
                log.error("Error generating invitation email.", e);
                response = serverError("Error generating invitation email.");
            }
            ctx.writeAndFlush(response);
        });

        return noResponse();
    }

}
