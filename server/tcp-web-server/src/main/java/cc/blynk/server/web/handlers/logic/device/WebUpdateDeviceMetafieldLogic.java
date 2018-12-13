package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_EDIT;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.09.18.
 */
public final class WebUpdateDeviceMetafieldLogic implements PermissionBasedLogic {

    private final WebUpdateOwnDeviceMetafieldLogic webUpdateOwnDeviceMetafieldLogic;

    public WebUpdateDeviceMetafieldLogic(Holder holder) {
        this.webUpdateOwnDeviceMetafieldLogic = new WebUpdateOwnDeviceMetafieldLogic(holder);
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canEditOrgDevice();
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_EDIT;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage msg) {
        webUpdateOwnDeviceMetafieldLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx,
                                 BaseUserStateHolder state, StringMessage message) {
        webUpdateOwnDeviceMetafieldLogic.messageReceived0(ctx, state, message);
    }

}
