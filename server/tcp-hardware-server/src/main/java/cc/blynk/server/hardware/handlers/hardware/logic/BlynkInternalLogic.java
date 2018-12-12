package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.ota.OTAInfo;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.widgets.others.rtc.RTC;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.internal.token.OTADownloadToken;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

/**
 *
 * Simple handler that accepts info command from hardware.
 * At the moment only 1 param is used "h-beat".
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class BlynkInternalLogic {

    private static final Logger log = LogManager.getLogger(BlynkInternalLogic.class);

    private BlynkInternalLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       HardwareStateHolder state, StringMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (messageParts.length == 0 || messageParts[0].length() == 0) {
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String cmd = messageParts[0];

        switch (cmd.charAt(0)) {
            case 'v' : //ver
            case 'f' : //fw
            case 'h' : //h-beat
            case 'b' : //buff-in
            case 'd' : //dev
            case 'c' : //cpu
            case 't' : //tmpl
                parseHardwareInfo(holder, ctx, messageParts, state, message.id);
                break;
            case 'r' : //rtc
                sendRTC(ctx, state, message.id);
                break;
            case 'a' :
            case 'o' :
                break;
        }

    }

    private static void sendRTC(ChannelHandlerContext ctx, HardwareStateHolder state, int msgId) {
        //todo fix?
        DashBoard dashBoard = new DashBoard();
        RTC rtc = dashBoard.getWidgetByType(RTC.class);
        if (rtc != null && ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeASCIIStringMessage(BLYNK_INTERNAL, msgId, "rtc" + BODY_SEPARATOR + rtc.getTime()),
                    ctx.voidPromise());
        }
    }

    private static void parseHardwareInfo(Holder holder, ChannelHandlerContext ctx,
                                          String[] messageParts,
                                          HardwareStateHolder state, int msgId) {
        HardwareInfo hardwareInfo = new HardwareInfo(messageParts);
        int newHardwareInterval = hardwareInfo.heartbeatInterval;

        log.trace("Info command. heartbeat interval {}", newHardwareInterval);
        int hardwareIdleTimeout = holder.limits.hardwareIdleTimeout;

        //no need to change IdleStateHandler if heartbeat interval wasn't changed or wasn't provided
        if (hardwareIdleTimeout != 0 && newHardwareInterval > 0 && newHardwareInterval != hardwareIdleTimeout) {
            int newReadTimeout = NumberUtil.calcHeartbeatTimeout(newHardwareInterval);
            log.debug("Changing read timeout interval to {}", newReadTimeout);
            ctx.pipeline().replace(IdleStateHandler.class,
                    "H_IdleStateHandler_Replaced", new IdleStateHandler(newReadTimeout, 0, 0));
        }

        Device device = state.device;

        if (device != null) {
            DeviceOtaInfo deviceOtaInfo = device.deviceOtaInfo;
            if (deviceOtaInfo != null) {
                if (hardwareInfo.isFirmwareVersionChanged(deviceOtaInfo.buildDate)) {
                    if (deviceOtaInfo.otaStatus.isNotFailure()) {
                        if (device.isAttemptsLimitReached()) {
                            log.warn("OTA limit reached for deviceId {}.", device.id);
                            device.firmwareDownloadLimitReached();
                        } else {
                            String serverUrl = holder.props.getServerUrl(deviceOtaInfo.isSecure);
                            String downloadToken = TokenGeneratorUtil.generateNewToken();
                            holder.tokensPool.addToken(downloadToken, new OTADownloadToken(device.id));
                            String body = OTAInfo.makeHardwareBody(serverUrl,
                                    deviceOtaInfo.pathToFirmware, downloadToken);
                            StringMessage msg = makeASCIIStringMessage(BLYNK_INTERNAL, 7777, body);
                            ctx.write(msg, ctx.voidPromise());
                            device.requestSent();
                        }
                    }
                } else {
                    if (deviceOtaInfo.otaStatus == OTAStatus.FIRMWARE_UPLOADED) {
                        device.success();
                    }
                }
            }

            //special temporary hotfix https://github.com/blynkkk/dash/issues/1765
            String templateId = hardwareInfo.templateId;
            if (templateId == null) {
                Organization org = holder.organizationDao.getOrgByIdOrThrow(state.orgId);

                Product product = null;
                if ("0.7.0".equals(hardwareInfo.blynkVersion)) {
                    String productName = "Airius Fan";
                    product = org.getProductByName(productName);
                    if (product == null) {
                        log.error("Didn't find product by name {} for orgId={}.", productName, state.orgId);
                    }
                }
                if (product == null) {
                    product = org.getFirstProduct();
                }
                templateId = product.getFirstTemplateId();
                hardwareInfo.templateId = templateId;
            }
            device.hardwareInfo = hardwareInfo;
        }

        ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
    }

}
