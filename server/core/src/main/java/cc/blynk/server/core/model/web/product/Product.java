package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class Product {

    public int id;

    public volatile String name;

    public volatile String boardType;

    public volatile ConnectionType connectionType;

    public volatile String description;

    public volatile String logoUrl;

    public volatile long updatedAt;

    public long createdAt;

    public volatile MetaField[] metaFields;

    public volatile DataStream[] dataStreams;

    public volatile Event[] events;

    public int deviceCount;

    public volatile int version;

    public Product() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public void update(Product updatedProduct) {
        this.name = updatedProduct.name;
        this.boardType = updatedProduct.boardType;
        this.connectionType = updatedProduct.connectionType;
        this.description = updatedProduct.description;
        this.logoUrl = updatedProduct.logoUrl;
        this.metaFields = updatedProduct.metaFields;
        this.dataStreams = updatedProduct.dataStreams;
        this.events = updatedProduct.events;
        this.updatedAt = System.currentTimeMillis();
        this.version++;
    }

    public Event findEventByCode(int hashcode) {
        for (Event event : events) {
            if (event.isSame(hashcode)) {
                return event;
            }
        }
        return null;
    }

    public MetaField[] copyMetaFields() {
        if (metaFields == null || metaFields.length == 0) {
            return ArrayUtil.EMPTY_META_FIELDS;
        }

        List<MetaField> result = new ArrayList<>(metaFields.length);
        for (MetaField metaField : metaFields) {
            if (!metaField.isDefault()) {
                result.add(metaField.copy());
            }
        }
        return result.toArray(new MetaField[result.size()]);
    }

    public boolean notValid() {
        return name == null || name.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return id == product.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
