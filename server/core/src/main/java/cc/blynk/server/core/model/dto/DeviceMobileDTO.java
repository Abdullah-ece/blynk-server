package cc.blynk.server.core.model.dto;

import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Device field + some additional just for UI.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.07.17.
 */
public class DeviceMobileDTO {

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

    public final long lastReportedAt;

    public final String iconName;
    public final HardwareInfo hardwareInfo;
    public MetaField[] metaFields;
    public String productName;
    public String productLogoUrl;

    @JsonCreator
    public DeviceMobileDTO(@JsonProperty("id") int id,
                           @JsonProperty("productId") int productId,
                           @JsonProperty("name") String name,
                           @JsonProperty("boardType") BoardType boardType,
                           @JsonProperty("token") String token,
                           @JsonProperty("connectionType") ConnectionType connectionType,
                           @JsonProperty("status") Status status,
                           @JsonProperty("activatedAt") long activatedAt,
                           @JsonProperty("activatedBy") String activatedBy,
                           @JsonProperty("disconnectTime") long disconnectTime,
                           @JsonProperty("lastReportedAt") long lastReportedAt,
                           @JsonProperty("iconName") String iconName,
                           @JsonProperty("metaFields") MetaField[] metaFields,
                           @JsonProperty("productName") String productName,
                           @JsonProperty("productLogoUrl") String productLogoUrl,
                           @JsonProperty("hardwareInfo") HardwareInfo hardwareInfo) {
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
        this.iconName = iconName;
        this.lastReportedAt = lastReportedAt;
        this.metaFields = metaFields;
        this.productName = productName;
        this.productLogoUrl = productLogoUrl;
        this.hardwareInfo = hardwareInfo;
    }

    public DeviceMobileDTO(Device device, Product product) {
        this.id = device.id;
        this.productId = device.productId;
        this.name = device.name;
        this.boardType = device.boardType;
        this.token = device.token;
        this.connectionType = device.connectionType;
        this.status = device.status;
        this.disconnectTime = device.disconnectTime;
        this.iconName = device.iconName;
        this.metaFields = device.metaFields;
        this.activatedAt = device.activatedAt;
        this.activatedBy = device.activatedBy;
        this.lastReportedAt = device.pinStorage.lastReportedAt;
        this.hardwareInfo = device.hardwareInfo;

        if (product != null) {
            this.productName = product.name;
            this.productLogoUrl = product.logoUrl;
        }
    }

    public DeviceMobileDTO(Device device, Product product, MetaField[] metaFields) {
        this(device, product);
        this.metaFields = metaFields;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
