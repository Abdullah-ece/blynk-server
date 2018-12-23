package cc.blynk.server.core.model.widgets.others.eventor.model.condition;

import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.BetweenCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.EqualCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.GreaterThanCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.GreaterThanOrEqualCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.LessThanCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.LessThanOrEqualCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.NotBetweenCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.NotEqualCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.UpdatedCondition;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.string.StringEqual;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.string.StringNotEqual;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.08.16.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GreaterThanCondition.class, name = "GT"),
        @JsonSubTypes.Type(value = GreaterThanOrEqualCondition.class, name = "GTE"),
        @JsonSubTypes.Type(value = LessThanCondition.class, name = "LT"),
        @JsonSubTypes.Type(value = LessThanOrEqualCondition.class, name = "LTE"),
        @JsonSubTypes.Type(value = EqualCondition.class, name = "EQ"),
        @JsonSubTypes.Type(value = NotEqualCondition.class, name = "NEQ"),
        @JsonSubTypes.Type(value = BetweenCondition.class, name = "BETWEEN"),
        @JsonSubTypes.Type(value = NotBetweenCondition.class, name = "NOT_BETWEEN"),
        @JsonSubTypes.Type(value = UpdatedCondition.class, name = "UPDATED"),

        @JsonSubTypes.Type(value = ValueChanged.class, name = "CHANGED"),
        @JsonSubTypes.Type(value = StringEqual.class, name = "STR_EQUAL"),
        @JsonSubTypes.Type(value = StringNotEqual.class, name = "STR_NOT_EQUAL")
})
public abstract class BaseCondition {

    public abstract boolean matches(String inString, double in);

}
