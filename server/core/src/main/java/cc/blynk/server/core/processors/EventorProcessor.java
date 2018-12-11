package cc.blynk.server.core.processors;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.profile.NotificationSettings;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.notifications.Mail;
import cc.blynk.server.core.model.widgets.notifications.Twitter;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.others.eventor.Rule;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.BaseAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPropertyPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.notification.MailAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.notification.NotificationAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.notification.NotifyAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.notification.TwitAction;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.notifications.push.GCMWrapper;
import cc.blynk.server.notifications.twitter.TwitterWrapper;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.validators.BlynkEmailValidator;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;

import static cc.blynk.server.core.protocol.enums.Command.EVENTOR;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.utils.StringUtils.PIN_PATTERN;

/**
 * Class responsible for handling eventor logic.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.16.
 */
public class EventorProcessor {

    private static final Logger log = LogManager.getLogger(EventorProcessor.class);

    private final GCMWrapper gcmWrapper;
    private final TwitterWrapper twitterWrapper;
    private final MailWrapper mailWrapper;
    private final BlockingIOProcessor blockingIOProcessor;
    private final GlobalStats globalStats;

    public EventorProcessor(GCMWrapper gcmWrapper, MailWrapper mailWrapper, TwitterWrapper twitterWrapper,
                            BlockingIOProcessor blockingIOProcessor, GlobalStats stats) {
        this.gcmWrapper = gcmWrapper;
        this.mailWrapper = mailWrapper;
        this.twitterWrapper = twitterWrapper;
        this.blockingIOProcessor = blockingIOProcessor;
        this.globalStats = stats;
    }

    public static void push(GCMWrapper gcmWrapper, NotificationSettings notificationSettings,
                            int deviceId, String body) {
        if (NotificationSettings.isWrongBody(body)) {
            log.debug("Wrong push body.");
            return;
        }

        if (notificationSettings.hasNoToken()) {
            log.debug("User has no access token provided for eventor push.");
            return;
        }

        gcmWrapper.sendAndroid(notificationSettings.androidTokens, notificationSettings.priority, body, deviceId);
        gcmWrapper.sendIOS(notificationSettings.iOSTokens, notificationSettings.priority, body, deviceId);
    }

    private void execute(User user, Device device, DashBoard dash,
                         String triggerValue, NotificationAction notificationAction) {
        String body = PIN_PATTERN.matcher(notificationAction.message).replaceAll(triggerValue);
        if (notificationAction instanceof NotifyAction) {
            push(gcmWrapper, user.profile.settings.notificationSettings, device.id, body);
        } else if (notificationAction instanceof TwitAction) {
            twit(dash, body);
        } else if (notificationAction instanceof MailAction) {
            MailAction mailAction = (MailAction) notificationAction;
            email(user, dash, mailAction.subject, body);
        }
    }

    public void process(User user, Session session, DashBoard dash, Device device, short pin,
                        PinType type, String triggerValue) {
        Eventor eventor = dash.getEventorWidget();
        if (eventor == null || eventor.rules == null
                || eventor.deviceId != device.id || !dash.isActive) {
            return;
        }

        double valueParsed = NumberUtil.parseDouble(triggerValue);

        for (Rule rule : eventor.rules) {
            if (rule.isReady(pin, type)) {
                if (rule.matchesCondition(triggerValue, valueParsed)) {
                    if (!rule.isProcessed) {
                        for (BaseAction action : rule.actions) {
                            if (action.isValid()) {
                                if (action instanceof SetPinAction) {
                                    execute(session, dash, device, (SetPinAction) action);
                                } else if (action instanceof SetPropertyPinAction) {
                                    execute(session, dash, device, (SetPropertyPinAction) action);
                                } else if (action instanceof NotificationAction) {
                                    execute(user, device, dash, triggerValue, (NotificationAction) action);
                                }
                                globalStats.mark(EVENTOR);
                            }
                        }
                        rule.isProcessed = true;
                    }
                } else {
                    rule.isProcessed = false;
                }
            }
        }
    }

    private void email(User user, DashBoard dash, String subject, String body) {
        Mail mail = dash.getMailWidget();

        if (mail == null) {
            log.debug("User has no mail widget.");
            return;
        }

        user.checkDailyEmailLimit();

        String to = (mail.to == null || mail.to.isEmpty()) ? user.email : mail.to;

        if (BlynkEmailValidator.isNotValidEmail(to)) {
            log.error("Invalid mail receiver: {}.", to);
            return;
        }

        blockingIOProcessor.execute(() -> {
            try {
                mailWrapper.sendText(to, subject, body);
            } catch (Exception e) {
                log.warn("Error sending email from eventor. From user {}, to : {}. Reason : {}",
                        user.email, to, e.getMessage());
            }
        });
        user.emailMessages++;
    }

    private void twit(DashBoard dash, String body) {
        if (Twitter.isWrongBody(body)) {
            log.debug("Wrong twit body.");
            return;
        }

        Twitter twitterWidget = dash.getTwitterWidget();

        if (twitterWidget == null
                || twitterWidget.token == null
                || twitterWidget.token.isEmpty()
                || twitterWidget.secret == null
                || twitterWidget.secret.isEmpty()) {
            log.debug("User has no access token provided for eventor twit.");
            return;
        }

        twitterWrapper.send(twitterWidget.token, twitterWidget.secret, body,
                new AsyncCompletionHandler<>() {
                    @Override
                    public Response onCompleted(Response response) {
                        if (response.getStatusCode() != HttpResponseStatus.OK.code()) {
                            log.debug("Error sending twit from eventor. Reason : {}.", response.getResponseBody());
                        }
                        return response;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        log.debug("Error sending twit from eventor.", t);
                    }
                }
        );
    }

    private void execute(Session session, DashBoard dash,
                         Device device, SetPinAction action) {
        String body = action.makeHardwareBody();
        int deviceId = device.id;
        session.sendMessageToHardware(HARDWARE, 888, body, deviceId);
        session.sendToApps(HARDWARE, 888, deviceId, body);
        device.updateValue(action.dataStream.pin, action.dataStream.pinType, action.value);
    }

    private void execute(Session session, DashBoard dash,
                         Device device, SetPropertyPinAction action) {
        String body = action.makeHardwareBody();
        session.sendToApps(SET_WIDGET_PROPERTY, 888, device.id, body);

        Widget widget = dash.updateProperty(device.id, action.dataStream.pin, action.property, action.value);
        //this is possible case for device selector
        if (widget == null) {
            short pin = action.dataStream.pin;
            WidgetProperty property = action.property;
            device.updateValue(pin, property, action.value);
        }
    }
}
