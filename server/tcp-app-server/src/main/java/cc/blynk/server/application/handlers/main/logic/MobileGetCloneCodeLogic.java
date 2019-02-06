package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.CopyUtil;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.db.DBManager;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.GET_CLONE_CODE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileGetCloneCodeLogic {

    private static final Logger log = LogManager.getLogger(MobileGetCloneCodeLogic.class);

    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;

    public MobileGetCloneCodeLogic(Holder holder) {
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                User user, StringMessage message) {
        int dashId = Integer.parseInt(message.body);

        //todo all this is very ugly, however takes 5 min for implementation, also this is rare feature
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        DashBoard copiedDash = CopyUtil.deepCopy(dash);
        copiedDash.eraseWidgetValues();

        String json = JsonParser.toJsonRestrictiveDashboard(copiedDash);
        String qrToken = TokenGeneratorUtil.generateNewToken();

        blockingIOProcessor.executeDB(() -> {
            MessageBase result;
            try {
                boolean insertStatus = dbManager.insertClonedProject(qrToken, json);
                if (insertStatus) {
                    result = makeASCIIStringMessage(GET_CLONE_CODE, message.id, qrToken);
                } else {
                    log.error("Creating clone project failed for {}", user.email);
                    result = json(message.id, "Creating clone project failed.");
                }
            } catch (Exception e) {
                log.error("Error cloning project.", e);
                result = json(message.id, "Error cloning project.");
            }
            ctx.writeAndFlush(result, ctx.voidPromise());
        });
    }
}
