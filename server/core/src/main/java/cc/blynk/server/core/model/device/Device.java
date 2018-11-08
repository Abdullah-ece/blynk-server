package cc.blynk.server.core.model.device;

import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.serialization.View;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.widgets.Target;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import com.fasterxml.jackson.annotation.JsonView;

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

    @JsonView(View.Private.class)
    public final long createdAt;

    public volatile long activatedAt;

    public volatile String activatedBy;

    @JsonView(View.Private.class)
    public volatile long disconnectTime;

    @JsonView(View.Private.class)
    public volatile long connectTime;

    @JsonView(View.Private.class)
    public volatile long firstConnectTime;

    @JsonView(View.Private.class)
    public volatile long dataReceivedAt;

    @JsonView(View.Private.class)
    public volatile String lastLoggedIP;

    @JsonView(View.Private.class)
    public volatile HardwareInfo hardwareInfo;

    @JsonView(View.Private.class)
    public volatile DeviceOtaInfo deviceOtaInfo;

    public volatile long metadataUpdatedAt;

    public volatile long updatedAt;

    public volatile String metadataUpdatedBy;

    public volatile MetaField[] metaFields = EMPTY_META_FIELDS;

    public volatile String iconName;

    public volatile boolean isUserIcon;

    @JsonView(View.Web.class)
    public volatile WebDashboard webDashboard;

    public boolean isNotValid() {
        return name == null || name.isEmpty() || name.trim().isEmpty() || name.length() > 50;
    }

    public Device() {
        this.createdAt = System.currentTimeMillis();
        this.webDashboard = new WebDashboard();
    }

    public Device(String name, BoardType boardType, String token, int productId, ConnectionType connectionType) {
        this();
        this.name = name;
        this.boardType = boardType;
        this.token = token;
        this.productId = productId;
        this.connectionType = connectionType;
    }

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

    private int findMetaFieldIndexOrThrow(int id) {
        return findMetaFieldIndexOrThrow(this.metaFields, id);
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
        MetaField[] localCopy = Arrays.copyOf(this.metaFields, this.metaFields.length);
        for (MetaField updated : updatedMetafields) {
            int i = findMetaFieldIndexOrThrow(localCopy, updated.id);
            if (i != -1) {
                updateNameForDeviceNameMeta(updated);
                localCopy[i] = updated;
            }
        }

        this.metaFields = localCopy;
        this.metadataUpdatedAt = System.currentTimeMillis();
    }

    public void updateNameFromMetafields() {
        for (MetaField metaField : metaFields) {
            if (updateNameForDeviceNameMeta(metaField)) {
                break;
            }
        }
    }

    private boolean updateNameForDeviceNameMeta(MetaField updated) {
        if (updated instanceof TextMetaField && updated.isDeviceNameMetaField()) {
            this.name = ((TextMetaField) updated).value;
            return true;
        }
        return false;
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
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateFromWeb(Device newDevice) {
        this.productId = newDevice.productId;
        this.metaFields = newDevice.metaFields;
        this.webDashboard = newDevice.webDashboard;
        updateFromMobile(newDevice);
    }

    public void updateWebDashboard(short pin, PinType type, String value, long now) {
        webDashboard.update(id, pin, type, value);
        this.dataReceivedAt = now;
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
        this.dataReceivedAt = 0;
        this.lastLoggedIP = null;
        this.status = Status.OFFLINE;
        this.hardwareInfo = null;
        this.deviceOtaInfo = null;
        this.webDashboard = new WebDashboard();
        this.dataReceivedAt = 0;
        this.metadataUpdatedAt = 0;
        this.metadataUpdatedBy = null;
        this.updatedAt = 0;
        this.deviceOtaInfo = null;
    }

    public String getNameOrDefault() {
        return name == null ? "New Device" : name;
    }

    public void setDeviceOtaInfo(DeviceOtaInfo deviceOtaInfo) {
        this.deviceOtaInfo = deviceOtaInfo;
        this.updatedAt = System.currentTimeMillis();
    }

    public void requestSent() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo =  new DeviceOtaInfo(prev.otaStartedBy, prev.otaStartedAt,
                now, -1L, -1L, -1L,
                prev.pathToFirmware, prev.buildDate,
                OTAStatus.REQUEST_SENT, prev.attempts, prev.attemptsLimit);
        this.updatedAt = now;
    }

    public void success() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo = new DeviceOtaInfo(prev.otaStartedBy, prev.otaStartedAt,
                prev.requestSentAt, prev.firmwareRequestedAt, prev.firmwareUploadedAt, now,
                prev.pathToFirmware, prev.buildDate,
                OTAStatus.SUCCESS, prev.attempts, prev.attemptsLimit);
        this.updatedAt = now;
    }

    public void firmwareRequested() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo =  new DeviceOtaInfo(prev.otaStartedBy, prev.otaStartedAt,
                prev.requestSentAt, now, -1L, -1L,
                prev.pathToFirmware, prev.buildDate,
                OTAStatus.FIRMWARE_REQUESTED, prev.attempts + 1, prev.attemptsLimit);
        this.updatedAt = now;
    }

    public void firmwareUploaded() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo =  new DeviceOtaInfo(prev.otaStartedBy, prev.otaStartedAt,
                prev.requestSentAt, prev.firmwareRequestedAt, now, -1L,
                prev.pathToFirmware, prev.buildDate,
                OTAStatus.FIRMWARE_UPLOADED, prev.attempts, prev.attemptsLimit);
        this.updatedAt = now;
    }

    public void firmwareUploadFailure() {
        DeviceOtaInfo prev = this.deviceOtaInfo;
        long now = System.currentTimeMillis();
        this.deviceOtaInfo = new DeviceOtaInfo(prev.otaStartedBy, prev.otaStartedAt,
                prev.requestSentAt, prev.firmwareRequestedAt, prev.firmwareUploadedAt, -1L,
                prev.pathToFirmware, prev.buildDate,
                OTAStatus.FAILURE, prev.attempts, prev.attemptsLimit);
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

    public boolean hasOwner(String ownerEmail) {
        for (MetaField metaField : metaFields) {
            if (metaField.isDeviceOwnerMetaField()) {
                TextMetaField textMetaField = (TextMetaField) metaField;
                if (ownerEmail.equals(textMetaField.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
