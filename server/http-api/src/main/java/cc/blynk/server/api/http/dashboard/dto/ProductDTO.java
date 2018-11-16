package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.OtaProgress;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.events.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ProductDTO {

    public final int id;

    public final int parentId;

    public final String name;

    public final String boardType;

    public final ConnectionType connectionType;

    public final String description;

    public final String logoUrl;

    public final long lastModifiedTs;

    public final long createdAt;

    public final MetaField[] metaFields;

    public final DataStream[] dataStreams;

    public final Event[] events;

    public final WebDashboard webDashboard;

    public final OtaProgress otaProgress;

    public final int deviceCount;

    public final int version;

    @JsonCreator
    public ProductDTO(@JsonProperty("id") int id,
                      @JsonProperty("parentId") int parentId,
                      @JsonProperty("name") String name,
                      @JsonProperty("boardType") String boardType,
                      @JsonProperty("connectionType") ConnectionType connectionType,
                      @JsonProperty("description") String description,
                      @JsonProperty("logoUrl") String logoUrl,
                      @JsonProperty("lastModifiedTs") long lastModifiedTs,
                      @JsonProperty("createdAt") long createdAt,
                      @JsonProperty("metaFields") MetaField[] metaFields,
                      @JsonProperty("dataStreams") DataStream[] dataStreams,
                      @JsonProperty("events") Event[] events,
                      @JsonProperty("webDashboard") WebDashboard webDashboard,
                      @JsonProperty("otaProgress") OtaProgress otaProgress,
                      @JsonProperty("deviceCount") int deviceCount,
                      @JsonProperty("version")int version) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.boardType = boardType;
        this.connectionType = connectionType;
        this.description = description;
        this.logoUrl = logoUrl;
        this.lastModifiedTs = lastModifiedTs;
        this.createdAt = createdAt;
        this.metaFields = metaFields;
        this.dataStreams = dataStreams;
        this.events = events;
        this.webDashboard = webDashboard;
        this.otaProgress = otaProgress;
        this.deviceCount = deviceCount;
        this.version = version;
    }

    public ProductDTO(Product product) {
        this(product.id, product.parentId, product.name, product.boardType,
                product.connectionType, product.description, product.logoUrl,
                product.lastModifiedTs, product.createdAt, product.metaFields,
                product.dataStreams, product.events, product.webDashboard,
                product.otaProgress, product.devices.length, product.version);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
