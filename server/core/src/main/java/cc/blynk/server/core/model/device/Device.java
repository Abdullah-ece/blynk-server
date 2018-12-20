package cc.blynk.server.core.model.device;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.serialization.View;
import cc.blynk.server.core.model.storage.key.DevicePropertyStorageKey;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.DeviceNameMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceOwnerMetaField;
import cc.blynk.server.core.model.widgets.Target;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonView;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.List;

import static cc.blynk.server.core.model.device.HardwareInfo.DEFAULT_HARDWARE_BUFFER_SIZE;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_META_FIELDS;
import static cc.blynk.utils.ArrayUtil.arrayToList;
import static cc.blynk.utils.ArrayUtil.concat;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.16.
 */
public class Device implements Target {

    public int id;

    public volatile int productId = -1;

    public volatile String name;

    public volatile BoardType boardType = BoardType.Generic_Board;

    @JsonView(View.Private.class)
    public volatile String token;

    public volatile ConnectionType connectionType;

    @JsonView(View.Private.class)
    public volatile Status status = Status.OFFLINE;

    public volatile long activatedAt;

    public volatile String activatedBy;

    @JsonView(View.Private.class)
    public volatile long disconnectTime;

    @JsonView(View.Private.class)
    public volatile long connectTime;

    @JsonView(View.Private.class)
    public volatile long firstConnectTime;

    @JsonView(View.Private.class)
    public volatile String lastLoggedIP;

    @JsonView(View.Private.class)
    public volatile HardwareInfo hardwareInfo;

    @JsonView(View.Private.class)
    public volatile DeviceOtaInfo deviceOtaInfo;

    public volatile long metadataUpdatedAt;

    public volatile String metadataUpdatedBy;

    public volatile long updatedAt;

    public volatile MetaField[] metaFields = EMPTY_META_FIELDS;

    public volatile String iconName;

    public volatile boolean isUserIcon;

    @JsonView(View.Web.class)
    public volatile WebDashboard webDashboard;

    @JsonView(View.Private.class)
    public final PinStorage pinStorage = new PinStorage();

    public boolean isNotValid() {
        return name == null || name.isEmpty() || name.trim().isEmpty() || name.length() > 50;
    }

    public Device() {
        this.webDashboard = new WebDashboard();
    }

    //todo remove?
    public Device(String name, BoardType boardType, String token, int productId, ConnectionType connectionType) {
        this();
        this.name = name;
        this.boardType = boardType;
        this.token = token;
        this.productId = productId;
        this.connectionType = connectionType;
    }

    //todo remove?
    public Device(int id, String name, BoardType boardType) {
        this();
        this.id = id;
        this.name = name;
        this.boardType = boardType;
    }

    public MetaField findMetaFieldById(int id) {
        for (MetaField metaField : metaFields) {
            if (metaField.id == id) {
                return metaField;
            }
        }
        return null;
    }

    public MetaField findMetaFieldByIdOrThrow(int id) {
        MetaField metaField = findMetaFieldById(id);
        if (metaField == null) {
            throw new IllegalCommandException("Metafield with passed id not found.");
        }
        return metaField;
    }

    public int findMetaFieldIndex(int id) {
        return findMetaFieldIndex(this.metaFields, id);
    }

    private static int findMetaFieldIndex(MetaField[] metaFields, int id) {
        for (int i = 0; i < metaFields.length; i++) {
            if (metaFields[i].id == id) {
                return i;
            }
        }
        return -1;
    }

    private static int findMetaFieldIndexOrThrow(MetaField[] metaFields, int id) {
        int index = findMetaFieldIndex(metaFields, id);
        if (index == -1) {
            throw new IllegalCommandException("Metafield with passed id not found.");
        }
        return index;
    }

    public void updateMetaFields(MetaField[] updatedMetaFields) {
        MetaField[] metaFieldsCopy = Arrays.copyOf(metaFields, metaFields.length);

        for (MetaField updatedMetaField : updatedMetaFields) {
            for (int i = 0; i < metaFieldsCopy.length; i++) {
                MetaField existingMetaField = metaFieldsCopy[i];
                if (existingMetaField.id == updatedMetaField.id) {
                    metaFieldsCopy[i] = existingMetaField.copySpecificFieldsOnly(updatedMetaField);
                    break;
                }
            }
        }

        this.metaFields = metaFieldsCopy;
    }

    public void addMetaFields(MetaField[] metaFields) {
        this.metaFields = concat(this.metaFields, metaFields);
    }

    public void deleteMetaFields(MetaField[] metaFields) {
        List<MetaField> updatedSet = arrayToList(this.metaFields);
        updatedSet.removeAll(arrayToList(metaFields));
        this.metaFields = updatedSet.toArray(new MetaField[0]);
    }

