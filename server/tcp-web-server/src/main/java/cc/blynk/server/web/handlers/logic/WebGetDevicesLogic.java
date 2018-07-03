package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetDevicesLogic {

    private static final Logger log = LogManager.getLogger(WebGetDevicesLogic.class);

    private final OrganizationDao organizationDao;

    public WebGetDevicesLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = Integer.parseInt(split[0]);

        //todo refactor when permissions ready
        User user = state.user;
        organizationDao.hasAccess(user, orgId);

        Collection<Device> devices = organizationDao.getAllDevicesByOrgId(orgId);
        if (ctx.channel().isWritable()) {
            String devicesString = JsonParser.toJson(devices);
            ctx.writeAndFlush(
                    makeASCIIStringMessage(WEB_GET_DEVICES, message.id, devicesString), ctx.voidPromise());
        }
    }

}
