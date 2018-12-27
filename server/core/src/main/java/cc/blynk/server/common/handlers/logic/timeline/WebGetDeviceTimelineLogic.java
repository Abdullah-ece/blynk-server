package cc.blynk.server.common.handlers.logic.timeline;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetDeviceTimelineLogic implements PermissionBasedLogic<BaseUserStateHolder> {

    private final WebGetOwnDeviceTimelineLogic webGetOwnDeviceTimelineLogic;

    public WebGetDeviceTimelineLogic(Holder holder) {
        this.webGetOwnDeviceTimelineLogic = new WebGetOwnDeviceTimelineLogic(holder);
    }

    @Override
    public int getPermission() {
        return ORG_DEVICES_VIEW;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage msg) {
        webGetOwnDeviceTimelineLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage msg) {
        webGetOwnDeviceTimelineLogic.messageReceived0(ctx, state, msg);
    }

}
