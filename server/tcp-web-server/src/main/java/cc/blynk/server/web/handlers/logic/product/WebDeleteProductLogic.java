package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebDeleteProductLogic {

    private static final Logger log = LogManager.getLogger(WebDeleteProductLogic.class);

    private final OrganizationDao organizationDao;

    public WebDeleteProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int productId = Integer.parseInt(message.body);

        User user = state.user;

        boolean isRemoved = organizationDao.deleteProduct(user, productId);

        if (isRemoved) {
            log.debug("Product {} successfully deleted for {}", productId, user.email);
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
        }
    }

}
