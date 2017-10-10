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
        Field<Object> field = DSL.field(selectedColumn.name);
        return applyAggregation(field).as(selectedColumn.label);
    }

    private Field<?> applyAggregation(Field<Object> field) {
        switch (this) {
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
