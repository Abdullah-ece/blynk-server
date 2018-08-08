package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    public WebGetOrganizationLocationsLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = Integer.parseInt(message.body);

        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access organization he has no access.");
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        List<String> existingLocations = new ArrayList<>();
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        for (Product product : org.products) {
            for (MetaField metaField : product.metaFields) {
                if (metaField.isDefault && "Location Name".equalsIgnoreCase(metaField.name)) {
                    existingLocations.add(((TextMetaField) metaField).value);
                }
            }
        }

        if (ctx.channel().isWritable()) {
            String usersString = JsonParser.toJson(existingLocations);
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, usersString),
                    ctx.voidPromise());
        }
    }

}
