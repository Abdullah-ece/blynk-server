package cc.blynk.server.exp4j.tokenizer;

import java.util.Deque;
import java.util.Map;

/**
 * represents a setVariable used in an expression
 */
public class VariableToken extends Token {

    private final String name;

    /**
     * Create a new instance
     * @param name the name of the setVariable
     */
    public VariableToken(String name) {
        super(TOKEN_VARIABLE);
        this.name = name;
    }

    /**
     * Get the name of the setVariable
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public void process(Deque<Double> output, Map<String, Double> variables) {
        Double value = variables.get(name);
        if (value == null) {
            throw new IllegalArgumentException("No value has been set for the setVariable '" + name + "'.");
        }
        output.push(value);
    }
}
