package cc.blynk.server.core.model.profile;

import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.MultiPinWidget;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;

import java.util.Arrays;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class Profile {

    private static final DashBoard[] EMPTY_DASHBOARDS = {};
    private static final App[] EMPTY_APPS = {};

    public volatile DashBoard[] dashBoards = EMPTY_DASHBOARDS;

    public volatile App[] apps = EMPTY_APPS;

    public final ProfileSettings settings = new ProfileSettings();

    public void updateSettings(ProfileSettings updatedSettings) {
        this.settings.update(updatedSettings);
    }

    public void cleanPinStorageForTileTemplate(DeviceDao deviceDao, TileTemplate tileTemplate,
                                               boolean removeProperties) {
        for (int deviceId : tileTemplate.deviceIds) {
            for (Widget widget : tileTemplate.widgets) {
                if (widget instanceof OnePinWidget) {
                    OnePinWidget onePinWidget = (OnePinWidget) widget;
                    cleanPinStorage(deviceDao, onePinWidget, deviceId, removeProperties);
                } else if (widget instanceof MultiPinWidget) {
                    MultiPinWidget multiPinWidget = (MultiPinWidget) widget;
                    cleanPinStorage(deviceDao, multiPinWidget, deviceId, removeProperties);
                }
            }
        }
    }

    private void cleanPinStorage(DeviceDao deviceDao,
                                 MultiPinWidget multiPinWidget, int targetId, boolean removeProperties) {
        if (multiPinWidget.dataStreams != null) {
            for (DataStream dataStream : multiPinWidget.dataStreams) {
                if (dataStream != null && dataStream.isValid()) {
                    removePinStorageValue(deviceDao, targetId == -1 ? multiPinWidget.deviceId : targetId,
                            dataStream.pinType, dataStream.pin, removeProperties);
                }
            }
        }
    }

    private void cleanPinStorage(DeviceDao deviceDao, OnePinWidget onePinWidget,
                                 int targetId, boolean removeProperties) {
        if (onePinWidget.isValid()) {
            removePinStorageValue(deviceDao, targetId == -1 ? onePinWidget.deviceId : targetId,
                    onePinWidget.pinType, onePinWidget.pin, removeProperties);
        }
    }

    private void removePinStorageValue(DeviceDao deviceDao, int targetId,
                                       PinType pinType, short pin, boolean removeProperties) {
        Device device = deviceDao.getById(targetId);
        if (device != null) {
            device.removePinValue(pinType, pin, removeProperties);
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
