package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.LocationMetaField;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.logic.organization.dto.LocationDTO;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetProductLocationsLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final DeviceDao deviceDao;

    public WebGetProductLocationsLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewProduct();
    }

    @Override
    public int getPermission() {
        return PRODUCT_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = StringUtils.split2(message.body);
        int productId = Integer.parseInt(split[0]);
        String searchString = null;
        if (split.length == 2) {
            searchString = split[1];
        }

        List<Device> devices = deviceDao.getAllByProductId(productId);
        Set<LocationDTO> existingLocations = getLocations(devices, searchString);

        if (ctx.channel().isWritable()) {
            String locationsString = JsonParser.toJson(existingLocations);
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, locationsString),
                    ctx.voidPromise());
        }
    }

    private Set<LocationDTO> getLocations(List<Device> devices, String searchString) {
        Set<LocationDTO> existingLocations = new HashSet<>();
        for (Device device : devices) {
            for (MetaField metaField : device.metaFields) {
                if (metaField instanceof LocationMetaField) {
                    LocationMetaField locationMetaField = (LocationMetaField) metaField;
                    if (searchString == null || locationMetaField.matches(searchString)) {
                        existingLocations.add(new LocationDTO(locationMetaField, device.id));
                        if (existingLocations.size() == 10) {
                            return existingLocations;
                        }
                    }
                }
            }
        }
        return existingLocations;
    }

}
