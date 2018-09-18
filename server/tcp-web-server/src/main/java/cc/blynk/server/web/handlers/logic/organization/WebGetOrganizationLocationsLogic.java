package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.LocationMetaField;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetOrganizationLocationsLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationLocationsLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebGetOrganizationLocationsLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        int orgId = organizationDao.getOrganizationIdByProductId(productId);
        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access organization he has no access.", state.user.email);
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        List<Device> devices = deviceDao.getAllByProductId(productId);
        Set<LocationDTO> existingLocations = getLocations(devices);

        if (ctx.channel().isWritable()) {
            String locationsString = JsonParser.toJson(existingLocations);
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, locationsString),
                    ctx.voidPromise());
        }
    }

    private Set<LocationDTO> getLocations(List<Device> devices) {
        Set<LocationDTO> existingLocations = new HashSet<>();
        for (Device device : devices) {
            for (MetaField metaField : device.metaFields) {
                if (metaField instanceof LocationMetaField) {
                    LocationMetaField locationMetaField = (LocationMetaField) metaField;
                    existingLocations.add(new LocationDTO(locationMetaField.siteName, device.id));
                    if (existingLocations.size() == 10) {
                        return existingLocations;
                    }
                }
            }
        }
        return existingLocations;
    }

}
