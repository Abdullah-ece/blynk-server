package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split3;

/**
 * Handler that allows to change widget properties from hardware side.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class SetWidgetPropertyLogic {

    private static final Logger log = LogManager.getLogger(SetWidgetPropertyLogic.class);

    private SetWidgetPropertyLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       HardwareStateHolder state, StringMessage message) {
        SessionDao sessionDao = holder.sessionDao;

        String[] bodyParts = split3(message.body);

        if (bodyParts.length != 3) {
            log.debug("SetWidgetProperty command body has wrong format. {}", message.body);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        String property = bodyParts[1];
        String propertyValue = bodyParts[2];

        if (property.isEmpty() || propertyValue.isEmpty()) {
            log.debug("SetWidgetProperty command body has wrong format. {}", message.body);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        WidgetProperty widgetProperty = WidgetProperty.getProperty(property);

        if (widgetProperty == null) {
            log.debug("Unsupported set property {}.", property);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        int deviceId = state.device.id;
        short pin = NumberUtil.parsePin(bodyParts[0]);

        state.device.updateValue(pin, widgetProperty, propertyValue);

        Session session = sessionDao.getOrgSession(state.org.id);
        session.sendToApps(SET_WIDGET_PROPERTY, message.id, message.body, deviceId);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
