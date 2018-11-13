package cc.blynk.server.core.model.dto;

import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.db.model.LogEventCountKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Device field + some additional just for UI.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.07.17.
 */
public class DeviceDTO {

    public final int id;

    public final int productId;

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

    public MetaField[] metaFields;

    public final WebDashboard webDashboard;

    public String productName;

    public String productLogoUrl;

    public String orgName;

    public Integer criticalSinceLastView;

    public Integer warningSinceLastView;

    public final HardwareInfo hardwareInfo;

    public final DeviceOtaInfo deviceOtaInfo;

    @JsonCreator
    public DeviceDTO(@JsonProperty("id") int id,
                     @JsonProperty("productId") int productId,
                     @JsonProperty("name") String name,
                     @JsonProperty("boardType") BoardType boardType,
                     @JsonProperty("token") String token,
                     @JsonProperty("connectionType") ConnectionType connectionType,
                     @JsonProperty("status") Status status,
                     @JsonProperty("activatedAt") long activatedAt,
                     @JsonProperty("activatedBy") String activatedBy,
                     @JsonProperty("disconnectTime") long disconnectTime,
                     @JsonProperty("lastLoggedIP") String lastLoggedIP,
                     @JsonProperty("lastReportedAt") long lastReportedAt,
                     @JsonProperty("metadataUpdatedAt") long metadataUpdatedAt,
                     @JsonProperty("metadataUpdatedBy") String metadataUpdatedBy,
                     @JsonProperty("iconName") String iconName,
                     @JsonProperty("metaFields") MetaField[] metaFields,
                     @JsonProperty("webDashboard") WebDashboard webDashboard,
                     @JsonProperty("productName") String productName,
                     @JsonProperty("productLogoUrl") String productLogoUrl,
                     @JsonProperty("orgName") String orgName,
                     @JsonProperty("criticalSinceLastView") Integer criticalSinceLastView,
                     @JsonProperty("warningSinceLastView") Integer warningSinceLastView,
                     @JsonProperty("hardwareInfo") HardwareInfo hardwareInfo,
                     @JsonProperty("deviceOtaInfo") DeviceOtaInfo deviceOtaInfo) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.boardType = boardType;
        this.token = token;
        this.connectionType = connectionType;
        this.status = status;
        this.activatedAt = activatedAt;
        this.activatedBy = activatedBy;
        this.disconnectTime = disconnectTime;
        this.lastLoggedIP = lastLoggedIP;
        this.iconName = iconName;
        this.metadataUpdatedAt = metadataUpdatedAt;
        this.metadataUpdatedBy = metadataUpdatedBy;
        this.lastReportedAt = lastReportedAt;
        this.metaFields = metaFields;
        this.webDashboard = webDashboard;
        this.productName = productName;
        this.productLogoUrl = productLogoUrl;
        this.orgName = orgName;
        this.criticalSinceLastView = criticalSinceLastView;
        this.warningSinceLastView = warningSinceLastView;
        this.hardwareInfo = hardwareInfo;
        this.deviceOtaInfo = deviceOtaInfo;
    }

    public DeviceDTO(Device device, Product product) {
        this.id = device.id;
        this.productId = device.productId;
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

        if (product != null) {
            this.productName = product.name;
            this.productLogoUrl = product.logoUrl;
        }
    }

    public DeviceDTO(Device device, Product product, MetaField[] metaFields) {
        this(device, product);
        this.metaFields = metaFields;
    }

    public DeviceDTO(Device device, Product product, Map<LogEventCountKey, Integer> counters) {
        this(device, product);

        this.criticalSinceLastView = counters.get(new LogEventCountKey(device.id, EventType.CRITICAL, false));
        this.warningSinceLastView = counters.get(new LogEventCountKey(device.id, EventType.WARNING, false));
    }

    public DeviceDTO(Device device, Product product, String orgName) {
        this(device, product);
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