    public void updateMetafields(MetaField[] updatedMetafields) {
        //copy is necesary here, we don't know here this reference comes from
        MetaField[] localCopy = Arrays.copyOf(this.metaFields, this.metaFields.length);
        for (MetaField updated : updatedMetafields) {
            int i = findMetaFieldIndexOrThrow(localCopy, updated.id);
            updateNameForDeviceNameMeta(updated);
            localCopy[i] = updated;
        }

        long now = System.currentTimeMillis();
        this.metaFields = localCopy;
        this.metadataUpdatedAt = now;
        this.updatedAt = now;
    }

    public void updateNameFromMetafields() {
        for (MetaField metaField : metaFields) {
            if (updateNameForDeviceNameMeta(metaField)) {
                break;
            }
        }
    }

    private boolean updateNameForDeviceNameMeta(MetaField updated) {
        if (updated instanceof DeviceNameMetaField) {
            this.name = ((DeviceNameMetaField) updated).value;
            return true;
        }
        return false;
    }

    public void updateDeviceNameMetaFieldFromName() {
        MetaField[] localCopy = this.metaFields;
        for (int i = 0; i < localCopy.length; i++) {
            MetaField metaField = localCopy[i];
            if (metaField instanceof DeviceNameMetaField) {
                localCopy[i] = new DeviceNameMetaField(
                        metaField.id, metaField.name,
                        metaField.roleIds,
                        metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                        metaField.icon, this.name);
                this.metaFields = localCopy;
                return;
            }
        }
    }

    public String getTemplateId() {
        if (hardwareInfo != null) {
            return hardwareInfo.templateId;
        }
        return null;
    }

    @Override
    public int[] getDeviceIds() {
        return new int[] {id};
    }

    @Override
    public boolean isSelected(int deviceId) {
        return id == deviceId;
    }

    @Override
    public int[] getAssignedDeviceIds() {
        return new int[] {id};
    }

    @Override
    public int getDeviceId() {
        return id;
    }

    public void updateFromMobile(Device newDevice) {
        this.name = newDevice.name;
        this.boardType = newDevice.boardType;
        this.connectionType = newDevice.connectionType;
        this.iconName = newDevice.iconName;
        this.isUserIcon = newDevice.isUserIcon;
        //that's fine. leave this fields as it is. It cannot be update from app client.
        //this.hardwareInfo = newDevice.hardwareInfo;
        //this.deviceOtaInfo = newDevice.deviceOtaInfo;
        updateDeviceNameMetaFieldFromName();
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateFromWeb(Device newDevice) {
        this.productId = newDevice.productId;
        this.metaFields = newDevice.metaFields;
        this.webDashboard = newDevice.webDashboard;
        updateFromMobile(newDevice);
    }

    public void disconnected() {
        this.status = Status.OFFLINE;
        this.disconnectTime = System.currentTimeMillis();
    }

    public void connected() {
        this.status = Status.ONLINE;
        this.connectTime = System.currentTimeMillis();
    }

    public void erase() {
        this.token = null;
        this.disconnectTime = 0;
        this.connectTime = 0;
        this.firstConnectTime = 0;
        this.lastLoggedIP = null;
        this.status = Status.OFFLINE;
        this.hardwareInfo = null;
        this.deviceOtaInfo = null;
        this.webDashboard = new WebDashboard();
        this.metadataUpdatedAt = 0;
        this.metadataUpdatedBy = null;
        this.updatedAt = 0;
        this.deviceOtaInfo = null;
        this.pinStorage.erase();
    }

    public String getNameOrDefault() {
        return name == null ? "New Device" : name;
    }

    public void clearDeviceOtaInfo() {
        setDeviceOtaInfo(null);
    }

    public void setDeviceOtaInfo(DeviceOtaInfo deviceOtaInfo) {
        this.deviceOtaInfo = deviceOtaInfo;
        this.updatedAt = System.currentTimeMillis();
    }

    public void requestSent() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo = new DeviceOtaInfo(prev.otaStartedBy, prev.otaStartedAt,
                now, -1L, -1L, -1L,
                prev.pathToFirmware, prev.buildDate,
                OTAStatus.REQUEST_SENT, prev.attempts, prev.attemptsLimit, prev.isSecure);
        this.updatedAt = now;
    }

    public void success() {
        long now = System.currentTimeMillis();
        this.deviceOtaInfo = new DeviceOtaInfo(this.deviceOtaInfo, now, OTAStatus.SUCCESS);
        this.updatedAt = now;
    }

