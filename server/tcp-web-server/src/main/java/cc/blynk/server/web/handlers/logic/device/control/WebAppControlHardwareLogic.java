package cc.blynk.server.web.handlers.logic.device.control;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_EDIT;

/**
 * Responsible for handling incoming hardware commands from applications and forwarding it to
 * appropriate hardware.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebAppControlHardwareLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final WebAppOwnControlHardwareLogic webAppOwnControlHardwareLogic;

    public WebAppControlHardwareLogic(Holder holder) {
        this.webAppOwnControlHardwareLogic = new WebAppOwnControlHardwareLogic(holder);
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
    public void noPermissionAction(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webAppOwnControlHardwareLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webAppOwnControlHardwareLogic.messageReceived0(ctx, state, msg);
    }

}
