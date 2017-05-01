package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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

        //controls
        @JsonSubTypes.Type(value = TextMetaField.class, name = "Text"),
        @JsonSubTypes.Type(value = NumberMetaField.class, name = "Number"),
        @JsonSubTypes.Type(value = RangeMetaField.class, name = "Range"),
        @JsonSubTypes.Type(value = CostMetaField.class, name = "Cost"),
        @JsonSubTypes.Type(value = ContactMetaField.class, name = "Contact"),
        @JsonSubTypes.Type(value = MeasurementUnitMetaField.class, name = "Measurement"),
        @JsonSubTypes.Type(value = TimeMetaField.class, name = "Time"),
        @JsonSubTypes.Type(value = CoordinatesMetaField.class, name = "Coordinates"),
        @JsonSubTypes.Type(value = AddressMetaField.class, name = "Address")

})
public abstract class MetaField {

    public String name;

    public Role role;

    public MetaField() {
    }

    public MetaField(String name, Role role) {
        this.name = name;
        this.role = role;
    }
}
