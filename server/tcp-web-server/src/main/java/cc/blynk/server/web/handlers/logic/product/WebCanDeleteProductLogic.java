package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebCanDeleteProductLogic {

    private final DeviceDao deviceDao;

    public WebCanDeleteProductLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        if (deviceDao.productHasDevices(productId)) {
            ctx.writeAndFlush(json(message.id, "You can't delete product with devices."), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        }
    }

}
