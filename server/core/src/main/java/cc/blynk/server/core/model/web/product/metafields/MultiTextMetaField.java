package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.CaseConditionStep;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class MultiTextMetaField extends MetaField {

    public final String[] values;

    @JsonCreator
    public MultiTextMetaField(@JsonProperty("id") int id,
                              @JsonProperty("name") String name,
                              @JsonProperty("roleId") int roleId,
                              @JsonProperty("isDefault") boolean isDefault,
                              @JsonProperty("icon") String icon,
                              @JsonProperty("values") String[] values) {
        super(id, name, roleId, isDefault, icon);
        this.values = values;
    }

    @Override
    public Field<?> applyMapping(SelectSelectStep<Record> query, Field<Object> field) {
        CaseConditionStep<String> caseConditionStep = null;
        for (int i = 0; i < values.length; i++) {
            caseConditionStep = caseConditionStep == null
                    ? DSL.when(field.eq(i), values[i])
                    : caseConditionStep.when(field.eq(i), values[i]);
        }
        return caseConditionStep;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new MultiTextMetaField(id, metaField.name,
                metaField.roleId, metaField.isDefault, metaField.icon, values);
    }

    @Override
    public MetaField copy() {
        return new MultiTextMetaField(id, name, roleId, isDefault, icon, values);
    }

}
