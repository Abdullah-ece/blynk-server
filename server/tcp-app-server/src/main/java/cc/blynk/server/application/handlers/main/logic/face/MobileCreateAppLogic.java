package cc.blynk.server.application.handlers.main.logic.face;

import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.CREATE_APP;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileCreateAppLogic {

    private static final Logger log = LogManager.getLogger(MobileCreateAppLogic.class);

    private MobileCreateAppLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state,
                                       StringMessage message, int maxWidgetSize) {
        var appString = message.body;

        if (appString.isEmpty()) {
            throw new JsonException("Income app message is empty.");
        }

        if (appString.length() > maxWidgetSize) {
            throw new JsonException("App is larger then limit.");
        }

        var newApp = JsonParser.parseApp(appString, message.id);

        newApp.id = AppNameUtil.generateAppId();

        if (newApp.isNotValid()) {
            throw new JsonException("App is not valid.");
        }

        log.debug("Creating new app {}.", newApp);

        var user = state.user;

        if (user.profile.apps.length > 25) {
            throw new JsonException("App limit is reached.");
        }

        for (App app : user.profile.apps) {
            if (app.id.equals(newApp.id)) {
                throw new JsonException("App with same id already exists.");
            }
        }

        user.profile.apps = ArrayUtil.add(user.profile.apps, newApp, App.class);
        user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(makeUTF8StringMessage(CREATE_APP, message.id, JsonParser.toJson(newApp)), ctx.voidPromise());
    }

}
