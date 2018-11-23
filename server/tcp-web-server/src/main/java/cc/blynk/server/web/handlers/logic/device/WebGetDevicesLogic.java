package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetDevicesLogic {

    private static final Logger log = LogManager.getLogger(WebGetDevicesLogic.class);

    private WebGetDevicesLogic() {
    }

    public static void messageReceived(Holder holder,
                                ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);

        OrganizationDao organizationDao = holder.organizationDao;
        DeviceDao deviceDao = holder.deviceDao;

        //todo refactor when permissions ready
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} not allowed to access orgId {}", user.email, orgId);
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        Organization org = organizationDao.getOrgById(orgId);

        String devicesJson;
        List<DeviceDTO> deviceDTOs;

        //todo if no access to org devices - use own devices filter
        if (state.role.canViewOrgDevices()) {
            deviceDTOs = org.getAllDeviceDTOs();
        } else {
            deviceDTOs = org.getDevicesByOwnerDTOs(user.email);
        }
        devicesJson = JsonParser.toJson(deviceDTOs);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesJson), ctx.voidPromise());
        }
    }

}
