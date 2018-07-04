package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetOrganizationsLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationsLogic.class);

    private final OrganizationDao organizationDao;

    public WebGetOrganizationsLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        User user = state.user;
        List<Organization> orgs = organizationDao.getAll(user)
                .stream()
                .filter(org -> org.id != user.orgId && org.parentId == user.orgId)
                .collect(Collectors.toList());

        organizationDao.calcDeviceCount(orgs);

        if (ctx.channel().isWritable()) {
            String orgString = JsonParser.toJson(orgs);
            ctx.writeAndFlush(
                    makeUTF8StringMessage(WEB_GET_ORG, message.id, orgString), ctx.voidPromise());
        }
    }

}
