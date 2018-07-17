package cc.blynk.server;

import cc.blynk.utils.properties.GCMProperties;

import static cc.blynk.utils.FileLoaderUtil.readAppResetEmailConfirmationTemplateAsString;
import static cc.blynk.utils.FileLoaderUtil.readAppResetEmailTemplateAsString;
import static cc.blynk.utils.FileLoaderUtil.readDynamicMailBody;
import static cc.blynk.utils.FileLoaderUtil.readLogEventMailBody;
import static cc.blynk.utils.FileLoaderUtil.readRegisterEmailTemplate;
import static cc.blynk.utils.FileLoaderUtil.readResetPassLandingTemplateAsString;
import static cc.blynk.utils.FileLoaderUtil.readStaticMailBody;
import static cc.blynk.utils.FileLoaderUtil.readTemplateIdMailBody;
import static cc.blynk.utils.FileLoaderUtil.readTokenMailBody;
import static cc.blynk.utils.FileLoaderUtil.readWebResetPassMailBody;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.03.18.
 */
public class TextHolder {

    public volatile String tokenBody;
    public final String dynamicMailBody;
    public final String staticMailBody;
    public final String templateIdMailBody;
    public final String pushNotificationBody;
    public final String resetPassLandingTemplate;
    public final String webResetEmailTemplate;
    public final String appResetEmailTemplate;
    public final String appResetEmailConfirmationTemplate;
    public final String registerEmailTemplate;
    public final String logEventMailBody;

    TextHolder(GCMProperties gcmProperties) {
        this.tokenBody = readTokenMailBody();
        this.dynamicMailBody = readDynamicMailBody();
        this.staticMailBody = readStaticMailBody();
        this.templateIdMailBody = readTemplateIdMailBody();
        this.pushNotificationBody = gcmProperties.getNotificationBody();
        this.resetPassLandingTemplate = readResetPassLandingTemplateAsString();
        this.webResetEmailTemplate = readWebResetPassMailBody();
        this.appResetEmailTemplate = readAppResetEmailTemplateAsString();
        this.appResetEmailConfirmationTemplate = readAppResetEmailConfirmationTemplateAsString();
        this.registerEmailTemplate = readRegisterEmailTemplate();
        this.logEventMailBody = readLogEventMailBody();
    }
}
