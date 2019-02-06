package cc.blynk.server.application.handlers.main.logic.face;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileEditAppLogic {

    private static final Logger log = LogManager.getLogger(MobileEditAppLogic.class);

    private final int maxWidgetSize;

    public MobileEditAppLogic(Holder holder) {
        this.maxWidgetSize = holder.limits.widgetSizeLimitBytes;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state,
                                StringMessage message) {
        var appString = message.body;

        if (appString.isEmpty()) {
            throw new JsonException("Income app message is empty.");
        }

        if (appString.length() > maxWidgetSize) {
            throw new JsonException("App is larger then limit.");
        }

        var newApp = JsonParser.parseApp(appString, message.id);

        if (newApp.isNotValid()) {
            throw new JsonException("App is not valid.");
        }

        log.debug("Creating new app {}.", newApp);

        var user = state.user;

        var existingApp = user.profile.getAppById(newApp.id);

        if (existingApp == null) {
            throw new JsonException("App with passed is not exists.");
        }

        existingApp.update(newApp);

        user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
