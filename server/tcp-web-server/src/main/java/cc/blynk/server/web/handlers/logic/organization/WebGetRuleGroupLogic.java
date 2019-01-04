package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.Permission2BasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.RULE_GROUP_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 26.12.18.
 */
public final class WebGetRuleGroupLogic implements Permission2BasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebGetRuleGroupLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public int getPermission() {
        return RULE_GROUP_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = state.selectedOrgId;
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);

        String ruleGroupString = JsonParser.toJson(org.ruleGroup);

        ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, ruleGroupString), ctx.voidPromise());
    }

}
