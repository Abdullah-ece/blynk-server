package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_CREATE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebCreateOrgDeviceLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final WebCreateOwnDeviceLogic webCreateOwnDeviceLogic;

    public WebCreateOrgDeviceLogic(Holder holder) {
        this.webCreateOwnDeviceLogic = new WebCreateOwnDeviceLogic(holder);
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_CREATE;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webCreateOwnDeviceLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webCreateOwnDeviceLogic.messageReceived0(ctx, state, msg);
    }

}
