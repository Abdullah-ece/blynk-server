package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.WebDashboard;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.07.17.
 */
public class DeviceWithOrgIdDTO {

    public final int id;

    public final int productId;

    public final int orgId;

    public final String name;

    public final BoardType boardType;

    public final String token;

    public final ConnectionType connectionType;

    public final Status status;

    public final long activatedAt;

    public final String activatedBy;

    public final long disconnectTime;

    public final String lastLoggedIP;

    public final long metadataUpdatedAt;

    public final String metadataUpdatedBy;

    public final long lastReportedAt;

    public final String iconName;

    public final MetaField[] metaFields;

    public final WebDashboard webDashboard;

    public final HardwareInfo hardwareInfo;

    public final DeviceOtaInfo deviceOtaInfo;

    public DeviceWithOrgIdDTO(Device device, int orgId) {
        this.id = device.id;
        this.productId = device.productId;
        this.orgId = orgId;
        this.name = device.name;
        this.boardType = device.boardType;
        this.token = device.token;
        this.connectionType = device.connectionType;
        this.status = device.status;
        this.disconnectTime = device.disconnectTime;
        this.lastLoggedIP = device.lastLoggedIP;
        this.iconName = device.iconName;
        this.metaFields = device.metaFields;
        this.webDashboard = device.webDashboard;
        this.activatedAt = device.activatedAt;
        this.activatedBy = device.activatedBy;
        this.metadataUpdatedAt = device.metadataUpdatedAt;
        this.metadataUpdatedBy = device.metadataUpdatedBy;
        this.lastReportedAt = device.pinStorage.lastReportedAt;
        this.hardwareInfo = device.hardwareInfo;
        this.deviceOtaInfo = device.deviceOtaInfo;
    }
}
