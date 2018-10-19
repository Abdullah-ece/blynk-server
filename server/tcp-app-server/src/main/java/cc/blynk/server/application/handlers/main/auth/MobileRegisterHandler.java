package cc.blynk.server.application.handlers.main.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.TokenManager;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.ProvisionType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.appllication.RegisterMessage;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.alreadyRegistered;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * Process register message.
 * Divides input string by nil char on 3 parts:
 * "username" "password" "appName".
 * Checks if user not registered yet. If not - registering.
 *
 * For instance, incoming register message may be : "user@mail.ua my_password"
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
@ChannelHandler.Sharable
public class MobileRegisterHandler extends SimpleChannelInboundHandler<RegisterMessage> {

    private static final Logger log = LogManager.getLogger(MobileRegisterHandler.class);

    private final UserDao userDao;
    private final TokenManager tokenManager;
    private final TimerWorker timerWorker;
    private final LimitChecker registrationLimitChecker;
    private final OrganizationDao organizationDao;

    public MobileRegisterHandler(Holder holder) {
        this.userDao = holder.userDao;
        this.tokenManager = holder.tokenManager;
        this.timerWorker = holder.timerWorker;
        this.organizationDao = holder.organizationDao;
        this.registrationLimitChecker = new LimitChecker(holder.limits.hourlyRegistrationsLimit, 3_600_000L);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterMessage message) {
        if (registrationLimitChecker.isLimitReached()) {
            log.error("Register Handler. Registration limit reached. {}", message);
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        String[] messageParts = StringUtils.split3(message.body);

        //expecting message with 2 parts at least.
        if (messageParts.length < 3) {
            log.error("Register Handler. Wrong income message format. {}", message);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String email = messageParts[0].trim().toLowerCase();
        String passHash = messageParts[1];
        String appName = messageParts[2];
        log.info("Trying register user : {}, app : {}", email, appName);

        if (BlynkEmailValidator.isNotValidEmail(email)) {
            log.error("Register Handler. Wrong email: {}", email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        if (userDao.isUserExists(email)) {
            log.warn("User with email {} already exists.", email);
            ctx.writeAndFlush(alreadyRegistered(message.id), ctx.voidPromise());
            return;
        }

        Organization superOrg = organizationDao.getSuperOrg();
        int orgId = superOrg == null ? OrganizationDao.DEFAULT_ORGANIZATION_ID : superOrg.id;
        int defaultOrgRoleId = superOrg.getDefaultRoleId();
        User newUser = userDao.add(email, passHash, orgId, defaultOrgRoleId);

        log.info("Registered {}.", email);

        userDao.createProjectForExportedApp(timerWorker, tokenManager, newUser, message.id);

        ctx.pipeline().remove(this);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
