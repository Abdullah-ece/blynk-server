package cc.blynk.server.http.web.dto;

import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
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

    public final String boardType;

    public final String token;

    public final ConnectionType connectionType;

    public final Status status;

    public final long createdAt;

    public final long activatedAt;

    public final String activatedBy;

    public final long disconnectTime;

    public final String lastLoggedIP;

    public final long dataReceivedAt;

    public final long metadataUpdatedAt;

    public final String metadataUpdatedBy;

    public final MetaField[] metaFields;

    public final WebDashboard webDashboard;

    public String productName;

    public String productLogoUrl;

    public String orgName;

    public Integer criticalSinceLastView;

    public Integer warningSinceLastView;

    @JsonCreator
    public DeviceDTO(@JsonProperty("id") int id,
                     @JsonProperty("productId") int productId,
                     @JsonProperty("name") String name,
                     @JsonProperty("boardType") String boardType,
                     @JsonProperty("token") String token,
                     @JsonProperty("connectionType") ConnectionType connectionType,
                     @JsonProperty("status") Status status,
                     @JsonProperty("createdAt") long createdAt,
                     @JsonProperty("activatedAt") long activatedAt,
                     @JsonProperty("activatedBy") String activatedBy,
                     @JsonProperty("disconnectTime") long disconnectTime,
                     @JsonProperty("lastLoggedIP") String lastLoggedIP,
                     @JsonProperty("dataReceivedAt") long dataReceivedAt,
                     @JsonProperty("metadataUpdatedAt") long metadataUpdatedAt,
                     @JsonProperty("metadataUpdatedBy") String metadataUpdatedBy,
                     @JsonProperty("metaFields") MetaField[] metaFields,
                     @JsonProperty("webDashboard") WebDashboard webDashboard,
                     @JsonProperty("productName") String productName,
                     @JsonProperty("productLogoUrl") String productLogoUrl,
                     @JsonProperty("orgName") String orgName,
                     @JsonProperty("criticalSinceLastView") Integer criticalSinceLastView,
                     @JsonProperty("warningSinceLastView") Integer warningSinceLastView) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.boardType = boardType;
        this.token = token;
        this.connectionType = connectionType;
        this.status = status;
        this.createdAt = createdAt;
        this.activatedAt = activatedAt;
        this.activatedBy = activatedBy;
        this.disconnectTime = disconnectTime;
        this.lastLoggedIP = lastLoggedIP;
        this.dataReceivedAt = dataReceivedAt;
        this.metadataUpdatedAt = metadataUpdatedAt;
        this.metadataUpdatedBy = metadataUpdatedBy;
        this.metaFields = metaFields;
        this.webDashboard = webDashboard;
        this.productName = productName;
        this.productLogoUrl = productLogoUrl;
        this.orgName = orgName;
        this.criticalSinceLastView = criticalSinceLastView;
        this.warningSinceLastView = warningSinceLastView;
    }

    private DeviceDTO(Device device, Product product) {
        this.id = device.id;
        this.productId = device.productId;
        this.name = device.name;
        this.boardType = device.boardType;
        this.token = device.token;
        this.connectionType = device.connectionType;
        this.status = device.status;
        this.createdAt = device.createdAt;
        this.disconnectTime = device.disconnectTime;
        this.lastLoggedIP = device.lastLoggedIP;
        this.dataReceivedAt = device.dataReceivedAt;
        this.metaFields = device.metaFields;
        this.webDashboard = device.webDashboard;
        this.activatedAt = device.activatedAt;
        this.activatedBy = device.activatedBy;
        this.metadataUpdatedAt = device.metadataUpdatedAt;
        this.metadataUpdatedBy = device.metadataUpdatedBy;

        if (product != null) {
            this.productName = product.name;
            this.productLogoUrl = product.logoUrl;
        }
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
