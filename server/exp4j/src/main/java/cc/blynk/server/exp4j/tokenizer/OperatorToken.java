package cc.blynk.server.exp4j.tokenizer;

import cc.blynk.server.exp4j.operator.Operator;
import cc.blynk.server.exp4j.tokenizer.variable.DoubleValue;
import cc.blynk.server.exp4j.tokenizer.variable.VariableValue;

import java.util.Deque;
import java.util.Map;

/**
 * Represents an operator used in expressions
 */
public class OperatorToken extends Token {

    private final Operator operator;

    /**
     * Create a new instance
     * @param op the operator
     */
    public OperatorToken(Operator op) {
        super(Token.TOKEN_OPERATOR);
        if (op == null) {
            throw new IllegalArgumentException("Operator is unknown for token.");
        }
        this.operator = op;
    }

    /**
     * Get the operator for that token
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    @Override
    public void process(Deque<VariableValue> output, Map<String, VariableValue> variables) {
        if (output.size() < operator.getNumOperands()) {
            throw new IllegalArgumentException("Invalid number of operands available for '"
                    + operator.getSymbol() + "' operator");
        }
        if (operator.getNumOperands() == 2) {
            /* pop the operands and push the result of the operation */
            VariableValue rightArg = output.pop();
            VariableValue leftArg = output.pop();
            output.push(new DoubleValue(operator.apply(leftArg.doubleValue(), rightArg.doubleValue())));
        } else if (operator.getNumOperands() == 1) {
            /* pop the operand and push the result of the operation */
            VariableValue arg = output.pop();
            output.push(new DoubleValue(operator.apply(arg.doubleValue())));
        }
    }
}
