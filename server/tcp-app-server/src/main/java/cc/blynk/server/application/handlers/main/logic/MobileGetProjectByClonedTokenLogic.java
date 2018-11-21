package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.ByteUtils;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_CLONE_CODE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class MobileGetProjectByClonedTokenLogic {

    private static final Logger log = LogManager.getLogger(MobileGetProjectByClonedTokenLogic.class);

    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;
    private final FileManager fileManager;
    private final TimerWorker timerWorker;
    private final int dashMaxLimit;

    public MobileGetProjectByClonedTokenLogic(Holder holder) {
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
        this.fileManager = holder.fileManager;
        this.dashMaxLimit = holder.limits.dashboardsLimit;
        this.timerWorker = holder.timerWorker;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String token;
        boolean newFlow;
        if (message.body.contains(StringUtils.BODY_SEPARATOR_STRING)) {
            newFlow = true;
            token = split2(message.body)[0];
        } else {
            newFlow = false;
            token = message.body;
        }

        blockingIOProcessor.executeDB(() -> {
            MessageBase result;
            try {
                String json = dbManager.selectClonedProject(token);
                //no cloned project in DB, checking local storage on disk
                if (json == null) {
                    json = fileManager.readClonedProjectFromDisk(token);
                }
                if (json == null) {
                    log.debug("Cannot find request clone QR. {}", token);
                    result = json(message.id, "Cannot find requested clone QR.");
                } else {
                    if (newFlow) {
                        result = createDashboard(user, json, message.id);
                    } else {
                        byte[] data = ByteUtils.compress(json);
                        result = makeBinaryMessage(GET_PROJECT_BY_CLONE_CODE, message.id, data);
                    }
                }
            } catch (Exception e) {
                log.error("Error getting cloned project.", e);
                result = json(message.id, e.getMessage());
            }
            ctx.writeAndFlush(result, ctx.voidPromise());
        });
    }

    private MessageBase createDashboard(User user, String dashString, int msgId) throws IOException {
        DashBoard newDash = JsonParser.parseDashboard(dashString, msgId);
        newDash.id = max(user.profile.dashBoards) + 1;
        newDash.isPreview = false;
        newDash.parentId = -1;
        newDash.isShared = false;

        if (user.profile.dashBoards.length >= dashMaxLimit) {
            log.debug("Projects limit reached.");
            throw new JsonException("Projects limit reached.");
        }

        for (DashBoard dashBoard : user.profile.dashBoards) {
            if (dashBoard.id == newDash.id) {
                log.debug("Project with passed already exists.");
                throw new JsonException("Project with passed already exists.");
            }
        }

        log.info("Creating new cloned dashboard.");

        if (newDash.createdAt == 0) {
            newDash.createdAt = System.currentTimeMillis();
        }

        int price = newDash.energySum();
        if (user.notEnoughEnergy(price)) {
            log.debug("Not enough energy.");
            throw new JsonException("Not enough energy.");
        }
        user.subtractEnergy(price);
        user.profile.dashBoards = ArrayUtil.add(user.profile.dashBoards, newDash, DashBoard.class);

        /*
        if (newDash.devices != null) {
            for (Device device : newDash.devices) {
                String token = TokenGeneratorUtil.generateNewToken();
                tokenManager.assignNewToken(user, newDash, device, token);
            }
        }
        */

        user.lastModifiedTs = System.currentTimeMillis();

        newDash.addTimers(timerWorker, user.orgId, user.email);

        byte[] data = ByteUtils.compress(newDash.toString());
        return makeBinaryMessage(GET_PROJECT_BY_CLONE_CODE, msgId, data);
    }

    private int max(DashBoard[] data) {
        int result = 0;
        for (DashBoard dash : data) {
            result = Math.max(result, dash.id);
        }
        return result;
    }
}
