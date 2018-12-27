package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.RuleGroup;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.RULE_GROUP_EDIT;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 26.12.18.
 */
public final class WebEditRuleGroupLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebEditRuleGroupLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canEditRuleGroup();
    }

    @Override
    public int getPermission() {
        return RULE_GROUP_EDIT;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        RuleGroup ruleGroup = JsonParser.readAny(message.body, RuleGroup.class);

        int orgId = state.selectedOrgId;
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);

        org.ruleGroup = ruleGroup;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