    public void firmwareRequested() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo =  new DeviceOtaInfo(prev, now, -1L, -1L, OTAStatus.FIRMWARE_REQUESTED, prev.attempts + 1);
        this.updatedAt = now;
    }

    public void firmwareUploaded() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo =  new DeviceOtaInfo(prev, now, -1L, OTAStatus.FIRMWARE_UPLOADED);
        this.updatedAt = now;
    }

    public void firmwareUploadFailure() {
        long now = System.currentTimeMillis();
        this.deviceOtaInfo = new DeviceOtaInfo(this.deviceOtaInfo, now, OTAStatus.FAILURE);
        this.updatedAt = now;
    }

    public void firmwareDownloadLimitReached() {
        long now = System.currentTimeMillis();
        this.deviceOtaInfo = new DeviceOtaInfo(this.deviceOtaInfo, now, OTAStatus.DOWNLOAD_LIMIT_REACHED);
        this.updatedAt = now;
    }

    public boolean isAttemptsLimitReached() {
        return deviceOtaInfo != null && deviceOtaInfo.isLimitReached();
    }

    public boolean fitsBufferSize(int bodySize) {
        if (hardwareInfo == null) {
            return bodySize <= DEFAULT_HARDWARE_BUFFER_SIZE;
        }
        return bodySize + 5 <= hardwareInfo.buffIn;
    }

    public boolean hasOwner(User user) {
        return hasOwner(user.email);
    }

    public boolean hasOwner(String ownerEmail) {
        int index = getOwnerMetaFieldIndex(ownerEmail);
        return index != -1;
    }

    private int getOwnerMetaFieldIndex(String ownerEmail) {
        MetaField[] localMetaFields = this.metaFields;
        for (int i = 0; i < localMetaFields.length; i++) {
            MetaField metaField = localMetaFields[i];
            if (metaField instanceof DeviceOwnerMetaField) {
                DeviceOwnerMetaField deviceOwnerMetaField = (DeviceOwnerMetaField) metaField;
                //todo lowercase device owner?
                if (ownerEmail.equalsIgnoreCase(deviceOwnerMetaField.value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean reassignOwner(String oldOwner, String newOwner) {
        int index = getOwnerMetaFieldIndex(oldOwner);
        if (index != -1) {
            DeviceOwnerMetaField prevDeviceOwner = (DeviceOwnerMetaField) this.metaFields[index];
            MetaField deviceOwnerMetaField = prevDeviceOwner.copy(newOwner);
            this.metaFields = ArrayUtil.copyAndReplace(this.metaFields, deviceOwnerMetaField, index);
            this.updatedAt = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void removePinValue(PinType pinType, short pin, boolean removeProperties) {
        pinStorage.removePinValue(pinType, pin, removeProperties);
    }

    //property users always virtual pins
    public void updateValue(short pin, WidgetProperty widgetProperty, String value) {
        pinStorage.updateValue(new DevicePropertyStorageKey(PinType.VIRTUAL, pin, widgetProperty), value);
    }

    public void updateValue(short pin, PinType pinType, String value) {
        pinStorage.updateValue(new DeviceStorageKey(pin, pinType), value);
    }

    public void updateValue(DeviceStorageKey key, String value) {
        pinStorage.updateValue(key, value, System.currentTimeMillis());
    }

    public void updateValue(DataStream dataStream, String value, long now) {
        pinStorage.updateValue(new DeviceStorageKey(dataStream.pin, dataStream.pinType), value, now);
    }

    public void updateValue(short pin, PinType pinType, String value, long now) {
        pinStorage.updateValue(pin, pinType, value, now);
    }

    public void updateValue(DeviceStorageKey key, String value, long now) {
        pinStorage.updateValue(key, value, now);
    }

    public void sendPinStorageSyncs(Channel appChannel) {
        pinStorage.sendPinStorageSyncs(appChannel, this.id);
    }

    public PinStorageValue getValue(short pin, PinType pinType) {
        return pinStorage.get(pin, pinType);
    }

    public void setLastReportedAt(long lastReportedAt) {
        pinStorage.setLastReportedAt(lastReportedAt);
    }

    public void fillWebDashboardValues() {
        for (var entry : pinStorage.values.entrySet()) {
            DeviceStorageKey key = entry.getKey();
            PinStorageValue value = entry.getValue();
            webDashboard.updateWidgetsValue(id, key.pin, key.pinType, value.lastValue());
        }
    }

    public boolean isUpdatedSince(long lastStart) {
        return lastStart <= this.updatedAt || lastStart <= this.pinStorage.lastReportedAt;
    }

    public void updateFromTileTemplate(TileTemplate tileTemplate) {
        this.iconName = tileTemplate.iconName;
        if (tileTemplate.boardType != null) {
            this.boardType = tileTemplate.boardType;
        }
    }

    public void setDeviceOwnerInMeta(String email) {
        MetaField[] metaFields = this.metaFields;
        for (int i = 0; i < metaFields.length; i++) {
            MetaField metaField = metaFields[i];
            if (metaField instanceof DeviceOwnerMetaField) {
                DeviceOwnerMetaField deviceOwnerMetaField = (DeviceOwnerMetaField) metaField;
                metaFields[i] = deviceOwnerMetaField.copy(email);
                this.metaFields = metaFields;
                return;
            }
        }
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
