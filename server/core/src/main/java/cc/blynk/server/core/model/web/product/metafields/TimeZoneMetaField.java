package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.widgets.others.rtc.StringToZoneId;
import cc.blynk.server.core.model.widgets.others.rtc.ZoneIdToString;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.ZoneId;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.09.18.
 */
public class TimeZoneMetaField extends MetaField {

    @JsonSerialize(using = ZoneIdToString.class)
    @JsonDeserialize(using = StringToZoneId.class, as = ZoneId.class)
    public final ZoneId value;

    @JsonCreator
    public TimeZoneMetaField(@JsonProperty("id") int id,
                             @JsonProperty("name") String name,
                             @JsonProperty("roleIds") int[] roleIds,
                             @JsonProperty("includeInProvision") boolean includeInProvision,
                             @JsonProperty("isMandatory") boolean isMandatory,
                             @JsonProperty("isDefault") boolean isDefault,
                             @JsonProperty("icon") String icon,
                             @JsonProperty("value") ZoneId value) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.value = value;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new TimeZoneMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon, value);
    }

    @Override
    public MetaField copy() {
        return new TimeZoneMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, value);
    }

}
