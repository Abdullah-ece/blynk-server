package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceKey;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String orgName = org.name;

        List<DeviceDTO> deviceDTOS = new ArrayList<>();
        for (Map.Entry<DeviceKey, Device> entry : deviceDao.devices.entrySet()) {
            DeviceKey key = entry.getKey();
            if (key.orgId == orgId) {
                Product product = org.getProduct(key.productId);
                deviceDTOS.add(new DeviceDTO(entry.getValue(), product, orgName));
            }
        }

        if (ctx.channel().isWritable()) {
            String devicesString = JsonParser.toJson(deviceDTOS);
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesString), ctx.voidPromise());
        }
    }

}
