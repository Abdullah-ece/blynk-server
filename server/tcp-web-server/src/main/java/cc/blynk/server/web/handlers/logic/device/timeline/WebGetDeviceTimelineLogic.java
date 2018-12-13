package cc.blynk.server.web.handlers.logic.device.timeline;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetDeviceTimelineLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final WebGetOwnDeviceTimelineLogic webGetOwnDeviceTimelineLogic;

    public WebGetDeviceTimelineLogic(Holder holder) {
        this.webGetOwnDeviceTimelineLogic = new WebGetOwnDeviceTimelineLogic(holder);
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewOrgDevices();
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_VIEW;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webGetOwnDeviceTimelineLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webGetOwnDeviceTimelineLogic.messageReceived0(ctx, state, msg);
    }

}
