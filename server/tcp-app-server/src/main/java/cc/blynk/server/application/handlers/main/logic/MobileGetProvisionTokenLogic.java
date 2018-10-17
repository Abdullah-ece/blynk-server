package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.GET_PROVISION_TOKEN;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.04.18.
 */
public final class MobileGetProvisionTokenLogic {

    private static final Logger log = LogManager.getLogger(MobileGetProvisionTokenLogic.class);

    private MobileGetProvisionTokenLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split2(message.body);

        int dashId = Integer.parseInt(split[0]);
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        String deviceString = split[1];
        if (deviceString == null || deviceString.isEmpty()) {
            throw new IllegalCommandException("Income device message is empty.");
        }

        Device temporaryDevice = JsonParser.parseDevice(deviceString, message.id);

        if (temporaryDevice.isNotValid()) {
            throw new IllegalCommandException("Income device message is not valid.");
        }

        temporaryDevice.id = holder.deviceDao.getId();

        String tempToken = TokenGeneratorUtil.generateNewToken();
        log.debug("Getting provision token {} for deviceId {}.", tempToken, temporaryDevice.id);
        holder.tokenManager.assignToken(user, dash, temporaryDevice, tempToken, true);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeASCIIStringMessage(GET_PROVISION_TOKEN,
                    message.id, temporaryDevice.toString()), ctx.voidPromise());
        }

    }

}
