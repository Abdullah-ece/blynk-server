package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_PROVISION_TOKEN;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.04.18.
 */
public final class MobileGetProvisionTokenLogic {

    private static final Logger log = LogManager.getLogger(MobileGetProvisionTokenLogic.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;

    public MobileGetProvisionTokenLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String deviceString = message.body;

        if (deviceString.isEmpty()) {
            throw new IllegalCommandException("Income device message is empty.");
        }

        Device temporaryDevice = JsonParser.parseDevice(deviceString, message.id);

        temporaryDevice.id = deviceDao.getId();

        log.debug("Getting provision token for deviceId {}.", temporaryDevice.id);
        Organization org = organizationDao.getOrgByIdOrThrow(user.orgId);
        deviceDao.assignTempToken(org, user, temporaryDevice);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeASCIIStringMessage(MOBILE_GET_PROVISION_TOKEN,
                    message.id, temporaryDevice.toString()), ctx.voidPromise());
        }
    }

}
