package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.exceptions.WebException;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.web.product.events.OfflineEvent;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.JsonParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cc.blynk.utils.ArrayUtil.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class Product {

    public int id;

    public int parentId;

    public volatile String name;

    public volatile String boardType;

    public volatile ConnectionType connectionType;

    public volatile String description;

    public volatile String logoUrl;

    public volatile long lastModifiedTs;

    public long createdAt;

    public volatile MetaField[] metaFields = EMPTY_META_FIELDS;

    public volatile WebDataStream[] dataStreams = EMPTY_WEB_DATA_STREAMS;

    public volatile Event[] events = EMPTY_EVENTS;

    public int deviceCount;

    public volatile int version;

    public Product() {
        this.createdAt = System.currentTimeMillis();
        this.lastModifiedTs = createdAt;
    }

    public Product(Product product) {
        this();
        this.name = product.name;
        this.boardType = product.boardType;
        this.connectionType = product.connectionType;
        this.description = product.description;
        this.logoUrl = product.logoUrl;
        this.metaFields = product.copyMetaFields();
        this.dataStreams = product.copyDataStreams();
        this.events = product.copyEvents();
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
        this.lastModifiedTs = System.currentTimeMillis();
        this.version++;
    }

    public void checkEvents() {
        Set<Integer> set = new HashSet<>();
        for (Event event : events) {
            String eventCode = event.eventCode;
            set.add(eventCode == null ? 0 : event.eventCode.hashCode());
        }
        if (set.size() != events.length) {
            throw new WebException("Events with this event codes are not allowed.");
        }
    }

    public void addMetafield(MetaField metafield) {
        this.metaFields = ArrayUtil.add(metaFields, metafield, MetaField.class);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public Event findEventByType(EventType eventType) {
        for (Event event : events) {
            if (event.getType() == eventType) {
                return event;
            }
        }
        return null;
    }

    public Event findEventByCode(int hashcode) {
        for (Event event : events) {
            if (event.isSame(hashcode)) {
                return event;
            }
        }
        return null;
    }

    public Event findEventByCodeOrType(int hashcode, EventType eventType) {
        Event event = findEventByCode(hashcode);

        //special case for system events
        if (event == null) {
            event = findEventByType(eventType);
        }

        return event;
    }

    public int getIgnorePeriod() {
        for (Event event : events) {
            if (event instanceof OfflineEvent) {
                return ((OfflineEvent) event).ignorePeriod;
            }
        }
        return 0;
    }

    public WebDataStream[] copyDataStreams() {
        return ArrayUtil.cloneArray(dataStreams, WebDataStream.class);
    }

    //todo a bit ugly. do cleanup
    public MetaField[] copyMetaFields() {
        if (metaFields == null || metaFields.length == 0) {
            return ArrayUtil.EMPTY_META_FIELDS;
        }

        MetaField[] result = new MetaField[metaFields.length];
        int i = 0;
        for (MetaField metaField : metaFields) {
            result[i++] = metaField.copy();
        }
        return result;
    }

    public Event[] copyEvents() {
        if (events == null || events.length == 0) {
            return ArrayUtil.EMPTY_EVENTS;
        }

        Event[] result = new Event[events.length];
        int i = 0;
        for (Event event : events) {
            result[i++] = event.copy();
        }
        return result;
    }

    public MetaField[] copyNonDefaultMetaFields() {
        if (metaFields == null || metaFields.length == 0) {
            return ArrayUtil.EMPTY_META_FIELDS;
        }

        List<MetaField> result = new ArrayList<>(metaFields.length);
        for (MetaField metaField : metaFields) {
            if (!metaField.isDefault) {
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
