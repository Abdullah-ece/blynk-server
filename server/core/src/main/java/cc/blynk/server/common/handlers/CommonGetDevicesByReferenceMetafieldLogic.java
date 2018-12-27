package cc.blynk.server.common.handlers;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.IdNameDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.09.18.
 */
public final class CommonGetDevicesByReferenceMetafieldLogic {

    private static final Logger log = LogManager.getLogger(CommonGetDevicesByReferenceMetafieldLogic.class);

    private CommonGetDevicesByReferenceMetafieldLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       BaseUserStateHolder state, StringMessage message) {
        String[] split = StringUtils.split2(message.body);
        int deviceId = Integer.parseInt(split[0]);
        Device device = holder.deviceDao.getByIdOrThrow(deviceId);

        int metafieldId = Integer.parseInt(split[1]);
        MetaField metaField = device.findMetaFieldByIdOrThrow(metafieldId);

        if (!(metaField instanceof DeviceReferenceMetaField)) {
            log.debug("Wrong metafield type {} for {}", metaField, state.user.email);
            throw new IllegalCommandException("Metafield is not DeviceReferenceMetaField.");
        }

        //we allow to view devices only from the current org of this user
        //todo security checks
        int orgId = state.selectedOrgId;
        Organization org = holder.organizationDao.getOrgByIdOrThrow(orgId);

        DeviceReferenceMetaField deviceReferenceMetaField = (DeviceReferenceMetaField) metaField;
        List<IdNameDTO> result = new ArrayList<>();
        for (int productId : deviceReferenceMetaField.selectedProductIds) {
            int[] subProductIds = holder.organizationDao.getProductChilds(productId);
            for (Product product : org.products) {
                if (ArrayUtil.contains(subProductIds, product.id)) {
                    for (Device tempDevice : product.devices) {
                        result.add(new IdNameDTO(tempDevice));
                    }
                }
            }
        }

        String jsonResponse = JsonParser.toJson(result);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, jsonResponse), ctx.voidPromise());
        }
    }

}
