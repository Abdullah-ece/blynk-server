package cc.blynk.server.web.handlers.logic.product.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.utils.FileUtils;
import io.netty.channel.ChannelHandlerContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.12.18.
 */
public final class WebGetOtaFirmwareInfoLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final String staticFilesFolder;

    public WebGetOtaFirmwareInfoLogic(Holder holder) {
        this.staticFilesFolder = holder.props.jarPath;
    }

    @Override
    public int getPermission() {
        return OTA_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String pathToFirmware = message.body;

        if (pathToFirmware.isEmpty()) {
            log.error("Path to firmware is not provided. User {}.", state.user.email);
            throw new JsonException("Path to firmware is not provided.");
        }

        //todo check access
        Path path = Paths.get(staticFilesFolder, pathToFirmware);
        Map<String, String> firmwareInfoDTO = FileUtils.getPatternFromString(path);
        String firmwareParamsString = firmwareInfoDTO.toString();
        StringMessage response = makeUTF8StringMessage(message.command, message.id, firmwareParamsString);
        ctx.writeAndFlush(response, ctx.voidPromise());
    }
}
