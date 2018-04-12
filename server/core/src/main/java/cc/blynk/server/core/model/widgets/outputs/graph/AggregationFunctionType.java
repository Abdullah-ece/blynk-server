package cc.blynk.server.core.model.widgets.outputs.graph;

import cc.blynk.server.core.dao.functions.AverageGraphFunction;
import cc.blynk.server.core.dao.functions.GraphFunction;
import cc.blynk.server.core.dao.functions.MaxGraphFunction;
import cc.blynk.server.core.dao.functions.MedianGraphFunction;
import cc.blynk.server.core.dao.functions.MinGraphFunction;
import cc.blynk.server.core.dao.functions.SumGraphFunction;
import cc.blynk.server.core.model.widgets.web.FieldType;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import org.jooq.Field;
import org.jooq.impl.DSL;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 21.07.17.
 */
public enum AggregationFunctionType {

    RAW_DATA,
    MIN,
    MAX,
    AVG,
    SUM,
    MED,
    COUNT,
    CUMULATIVE_COUNT;

    public GraphFunction produce() {
        switch (this) {
            case MIN :
                return new MinGraphFunction();
            case MAX :
                return new MaxGraphFunction();
            case SUM :
                return new SumGraphFunction();
            case MED :
                return new MedianGraphFunction();
            default:
                return new AverageGraphFunction();
        }
    }

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
