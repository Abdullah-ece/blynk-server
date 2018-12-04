package cc.blynk.server.hardware.handlers.hardware.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.DeviceOwnerMetaField;
import cc.blynk.server.core.model.web.product.metafields.TemplateIdMetaField;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.hardware.internal.ProvisionedDeviceAddedMessage;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
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
    private final int orgId;
    private final User user;
    private final Device device;
    private Organization org;
    private Product product;

    ProvisionedHardwareFirstHandler(Holder holder, int orgId, User user, Device device) {
        super(StringMessage.class);
        this.holder = holder;
        this.orgId = orgId;
        this.user = user;
        this.device = device;
    }

    private static void setDeviceOwnerInMeta(MetaField[] metaFields, String email) {
        for (int i = 0; i < metaFields.length; i++) {
            MetaField metaField = metaFields[i];
            if (metaField instanceof DeviceOwnerMetaField) {
                DeviceOwnerMetaField deviceOwnerMetaField = (DeviceOwnerMetaField) metaField;
                metaFields[i] = deviceOwnerMetaField.copy(email);
                return;
            }
        }
    }

    private void getProductAndOrgByTemplateId(int orgId, String templateId) {
        Organization tempOrg = holder.organizationDao.getOrgByIdOrThrow(orgId);
        Product productWithTemplate = tempOrg.getProductByTemplateId(templateId);
        if (productWithTemplate != null) {
            this.org = tempOrg;
            this.product = productWithTemplate;
        }
    }

    private static void setTemplateIdInMeta(MetaField[] metaFields, String templateId) {
        for (int i = 0; i < metaFields.length; i++) {
            MetaField metaField = metaFields[i];
            if (metaField instanceof TemplateIdMetaField) {
                TemplateIdMetaField templateIdMetaField = (TemplateIdMetaField) metaField;
                metaFields[i] = templateIdMetaField.copy(templateId);
                return;
            }
        }
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

                    if (templateId == null) {
                        org = holder.organizationDao.getOrgByIdOrThrow(orgId);

                        //special temporary hotfix https://github.com/blynkkk/dash/issues/1765
                        if ("0.7.0".equals(hardwareInfo.blynkVersion)) {
                            String productName = "Airius Fan";
                            product = org.getProductByName(productName);
                            if (product == null) {
                                log.error("Didn't find product by name {} for orgId={}.", productName, orgId);
                            }
                        }
                        if (product == null) {
                            product = org.getFirstProduct();
                        }

                        log.warn("No templateId from hardware. Getting first product (id={}) "
                                + "for provisioned device {}.", product.id, device.id);
                    } else {
                        getProductAndOrgByTemplateId(orgId, templateId);
                        if (product == null) {
                            org = holder.organizationDao.getOrgByIdOrThrow(orgId);
                            product = org.getFirstProduct();
                            log.warn("No templateId {} in products for deviceId {}. "
                                            + "Getting first product (id={}) for provisioned device.",
                                    device.id, templateId, product.id);
                        }
                    }
                    log.info("Provisioning new deviceId {}, productId {}, templId {}.",
                            device.id, product.id, templateId);
                    device.productId = product.id;
                    device.hardwareInfo = hardwareInfo;

                    holder.organizationDao.assignToOrgAndAddDevice(org, device);
                    MetaField[] metaFields = device.metaFields;
                    if (templateId != null) {
                        setTemplateIdInMeta(metaFields, templateId);
                    }
                    setDeviceOwnerInMeta(metaFields, user.email);
                    device.metaFields = metaFields;
                    device.updateNameFromMetafields();
                    holder.deviceDao.createWithPredefinedIdAndToken(orgId, user.email, product, device);

                    for (DashBoard dash : user.profile.dashBoards) {
                        dash.addDeviceToTemplate(device, templateId);
                    }

                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.remove(this)
                            .fireUserEventTriggered(
                                    new ProvisionedDeviceAddedMessage(orgId, user,
                                            device, message.id, product, org.name));
                    break;
            }
        } else {
            log.warn("Expecting only internal command here for user {}", user.email);
        }
    }

}
