package cc.blynk.server.web.handlers.logic.organization.roles;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.PermissionsTable;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.12.18.
 */
public final class WebGetUserCountersByRoleLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final UserDao userDao;

    public WebGetUserCountersByRoleLogic(Holder holder) {
        this.userDao = holder.userDao;
    }

    @Override
    public int getPermission() {
        return PermissionsTable.ROLE_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = state.selectedOrgId;

        List<User> users = userDao.getAllUsersByOrgId(orgId);
        Map<Integer, Integer> counters = userCountByRole(users);

        if (ctx.channel().isWritable()) {
            String countersString = JsonParser.toJson(counters);
            ctx.writeAndFlush(makeASCIIStringMessage(message.command, message.id, countersString),
                    ctx.voidPromise());
        }
    }

    private Map<Integer, Integer> userCountByRole(List<User> users) {
        Map<Integer, Integer> counters = new HashMap<>();
        for (User user : users) {
            int roleId = user.roleId;
            Integer val = counters.get(roleId);
            if (val == null) {
                val = 0;
            }
            counters.put(roleId, ++val);
        }
        return counters;
    }

}
