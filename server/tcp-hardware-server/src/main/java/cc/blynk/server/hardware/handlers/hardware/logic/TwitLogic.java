package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.widgets.notifications.Twitter;
import cc.blynk.server.core.processors.NotificationBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.notifications.twitter.TwitterWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;

import static cc.blynk.server.internal.CommonByteBufUtil.notificationError;
import static cc.blynk.server.internal.CommonByteBufUtil.notificationInvalidBody;
import static cc.blynk.server.internal.CommonByteBufUtil.notificationNotAuthorized;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * Sends tweets from hardware.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class TwitLogic extends NotificationBase {

    private static final Logger log = LogManager.getLogger(TwitLogic.class);

    private final TwitterWrapper twitterWrapper;

    public TwitLogic(Holder holder) {
        super(holder.limits.notificationPeriodLimitSec);
        this.twitterWrapper = holder.twitterWrapper;
    }

    private static void logError(String errorMessage) {
        if (errorMessage != null) {
            if (errorMessage.contains("Status is a duplicate")) {
                log.warn("Duplicate twit status.");
            } else if (errorMessage.contains("Authentication credentials")) {
                log.warn("Tweet authentication failure.");
            } else if (errorMessage.contains("The request is understood, but it has been refused.")) {
                log.warn("User twit account is banned by twitter.");
            } else {
                log.error("Error sending twit. Reason : {}", errorMessage);
            }
        }
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        if (Twitter.isWrongBody(message.body)) {
            log.debug("Notification message is empty or larger than limit.");
            ctx.writeAndFlush(notificationInvalidBody(message.id), ctx.voidPromise());
            return;
        }

        //todo fix?
        var dash = new DashBoard();
        var twitterWidget = dash.getTwitterWidget();

        if (twitterWidget == null || !dash.isActive
                || twitterWidget.token == null || twitterWidget.token.isEmpty()
                || twitterWidget.secret == null || twitterWidget.secret.isEmpty()) {
            log.debug("User has no access token provided for twit widget.");
            ctx.writeAndFlush(notificationNotAuthorized(message.id), ctx.voidPromise());
            return;
        }

        checkIfNotificationQuotaLimitIsNotReached();

        log.trace("Sending Twit, with message : '{}'.", message.body);
        twit(ctx.channel(), twitterWidget.token, twitterWidget.secret, message.body, message.id);
    }

    private void twit(Channel channel, String token, String secret, String body, int msgId) {
        twitterWrapper.send(token, secret, body,
                new AsyncCompletionHandler<>() {
                    @Override
                    public Response onCompleted(Response response) {
                        if (response.getStatusCode() == HttpResponseStatus.OK.code()) {
                            channel.writeAndFlush(ok(msgId), channel.voidPromise());
                        }
                        return response;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        logError(t.getMessage());
                        channel.writeAndFlush(notificationError(msgId), channel.voidPromise());
                    }
                }
        );
    }

}
