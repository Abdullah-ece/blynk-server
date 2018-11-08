package cc.blynk.server.core.model;

import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.serialization.View;
import cc.blynk.server.core.model.storage.DashPinStorageKeyDeserializer;
import cc.blynk.server.core.model.storage.PinStorageValueDeserializer;
import cc.blynk.server.core.model.storage.key.DashPinPropertyStorageKey;
import cc.blynk.server.core.model.storage.key.DashPinStorageKey;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.widgets.MobileSyncWidget;
import cc.blynk.server.core.model.widgets.MultiPinWidget;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Target;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.ui.DeviceSelector;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.Tile;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static cc.blynk.server.core.model.widgets.MobileSyncWidget.ANY_TARGET;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_APPS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_DASHBOARDS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_TAGS;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class Profile {

    public volatile DashBoard[] dashBoards = EMPTY_DASHBOARDS;

    public volatile App[] apps = EMPTY_APPS;

    public volatile Tag[] tags = EMPTY_TAGS;

    @JsonView(View.Private.class)
    @JsonDeserialize(keyUsing = DashPinStorageKeyDeserializer.class,
                     contentUsing = PinStorageValueDeserializer.class)
    public final Map<DashPinStorageKey, PinStorageValue> pinsStorage = new HashMap<>();

    //todo this method is very wrong, need to something with it.
    private static final DashBoard EMPTY_DASH = new DashBoard();
    public DashBoard getFirstDashOrEmpty() {
        if (dashBoards.length == 0) {
            return EMPTY_DASH;
        }
        return dashBoards[0];
    }

    public void deleteTag(int tagId) {
        int existingTagIndex = getTagIndexByIdOrThrow(tagId);
        this.tags = ArrayUtil.remove(this.tags, existingTagIndex, Tag.class);
    }

    public void addTag(Tag newTag) {
        this.tags = ArrayUtil.add(this.tags, newTag, Tag.class);
    }

    private int getTagIndexByIdOrThrow(int id) {
        for (int i = 0; i < this.tags.length; i++) {
            if (this.tags[i].id == id) {
                return i;
            }
        }
        throw new IllegalCommandException("Tag with passed id not found.");
    }

    public Tag getTagById(int id) {
        for (Tag tag : this.tags) {
            if (tag.id == id) {
                return tag;
            }
        }
        return null;
    }

    public void deleteDeviceFromTags(int deviceId) {
        for (Tag tag : this.tags) {
            tag.deleteDevice(deviceId);
        }
    }

    public void cleanPinStorage(DeviceDao deviceDao, DashBoard dash, Widget widget, boolean removeTemplates) {
        cleanPinStorageInternalWithoutUpdatedAt(deviceDao, dash, widget, true, removeTemplates);
        dash.updatedAt = System.currentTimeMillis();
    }

    public void cleanPinStorageForTileTemplate(DeviceDao deviceDao, DashBoard dash, TileTemplate tileTemplate,
                                               boolean removeProperties) {
        for (int deviceId : tileTemplate.deviceIds) {
            for (Widget widget : tileTemplate.widgets) {
                if (widget instanceof OnePinWidget) {
                    OnePinWidget onePinWidget = (OnePinWidget) widget;
                    cleanPinStorage(deviceDao, dash, onePinWidget, deviceId, removeProperties);
                } else if (widget instanceof MultiPinWidget) {
                    MultiPinWidget multiPinWidget = (MultiPinWidget) widget;
                    cleanPinStorage(deviceDao, dash, multiPinWidget, deviceId, removeProperties);
                }
            }
        }
    }

    private void cleanPinStorage(DeviceDao deviceDao, DashBoard dash,
                                 MultiPinWidget multiPinWidget, int targetId, boolean removeProperties) {
        if (multiPinWidget.dataStreams != null) {
            for (DataStream dataStream : multiPinWidget.dataStreams) {
                if (dataStream != null && dataStream.isValid()) {
                    removePinStorageValue(deviceDao, dash, targetId == -1 ? multiPinWidget.deviceId : targetId,
                            dataStream.pinType, dataStream.pin, removeProperties);
                }
            }
        }
    }

    private void cleanPinStorage(DeviceDao deviceDao, DashBoard dash, OnePinWidget onePinWidget,
                                 int targetId, boolean removeProperties) {
        if (onePinWidget.isValid()) {
            removePinStorageValue(deviceDao, dash, targetId == -1 ? onePinWidget.deviceId : targetId,
                    onePinWidget.pinType, onePinWidget.pin, removeProperties);
        }
    }

    private void removePinStorageValue(DeviceDao deviceDao, DashBoard dash, int targetId,
                                       PinType pinType, short pin, boolean removeProperties) {
        Target target;
        if (targetId < Tag.START_TAG_ID) {
            target = deviceDao.getById(targetId);
        } else if (targetId < DeviceSelector.DEVICE_SELECTOR_STARTING_ID) {
            target = getTagById(targetId);
        } else {
            //means widget assigned to device selector widget.
            target = dash.getDeviceSelector(targetId);
        }
        if (target != null) {
            for (int deviceId : target.getAssignedDeviceIds()) {
                pinsStorage.remove(new DashPinStorageKey(dash.id, deviceId, pinType, pin));
                if (removeProperties) {
                    for (WidgetProperty widgetProperty : WidgetProperty.values()) {
                        pinsStorage.remove(
                                new DashPinPropertyStorageKey(dash.id, deviceId, pinType, pin, widgetProperty));
                    }
                }
            }
        }
    }

    public void sendAppSyncs(DashBoard dash, Channel appChannel, int targetId, boolean useNewFormat) {
        for (Widget widget : dash.widgets) {
            if (widget instanceof MobileSyncWidget && appChannel.isWritable()) {
                ((MobileSyncWidget) widget).sendAppSync(appChannel, dash.id, targetId, useNewFormat);
            }
        }

        sendPinStorageSyncs(dash, appChannel, targetId, useNewFormat);
    }

    private void sendPinStorageSyncs(DashBoard dash, Channel appChannel, int targetId, boolean useNewFormat) {
        for (Map.Entry<DashPinStorageKey, PinStorageValue> entry : pinsStorage.entrySet()) {
            DashPinStorageKey key = entry.getKey();
            if ((targetId == ANY_TARGET || targetId == key.deviceId)
                    && dash.id == key.dashId
                    && appChannel.isWritable()) {
                PinStorageValue pinStorageValue = entry.getValue();
                pinStorageValue.sendAppSync(appChannel, dash.id, key, useNewFormat);
            }
        }
    }

    public void cleanPinStorage(DeviceDao deviceDao, DashBoard dash, boolean removeProperties,
                                boolean eraseTemplates) {
        for (Widget widget : dash.widgets) {
            cleanPinStorageInternalWithoutUpdatedAt(deviceDao, dash, widget, removeProperties, eraseTemplates);
        }
        dash.updatedAt = System.currentTimeMillis();
    }

    private void cleanPinStorageInternalWithoutUpdatedAt(DeviceDao deviceDao, DashBoard dash, Widget widget,
                                                         boolean removeProperties, boolean eraseTemplates) {
        if (widget instanceof OnePinWidget) {
            OnePinWidget onePinWidget = (OnePinWidget) widget;
            cleanPinStorage(deviceDao, dash, onePinWidget, -1, removeProperties);
        } else if (widget instanceof MultiPinWidget) {
            MultiPinWidget multiPinWidget = (MultiPinWidget) widget;
            cleanPinStorage(deviceDao, dash, multiPinWidget, -1, removeProperties);
        } else if (widget instanceof DeviceTiles) {
            DeviceTiles deviceTiles = (DeviceTiles) widget;
            cleanPinStorage(dash.id, deviceTiles, removeProperties);
            if (eraseTemplates) {
                cleanPinStorageForTemplate(deviceDao, dash, deviceTiles, removeProperties);
            }
        }
    }

    private void cleanPinStorage(int dashId, DeviceTiles deviceTiles, boolean removeProperties) {
        for (Tile tile : deviceTiles.tiles) {
            if (tile != null && tile.isValidDataStream()) {
                DataStream dataStream = tile.dataStream;
                pinsStorage.remove(new DashPinStorageKey(dashId, tile.deviceId, dataStream.pinType, dataStream.pin));
                if (removeProperties) {
                    for (WidgetProperty widgetProperty : WidgetProperty.values()) {
                        pinsStorage.remove(new DashPinPropertyStorageKey(dashId, tile.deviceId,
                                dataStream.pinType, dataStream.pin, widgetProperty));
                    }
                }
            }
        }
    }

    private void cleanPinStorageForTemplate(DeviceDao deviceDao, DashBoard dash,
                                            DeviceTiles deviceTiles, boolean removeProperties) {
        for (TileTemplate tileTemplate : deviceTiles.templates) {
            cleanPinStorageForTileTemplate(deviceDao, dash, tileTemplate, removeProperties);
        }
    }

    public void cleanPinStorageForDevice(int deviceId) {
        pinsStorage.entrySet().removeIf(entry -> entry.getKey().deviceId == deviceId);
    }

    public void update(DashBoard dash, int deviceId, short pin, PinType pinType, String value, long now) {
        if (!dash.updateWidgets(deviceId, pin, pinType, value)) {
            //special case. #237 if no widget - storing without widget.
            putPinStorageValue(dash, deviceId, pinType, pin, value);
        }

        dash.updatedAt = now;
    }

    public void putPinPropertyStorageValue(DashBoard dash, int deviceId, PinType type, short pin,
                                           WidgetProperty property, String value) {
        putPinStorageValue(dash, new DashPinPropertyStorageKey(dash.id, deviceId, type, pin, property), value);
    }

    private void putPinStorageValue(DashBoard dash, int deviceId, PinType type, short pin, String value) {
        putPinStorageValue(dash, new DashPinStorageKey(dash.id, deviceId, type, pin), value);
    }

    private void putPinStorageValue(DashBoard dash, DashPinStorageKey key, String value) {
        PinStorageValue pinStorageValue = pinsStorage.get(key);
        if (pinStorageValue == null) {
            pinStorageValue = dash.initStorageValueForStorageKey(key);
            pinsStorage.put(key, pinStorageValue);
        }
        pinStorageValue.update(value);
    }

    public int getDashIndexOrThrow(int dashId) {
        for (int i = 0; i < dashBoards.length; i++) {
            if (dashBoards[i].id == dashId) {
                return i;
            }
        }
        throw new IllegalCommandException("Dashboard with passed id not found.");
    }

    public DashBoard getDashByIdOrThrow(int id) {
        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.id == id) {
                return dashBoard;
            }
        }
        throw new IllegalCommandException("Dashboard with passed id not found.");
    }

    public DashBoard getDashById(int id) {
        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.id == id) {
                return dashBoard;
            }
        }
        return null;
    }

    public int getAppIndexById(String id) {
        for (int i = 0; i < apps.length; i++) {
            if (apps[i].id.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public App getAppById(String id) {
        for (App app : apps) {
            if (app.id.equals(id)) {
                return app;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Profile that = (Profile) o;

        return Arrays.equals(dashBoards, that.dashBoards);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dashBoards);
    }
}
