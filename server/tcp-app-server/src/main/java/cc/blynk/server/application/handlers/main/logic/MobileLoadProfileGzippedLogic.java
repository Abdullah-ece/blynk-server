package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.ExportAppProfileDTO;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.model.FlashedToken;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.server.core.model.serialization.JsonParser.gzipDash;
import static cc.blynk.server.core.model.serialization.JsonParser.gzipDashRestrictive;
import static cc.blynk.server.core.model.serialization.JsonParser.gzipExportProfileDTO;
import static cc.blynk.server.core.model.serialization.JsonParser.gzipProfile;
import static cc.blynk.server.core.protocol.enums.Command.LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.internal.CommonByteBufUtil.makeBinaryMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileLoadProfileGzippedLogic {

    private static final Logger log = LogManager.getLogger(MobileLoadProfileGzippedLogic.class);

    private final OrganizationDao organizationDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final UserDao userDao;
    private final DBManager dbManager;

    public MobileLoadProfileGzippedLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.userDao = holder.userDao;
        this.dbManager = holder.dbManager;
    }

    private static Profile fillWithValues(List<Device> userOwnedDevices, Profile profile) {
        for (DashBoard dashBoard : profile.dashBoards) {
            dashBoard.fillValues(userOwnedDevices);
        }
        return profile;
    }

    private static DashBoard fillWithValues(List<Device> userOwnedDevices, DashBoard dashBoard) {
        dashBoard.fillValues(userOwnedDevices);
        return dashBoard;
    }

    private static List<DashBoard> fillWithValues(List<Device> userOwnedDevices, List<DashBoard> dashBoards) {
        for (DashBoard dashBoard : dashBoards) {
            dashBoard.fillValues(userOwnedDevices);
        }
        return dashBoards;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                BaseUserStateHolder state, StringMessage message) {
        int msgId = message.id;

        //load all
        Organization org = organizationDao.getOrgByIdOrThrow(state.user.orgId);
        List<Device> devices;
        if (state.role.canViewOrgDevices()) {
            devices = org.getAllDevices();
        } else if (state.role.canViewOrgDevices()) {
            devices = org.getDevicesByOwner(state.user.email);
        } else {
            throw new NoPermissionException("User has no permission to view devices.");
        }

        if (message.body.length() == 0) {
            //special case for the super admin in exported app, as admin can have parent and
            //child projects within same account, so we have to filter dashes for exported app
            if (state.user.isSuperAdmin() && state.version.isExportApp()) {
                log.debug("Filtering dashboards for super admin {} and exported app {}.",
                        state.user.email, state.version);
                List<DashBoard> dashBoards = fillWithValues(devices, filter(state.user.profile.dashBoards));
                write(ctx, gzipExportProfileDTO(new ExportAppProfileDTO(dashBoards)), msgId);
            } else {
                write(ctx, gzipProfile(fillWithValues(devices, state.user.profile)), msgId);
            }
            return;
        }

        String[] parts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);
        if (parts.length == 1) {
            //load specific by id
            int dashId = Integer.parseInt(message.body);
            DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);
            write(ctx, gzipDash(fillWithValues(devices, dash)), msgId);
        } else {
            String token = parts[0];
            int dashId = Integer.parseInt(parts[1]);
            String publishingEmail = parts[2];
            //this is for simplification of testing.

            blockingIOProcessor.executeDB(() -> {
                try {
                    FlashedToken flashedToken = dbManager.selectFlashedToken(token);
                    if (flashedToken != null) {
                        User publishingUser = userDao.getByName(publishingEmail);
                        DashBoard dash = publishingUser.profile.getDashByIdOrThrow(dashId);
                        //todo ugly. but ok for now
                        String copyString = JsonParser.toJsonRestrictiveDashboard(dash);
                        DashBoard copyDash = JsonParser.parseDashboard(copyString, msgId);
                        //still need this, as dash may be with values from prev operations
                        copyDash.eraseWidgetValues();
                        write(ctx, gzipDashRestrictive(copyDash), msgId);
                    }
                } catch (Exception e) {
                    ctx.writeAndFlush(json(msgId, "Error getting publishing profile."), ctx.voidPromise());
                    log.error("Error getting publishing profile.", e.getMessage());
                }
            });
        }
    }

    public static void write(ChannelHandlerContext ctx, byte[] data, int msgId) {
        //todo, return?
        //if (ctx.channel().isWritable()) {
        MessageBase outputMsg = makeResponse(data, msgId);
        ctx.writeAndFlush(outputMsg, ctx.voidPromise());
        //}
    }

    private static MessageBase makeResponse(byte[] data, int msgId) {
        if (data == null) {
            return json(msgId, "No data.");
        }
        return makeBinaryMessage(LOAD_PROFILE_GZIPPED, msgId, data);
    }

    private static List<DashBoard> filter(DashBoard[] dashBoards) {
        var copy = new ArrayList<DashBoard>();
        for (DashBoard dash : dashBoards) {
            if (dash.isChild()) {
                copy.add(dash);
            }
        }
        return copy;
    }

}
