package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.CopyUtil;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.model.FlashedToken;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_TOKEN;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileGetProjectByTokenLogic {

    private static final Logger log = LogManager.getLogger(MobileGetProjectByTokenLogic.class);

    private final DBManager dbManager;
    private final UserDao userDao;
    private final BlockingIOProcessor blockingIOProcessor;

    public MobileGetProjectByTokenLogic(Holder holder) {
        this.dbManager = holder.dbManager;
        this.userDao = holder.userDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                User user, StringMessage message) {
        String token = message.body;

        blockingIOProcessor.executeDB(() -> {
            FlashedToken dbFlashedToken = dbManager.selectFlashedToken(token);

            if (dbFlashedToken == null) {
                log.error("{} token not exists for orgId {} for {} (GetProject).", token, user.orgId, user.email);
                throw new JsonException("Clone token not exists.");
            }

            User publishUser = userDao.getByName(dbFlashedToken.email);

            DashBoard dash = publishUser.profile.getDashById(dbFlashedToken.dashId);
            DashBoard copy = CopyUtil.deepCopy(dash);
            copy.eraseWidgetValues();

            if (dash == null) {
                log.error("Dash with {} id not exists in dashboards.", dbFlashedToken.dashId);
                throw new JsonException("Project with passed id not exists in user profile.");
            }

            write(ctx, JsonParser.gzipDashRestrictive(copy), message.id);
        });
    }

    public static void write(ChannelHandlerContext ctx, byte[] data, int msgId) {
        if (ctx.channel().isWritable()) {
            var outputMsg = makeBinaryMessage(GET_PROJECT_BY_TOKEN, msgId, data);
            ctx.writeAndFlush(outputMsg, ctx.voidPromise());
        }
    }
}
