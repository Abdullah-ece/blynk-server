package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.metafields.AddressMetaField;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.CoordinatesMetaField;
import cc.blynk.server.core.model.web.product.metafields.CostMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
import cc.blynk.server.core.model.web.product.metafields.EmailMetaField;
import cc.blynk.server.core.model.web.product.metafields.ListMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.MultiTextMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField;
import cc.blynk.server.core.model.web.product.metafields.ShiftMetaField;
import cc.blynk.server.core.model.web.product.metafields.SwitchMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.web.product.metafields.TimeMetaField;
import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({

        //WARNING, when adding new type - please update MetadataType class too

        @JsonSubTypes.Type(value = MultiTextMetaField.class, name = "MultiText"),
        @JsonSubTypes.Type(value = TextMetaField.class, name = "Text"),
        @JsonSubTypes.Type(value = EmailMetaField.class, name = "Email"),
        @JsonSubTypes.Type(value = NumberMetaField.class, name = "Number"),
        @JsonSubTypes.Type(value = RangeTimeMetaField.class, name = "Range"),
        @JsonSubTypes.Type(value = ShiftMetaField.class, name = "Shift"),
        @JsonSubTypes.Type(value = SwitchMetaField.class, name = "Switch"),
        @JsonSubTypes.Type(value = CostMetaField.class, name = "Cost"),
        @JsonSubTypes.Type(value = ContactMetaField.class, name = "Contact"),
        @JsonSubTypes.Type(value = MeasurementUnitMetaField.class, name = "Measurement"),
        @JsonSubTypes.Type(value = TimeMetaField.class, name = "Time"),
        @JsonSubTypes.Type(value = CoordinatesMetaField.class, name = "Coordinates"),
        @JsonSubTypes.Type(value = AddressMetaField.class, name = "Address"),
        @JsonSubTypes.Type(value = ListMetaField.class, name = "List"),
        @JsonSubTypes.Type(value = DeviceReferenceMetaField.class, name = "DeviceReference")

})
public abstract class MetaField implements CopyObject<MetaField> {

    public final int id;

    public final String name;

    public final Role role;

    public final boolean isDefault;

    public MetaField(int id, String name, Role role, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.isDefault = isDefault;
    }

    public abstract MetaField copySpecificFieldsOnly(MetaField metaField);

    public Field<?> prepareField(SelectSelectStep<Record> query, Field<Object> field) {
        return field;
    }

    public Field<?> applyMapping(SelectSelectStep<Record> query, Field<Object> field) {
        return field;
    }

    public String getNotificationEmail() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetaField)) {
            return false;
        }

        MetaField metaField = (MetaField) o;

        return id == metaField.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
