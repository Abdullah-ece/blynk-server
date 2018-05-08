package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.exceptions.WebException;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.web.product.events.OfflineEvent;
import cc.blynk.utils.ArrayUtil;

import java.util.HashSet;
import java.util.Set;

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_DATA_STREAMS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_EVENTS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_META_FIELDS;

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

    public volatile DataStream[] dataStreams = EMPTY_DATA_STREAMS;

    public volatile Event[] events = EMPTY_EVENTS;

    public WebDashboard webDashboard = new WebDashboard();

    public volatile OtaProgress otaProgress;

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
        this.metaFields = ArrayUtil.copy(product.metaFields, MetaField.class);
        this.dataStreams = ArrayUtil.copy(product.dataStreams, DataStream.class);
        this.events = ArrayUtil.copy(product.events, Event.class);
        this.webDashboard = product.webDashboard.copy();
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
        this.webDashboard = updatedProduct.webDashboard;
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

    public int getIgnorePeriod() {
        for (Event event : events) {
            if (event instanceof OfflineEvent) {
                return ((OfflineEvent) event).ignorePeriod;
            }
        }
        return 0;
    }

    public boolean notValid() {
        return name == null || name.isEmpty();
    }

    public void setOtaProgress(OtaProgress otaProgress) {
        this.otaProgress = otaProgress;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }

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
