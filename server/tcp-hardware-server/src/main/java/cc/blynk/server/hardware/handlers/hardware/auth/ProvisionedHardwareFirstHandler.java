package cc.blynk.server.hardware.handlers.hardware.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.TokenValue;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.hardware.internal.CreateSessionForwardMessage;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;

/**
 * Hardware that is provisioned should be connected to the product.
 * This action could be done only after hardware sends "internal"
 * command. So this handler is for catching this command and
 * initializing session for the device after it.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class ProvisionedHardwareFirstHandler extends SimpleChannelInboundHandler<StringMessage> {

    private static final Logger log = LogManager.getLogger(ProvisionedHardwareFirstHandler.class);

    private final Holder holder;
    private final User user;
    private final DashBoard dash;
    private final Device device;

    public ProvisionedHardwareFirstHandler(Holder holder, User user, DashBoard dash, Device device) {
        super(StringMessage.class);
        this.holder = holder;
        this.user = user;
        this.dash = dash;
        this.device = device;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, StringMessage message) {
        if (message.command == BLYNK_INTERNAL) {
            String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

            if (messageParts.length == 0 || messageParts[0].length() == 0) {
                ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
                return;
            }

            String cmd = messageParts[0];

            switch (cmd.charAt(0)) {
                case 'v': //ver
                case 'f': //fw
                case 'h': //h-beat
                case 'b': //buff-in
                case 'd': //dev
                case 'c': //cpu
                case 't': //tmpl
                    HardwareInfo hardwareInfo = new HardwareInfo(messageParts);
                    String templateId = hardwareInfo.templateId;
                    int orgId = user.orgId;

                    Product product;
                    if (templateId == null) {
                        Organization org = holder.organizationDao.getOrgByIdOrThrow(orgId);
                        product = org.getFirstProduct();
                        log.warn("No templateId from hardware. Getting first product (id={}) for provisioned device.",
                                product.id);
                    } else {
                        product = holder.organizationDao.getProductByTemplateId(templateId);
                        if (product == null) {
                            Organization org = holder.organizationDao.getOrgByIdOrThrow(orgId);
                            product = org.getFirstProduct();
                            log.warn("No templateId {} in products for deviceId {}. "
                                            + "Getting first product (id={}) for provisioned device.",
                                    device.id, templateId, product.id);
                        }
                    }
                    device.productId = product.id;
                    device.hardwareInfo = hardwareInfo;
                    device.metaFields = product.copyMetaFields();
                    device.webDashboard = product.webDashboard.copy();
                    holder.deviceDao.createWithPredefinedId(orgId, device);

                    holder.tokenManager.updateRegularCache(
                            device.token, new TokenValue(user, dash, device));
                    dash.addDevice(device, templateId);
                    ctx.pipeline().fireUserEventTriggered(
                            new CreateSessionForwardMessage(user, dash, device, message.id));
                    break;
            }
        } else {
            log.warn("Expecting only internal command here for user {}", user.email);
        }
    }

}
