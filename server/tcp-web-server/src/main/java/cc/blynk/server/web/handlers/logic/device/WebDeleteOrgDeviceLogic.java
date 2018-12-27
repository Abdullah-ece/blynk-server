package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_DELETE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebDeleteOrgDeviceLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final WebDeleteOwnDeviceLogic webDeleteOwnDeviceLogic;

    public WebDeleteOrgDeviceLogic(Holder holder) {
        this.webDeleteOwnDeviceLogic = new WebDeleteOwnDeviceLogic(holder);
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_DELETE;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webDeleteOwnDeviceLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        webDeleteOwnDeviceLogic.messageReceived0(ctx, state, message);
    }

}
