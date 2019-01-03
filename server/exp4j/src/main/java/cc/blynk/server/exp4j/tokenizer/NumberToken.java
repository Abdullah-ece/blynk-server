package cc.blynk.server.exp4j.tokenizer;

import cc.blynk.server.exp4j.tokenizer.variable.DoubleValue;
import cc.blynk.server.exp4j.tokenizer.variable.VariableValue;

import java.util.Deque;
import java.util.Map;

/**
 * Represents a number in the expression
 */
public final class NumberToken extends Token {

    private final double value;

    /**
     * Create a new instance
     * @param value the value of the number
     */
    public NumberToken(double value) {
        super(TOKEN_NUMBER);
        this.value = value;
    }

    NumberToken(final char[] expression, final int offset, final int len) {
        this(Double.parseDouble(String.valueOf(expression, offset, len)));
    }

    /**
     * Get the value of the number
     * @return the value
     */
    public double getValue() {
        return value;
    }

    @Override
    public void process(Deque<VariableValue> output, Map<String, VariableValue> variables) {
        output.push(new DoubleValue(value));
    }
}
