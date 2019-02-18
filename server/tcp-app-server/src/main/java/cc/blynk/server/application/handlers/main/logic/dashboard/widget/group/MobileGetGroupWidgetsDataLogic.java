package cc.blynk.server.application.handlers.main.logic.dashboard.widget.group;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.Granularity;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.group.BaseGroupTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.group.Group;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.db.dao.GroupRequest;
import cc.blynk.server.db.dao.RawEntryWithPin;
import cc.blynk.server.db.dao.ReportingGroupDBDao;
import cc.blynk.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.serverError;
import static cc.blynk.utils.StringUtils.split3;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileGetGroupWidgetsDataLogic {

    private static final Logger log = LogManager.getLogger(MobileGetGroupWidgetsDataLogic.class);

    private final ReportingGroupDBDao reportingGroupDBDao;
    private final BlockingIOProcessor blockingIOProcessor;

    public MobileGetGroupWidgetsDataLogic(Holder holder) {
        this.reportingGroupDBDao = holder.reportingDBManager.reportingGroupDBDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
    }

    public void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        String[] split = split3(message.body);

        if (split.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        long widgetId = Long.parseLong(split[1]);
        long groupId = Long.parseLong(split[2]);

        User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        DeviceTiles deviceTiles = dash.getDeviceTilesByIdOrThrow(widgetId);
        Group group = deviceTiles.getGroupByIdOrThrow(groupId);
        BaseGroupTemplate groupTemplate = deviceTiles.getGroupTemplateByIdOrThrow(group.templateId);

        //todo for now we support only aggregation function and only hour granularity
        List<GroupRequest> groupRequests = new ArrayList<>();
        List<RawEntryWithPin> result = new ArrayList<>();
        for (Widget widget : groupTemplate.widgets) {
            if (widget instanceof OnePinWidget) {
                OnePinWidget onePinWidget = (OnePinWidget) widget;
                //for control widgets we have values in memory so we can fill them right away
                if (widget.getModeType() == PinMode.out) {
                    PinStorageValue pinStorageValue = group.pinStorage.get(onePinWidget.pin, onePinWidget.pinType);
                    if (pinStorageValue != null) {
                        double value = NumberUtil.parseDouble(pinStorageValue.lastValue());
                        if (value != NumberUtil.NO_RESULT) {
                            result.add(new RawEntryWithPin(0L, value, onePinWidget.pin, onePinWidget.pinType));
                        }
                    }
                } else if (widget.getModeType() == PinMode.in) {
                    if (onePinWidget.isValid()) {
                        groupRequests.add(new GroupRequest(
                                        Granularity.HOURLY,
                                        onePinWidget.aggregationFunctionType,
                                        group.deviceIds,
                                        onePinWidget.pin,
                                        onePinWidget.pinType
                                )
                        );
                    }
                }
            }
        }

        blockingIOProcessor.executeReporting(() -> {
            try {
                for (GroupRequest groupRequest : groupRequests) {
                    RawEntryWithPin rawEntry = reportingGroupDBDao.getAverageForGroupOfDevices(groupRequest);
                    if (rawEntry != null) {
                        result.add(rawEntry);
                    }
                }
                String dataJson = JsonParser.toJson(result);
                ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, dataJson));
            } catch (Exception e) {
                log.error("Error processing group get request.", e);
                ctx.writeAndFlush(serverError(message.id, "Error processing group get request."), ctx.voidPromise());
            }
        });
    }

}
