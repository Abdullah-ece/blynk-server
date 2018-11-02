package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.MobileStateHolder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.ExportAppProfileDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.db.model.FlashedToken;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static cc.blynk.server.core.model.serialization.JsonParser.gzipDash;
import static cc.blynk.server.core.model.serialization.JsonParser.gzipDashRestrictive;
import static cc.blynk.server.core.model.serialization.JsonParser.gzipExportProfileDTO;
import static cc.blynk.server.core.model.serialization.JsonParser.gzipProfile;
import static cc.blynk.server.core.protocol.enums.Command.LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.noData;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileLoadProfileGzippedLogic {

    private static final Logger log = LogManager.getLogger(MobileLoadProfileGzippedLogic.class);

    private MobileLoadProfileGzippedLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        //load all
        int msgId = message.id;

        if (message.body.length() == 0) {
            DashBoard[] dashBoards = state.user.profile.dashBoards;

            //special case for the super admin in exported app, as admin can have parent and
            //child projects within same account, so we have to filter dashes for exported app
            if (state.user.isSuperAdmin() && state.version.isExportApp()) {
                log.debug("Filtering dashboards for super admin {} and exported app {}.",
                        state.user.email, state.version);
                dashBoards = filter(dashBoards);
                write(ctx, gzipExportProfileDTO(new ExportAppProfileDTO(dashBoards)), msgId);
            } else {
                write(ctx, gzipProfile(state.user.profile), msgId);
            }
            return;
        }

        String[] parts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);
        if (parts.length == 1) {
            //load specific by id
            int dashId = Integer.parseInt(message.body);
            DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);
            write(ctx, gzipDash(dash), msgId);
        } else {
            String token = parts[0];
            int dashId = Integer.parseInt(parts[1]);
            String publishingEmail = parts[2];
            //this is for simplification of testing.

            holder.blockingIOProcessor.executeDB(() -> {
                try {
                    FlashedToken flashedToken = holder.dbManager.selectFlashedToken(token);
                    if (flashedToken != null) {
                        User publishingUser = holder.userDao.getByName(publishingEmail);
                        DashBoard dash = publishingUser.profile.getDashByIdOrThrow(dashId);
                        //todo ugly. but ok for now
                        String copyString = JsonParser.toJsonRestrictiveDashboard(dash);
                        DashBoard copyDash = JsonParser.parseDashboard(copyString, msgId);
                        copyDash.eraseWidgetValues();
                        write(ctx, gzipDashRestrictive(copyDash), msgId);
                    }
                } catch (Exception e) {
                    ctx.writeAndFlush(illegalCommand(msgId), ctx.voidPromise());
                    log.error("Error getting publishing profile.", e.getMessage());
                }
            });
        }
    }

    public static void write(ChannelHandlerContext ctx, byte[] data, int msgId) {
        if (ctx.channel().isWritable()) {
            var outputMsg = makeResponse(data, msgId);
            ctx.writeAndFlush(outputMsg, ctx.voidPromise());
        }
    }

    private static MessageBase makeResponse(byte[] data, int msgId) {
        if (data == null) {
            return noData(msgId);
        }
        return makeBinaryMessage(LOAD_PROFILE_GZIPPED, msgId, data);
    }

    private static DashBoard[] filter(DashBoard[] dashBoards) {
        var copy = new ArrayList<DashBoard>();
        for (DashBoard dash : dashBoards) {
            if (dash.isChild()) {
                copy.add(dash);
            }
        }
        return copy.toArray(new DashBoard[0]);
    }

}
