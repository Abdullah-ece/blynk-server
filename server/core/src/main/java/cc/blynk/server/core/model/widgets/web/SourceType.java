package cc.blynk.server.core.model.widgets.web;

import org.jooq.Field;
import org.jooq.impl.DSL;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 30.08.17.
 */
public enum SourceType {

    RAW_DATA, SUM, AVG, MED, MIN, MAX, COUNT, CUMULATIVE_COUNT;

    public Field<?> apply(SelectedColumn selectedColumn) {
        Field<?> field = DSL.field(selectedColumn.name);
        field = applyAggregation(field);
        if (selectedColumn.type == FieldType.COLUMN) {
            return field.as(selectedColumn.label);
        } else {
            return field;
        }
    }

    private Field<?> applyAggregation(Field<?> field) {
        switch (this) {
            case COUNT:
                return field.count();
            case SUM :
                return field.sum();
            case AVG :
                return field.avg();
            case MED :
                return field.median();
            case MIN :
                return field.min();
            case MAX :
                return field.max();
            default :
                throw new RuntimeException("Not yet supported...");
        }
    }

}
