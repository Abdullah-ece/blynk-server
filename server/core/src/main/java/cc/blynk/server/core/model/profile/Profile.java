package cc.blynk.server.core.model.profile;

import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.MultiPinWidget;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Target;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.ui.DeviceSelector;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.utils.ArrayUtil;

import java.util.Arrays;

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

    public final ProfileSettings settings = new ProfileSettings();

    public void updateSettings(ProfileSettings updatedSettings) {
        this.settings.update(updatedSettings);
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
                Device device = deviceDao.getById(deviceId);
                device.removePinValue(pinType, pin, removeProperties);
            }
        }
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

    public void deleteDeviceFromTags(int deviceId) {
        for (Tag tag : this.tags) {
            tag.deleteDevice(deviceId);
        }
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
