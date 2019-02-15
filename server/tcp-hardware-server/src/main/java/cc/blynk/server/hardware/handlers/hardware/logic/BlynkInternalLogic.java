package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.device.ota.DeviceShipmentInfo;
import cc.blynk.server.core.model.device.ota.ShipmentDeviceStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.Shipment;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.internal.token.ShipmentFirmwareDownloadToken;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

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

    private final int hardwareIdleTimeout;
    private final TokensPool tokensPool;
    private final ServerProperties props;
    private final SessionDao sessionDao;
    private final ReportingDBManager reportingDBManager;

    public BlynkInternalLogic(Holder holder) {
        this.hardwareIdleTimeout = holder.limits.hardwareIdleTimeout;
        this.tokensPool = holder.tokensPool;
        this.props = holder.props;
        this.sessionDao = holder.sessionDao;
        this.reportingDBManager = holder.reportingDBManager;
    }

    private static void sendRTC(ChannelHandlerContext ctx, int msgId) {
        //todo rtc should be inherited from the product or user that provision device?
        //if (rtc != null && ctx.channel().isWritable()) {
        //    ctx.writeAndFlush(makeASCIIStringMessage(BLYNK_INTERNAL, msgId, "rtc" + BODY_SEPARATOR + rtc.getTime()),
        //            ctx.voidPromise());
        //}
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                HardwareStateHolder state, StringMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (messageParts.length == 0 || messageParts[0].isEmpty()) {
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
                parseHardwareInfo(ctx, messageParts, state, message.id);
                break;
            case 'r' : //rtc
                sendRTC(ctx, message.id);
                break;
            case 'a' :
            case 'o' :
                break;
        }

    }

    private void parseHardwareInfo(ChannelHandlerContext ctx,
                                   String[] messageParts,
                                   HardwareStateHolder state, int msgId) {
        HardwareInfo hardwareInfo = new HardwareInfo(messageParts);
        int newHardwareInterval = hardwareInfo.heartbeatInterval;

        log.trace("Info command. heartbeat interval {}", newHardwareInterval);

        //no need to change IdleStateHandler if heartbeat interval wasn't changed or wasn't provided
        if (hardwareIdleTimeout != 0 && newHardwareInterval > 0 && newHardwareInterval != hardwareIdleTimeout) {
            int newReadTimeout = NumberUtil.calcHeartbeatTimeout(newHardwareInterval);
            log.debug("Changing read timeout interval to {}", newReadTimeout);
            ctx.pipeline().replace(IdleStateHandler.class,
                    "H_IdleStateHandler_Replaced", new IdleStateHandler(newReadTimeout, 0, 0));
        }

        Device device = state.device;

        if (device != null) {
            processOTA(ctx, state.org, device, hardwareInfo, msgId);

            //special temporary hotfix https://github.com/blynkkk/dash/issues/1765
            String templateId = hardwareInfo.templateId;
            if (templateId == null) {
                Product product = null;
                if ("0.7.0".equals(hardwareInfo.blynkVersion)) {
                    String productName = "Airius Fan";
                    product = state.org.getProductByName(productName);
                    if (product == null) {
                        log.error("Didn't find product by name {} for orgId={}.", productName, state.org.id);
                    }
                }
                if (product == null) {
                    product = state.org.getFirstProduct();
                }
                templateId = product.getFirstTemplateId();
                hardwareInfo.templateId = templateId;
            }
            device.setHardwareInfo(hardwareInfo);
        }

        ctx.writeAndFlush(ok(msgId), ctx.voidPromise());
    }

    private void processOTA(ChannelHandlerContext ctx,
                                   Organization org, Device device, HardwareInfo hardwareInfo, int msgId) {
        DeviceShipmentInfo deviceShipmentInfo = device.deviceShipmentInfo;
        if (deviceShipmentInfo == null) {
            return;
        }

        Shipment shipment = org.getShipmentById(deviceShipmentInfo.shipmentId);
        if (shipment == null || shipment.firmwareInfo == null) {
            log.trace("Shipment by id {} not found or empty firmware info.", deviceShipmentInfo.shipmentId);
            return;
        }

        if (hardwareInfo.isFirmwareVersionChanged(shipment.firmwareInfo.buildDate)) {
            if (deviceShipmentInfo.status != null && deviceShipmentInfo.status.isNotFailure()) {
                if (deviceShipmentInfo.isLimitReached(shipment.attemptsLimit)) {
                    log.warn("OTA limit reached for deviceId {}.", device.id);
                    Session session = sessionDao.getOrgSession(org.id);
                    device.firmwareDownloadLimitReached(session, msgId, shipment);
                    reportingDBManager.collectEvent(shipment, device);
                } else {
                    String serverUrl = props.getServerUrl(shipment.isSecure);
                    String downloadToken = TokenGeneratorUtil.generateNewToken();
                    tokensPool.addToken(downloadToken, new ShipmentFirmwareDownloadToken(device.id));
                    String body = StringUtils.makeHardwareBody(serverUrl,
                            shipment.pathToFirmware, downloadToken);
                    StringMessage msg = makeASCIIStringMessage(BLYNK_INTERNAL, 7777, body);
                    ctx.write(msg, ctx.voidPromise());
                    device.requestSent();
                    reportingDBManager.collectEvent(shipment, device);
                }
            }
        } else {
            if (deviceShipmentInfo.status == ShipmentDeviceStatus.FIRMWARE_UPLOADED) {
                Session session = sessionDao.getOrgSession(org.id);
                device.success(session, msgId, shipment);
                reportingDBManager.collectEvent(shipment, device);
            }
        }
    }

}
