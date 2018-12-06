package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.web.handlers.logic.organization.dto.OrganizationsHierarchyDTO;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_SWITCH;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.18.
 */
public class WebGetOrganizationsHierarchyLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;
    private Set<Organization> allOrgs;
    private int invocationCounter;

    public WebGetOrganizationsHierarchyLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canSwitchOrg();
    }

    @Override
    public int getPermission() {
        return ORG_SWITCH;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        Organization userOrg = organizationDao.getOrgByIdOrThrow(state.orgId);
        allOrgs = new HashSet<>(organizationDao.getAll());
        invocationCounter = 0;
        OrganizationsHierarchyDTO result = buildOrgHierarchy(userOrg);

        if (ctx.channel().isWritable()) {
            String resultString = result.toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, resultString), ctx.voidPromise());
        }
    }

    private OrganizationsHierarchyDTO buildOrgHierarchy(Organization parentOrg) {
        invocationCounter++;
        if (invocationCounter == 1000) {
            throw new RuntimeException("Error building organization hierarchy.");
        }
        var organizationsHierarchyDTO = new OrganizationsHierarchyDTO(parentOrg, new TreeSet<>());
        for (Organization org : allOrgs) {
            if (org.isChildOf(parentOrg.id)) {
                organizationsHierarchyDTO.childs.add(buildOrgHierarchy(org));
            }

        }
        return organizationsHierarchyDTO;
    }
}
