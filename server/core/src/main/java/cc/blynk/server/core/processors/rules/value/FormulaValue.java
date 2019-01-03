package cc.blynk.server.core.processors.rules.value;

import cc.blynk.server.core.processors.rules.value.params.FormulaParamBase;
import cc.blynk.server.exp4j.Expression;
import cc.blynk.server.exp4j.ExpressionBuilder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class FormulaValue extends ValueBase {

    public final String formula;

    public final Map<String, FormulaParamBase> formulaParams;

    public final transient Expression expression;

    @JsonCreator
    public FormulaValue(@JsonProperty("formula") String formula,
                        @JsonProperty("formulaParams") Map<String, FormulaParamBase> formulaParams) {
        this.formula = formula;
        this.formulaParams = formulaParams;
        this.expression = new ExpressionBuilder(formula)
                .variables(formulaParams.keySet())
                .build();
        this.expression.validateExpression();
    }

    @Override
    public boolean isValid() {
        return formula != null && !formula.isEmpty() && formulaParams != null;
    }


}
