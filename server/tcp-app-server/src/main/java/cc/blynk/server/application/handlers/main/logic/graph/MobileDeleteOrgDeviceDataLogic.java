package cc.blynk.server.application.handlers.main.logic.graph;

import cc.blynk.server.Holder;
import cc.blynk.server.core.Permission2BasedLogic;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICE_DATA_DELETE;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 29/01/2019.
 */
public class MobileDeleteOrgDeviceDataLogic implements Permission2BasedLogic<MobileStateHolder> {

    private final MobileDeleteOwnDeviceDataLogic mobileDeleteOwnDeviceDataLogic;

    public MobileDeleteOrgDeviceDataLogic(Holder holder) {
        this.mobileDeleteOwnDeviceDataLogic = new MobileDeleteOwnDeviceDataLogic(holder);
    }

    @Override
    public int getPermission() {
        return ORG_DEVICE_DATA_DELETE;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage msg) {
        mobileDeleteOwnDeviceDataLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        mobileDeleteOwnDeviceDataLogic.messageReceived0(ctx, state, message);
    }
}
