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

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_DATA_STREAMS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_EVENTS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_META_FIELDS;

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
        this.metaFields = metaFields == null ? EMPTY_META_FIELDS : metaFields;
        this.dataStreams = dataStreams == null ? EMPTY_DATA_STREAMS : dataStreams;
        this.events = events == null ? EMPTY_EVENTS : events;
        this.webDashboard = webDashboard == null ? new WebDashboard() : webDashboard;
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

    public static ProductDTO[] toDTO(Product[] products) {
        ProductDTO[] productDTOS = new ProductDTO[products.length];
        for (int i = 0; i < products.length; i++) {
            productDTOS[i] = new ProductDTO(products[i]);
        }
        return productDTOS;
    }

    public Product toProduct() {
        Product product = new Product();
        product.id = id;
        product.parentId = parentId;
        product.name = name;
        product.boardType = boardType;
        product.connectionType = connectionType;
        product.description = description;
        product.logoUrl = logoUrl;
        product.lastModifiedTs = lastModifiedTs;
        product.createdAt = createdAt;
        product.metaFields = metaFields;
        product.dataStreams = dataStreams;
        product.events = events;
        product.webDashboard = webDashboard;
        product.otaProgress = otaProgress;
        product.version = version;
        return product;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
