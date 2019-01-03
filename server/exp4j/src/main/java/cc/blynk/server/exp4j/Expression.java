package cc.blynk.server.exp4j;

import cc.blynk.server.exp4j.exceptions.ParseExpressionException;
import cc.blynk.server.exp4j.exceptions.VariableNotSetException;
import cc.blynk.server.exp4j.operator.Operator;
import cc.blynk.server.exp4j.tokenizer.FunctionToken;
import cc.blynk.server.exp4j.tokenizer.OperatorToken;
import cc.blynk.server.exp4j.tokenizer.Token;
import cc.blynk.server.exp4j.tokenizer.VariableToken;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Expression {

    private final Token[] tokens;

    private final Map<String, Double> variables = new HashMap<>();

    private final Set<String> userFunctionNames;

    /**
     * Creates a new expression that is a copy of the existing one.
     *
     * @param existing the expression to copy
     */
    public Expression(final Expression existing) {
        this.tokens = Arrays.copyOf(existing.tokens, existing.tokens.length);
        this.variables.putAll(existing.variables);
        this.userFunctionNames = new HashSet<>(existing.userFunctionNames);
    }

    Expression(final Token[] tokens) {
        this.tokens = tokens;
        this.userFunctionNames = Collections.emptySet();
    }

    Expression(final Token[] tokens, Set<String> userFunctionNames, Map<String, Double> consts) {
        this.tokens = tokens;
        this.userFunctionNames = userFunctionNames;
        this.variables.putAll(consts);
    }

    public Expression setVariable(final String name, final double value) {
        this.checkVariableName(name);
        this.variables.put(name, value);
        return this;
    }

    private void checkVariableName(String name) {
        if (this.userFunctionNames.contains(name)) {
            throw new IllegalArgumentException("The variable name '"
                    + name + "' is invalid. Since there exists a function with the same name");
        }
    }

    public Expression setVariables(Map<String, Double> variables) {
        for (Map.Entry<String, Double> v : variables.entrySet()) {
            this.setVariable(v.getKey(), v.getValue());
        }
        return this;
    }

    public Set<String> getVariableNames() {
        Set<String> variables = new HashSet<>();
        for (Token token : tokens) {
            if (token.getType() == Token.TOKEN_VARIABLE) {
                variables.add(((VariableToken) token).getName());
            }
        }
        return variables;
    }

    public void validateVariables() {
        for (Token token : this.tokens) {
            if (token.getType() == Token.TOKEN_VARIABLE) {
                String variableName = ((VariableToken) token).getName();
                if (!variables.containsKey(variableName)) {
                    throw new VariableNotSetException(variableName);
                }
            }
        }
    }

    public void validateExpression() {
        /* Check if the number of operands, functions and operators match.
           The idea is to increment a counter for operands and decrease it for operators.
           When a function occurs the number of available arguments has to be greater
           than or equals to the function's expected number of arguments.
           The count has to be larger than 1 at all times and exactly 1 after all tokens
           have been processed */
        int count = 0;
        for (Token tok : this.tokens) {
            switch (tok.getType()) {
                case Token.TOKEN_NUMBER:
                case Token.TOKEN_VARIABLE:
                    count++;
                    break;
                case Token.TOKEN_FUNCTION:
                    final FunctionToken funcToken = ((FunctionToken) tok);
                    final int argsNum = ((FunctionToken) tok).getDynamicNumberOfArguments();
                    funcToken.getFunction().validateArguments(argsNum);
                    if (argsNum > 1) {
                        count -= argsNum - 1;
                    } else if (argsNum == 0) {
                        // see https://github.com/fasseg/exp4j/issues/59
                        count++;
                    }
                    break;
                case Token.TOKEN_OPERATOR:
                    Operator op = ((OperatorToken) tok).getOperator();
                    if (op.getNumOperands() == 2) {
                        count--;
                    }
                    break;
            }
            if (count < 1) {
                throw new ParseExpressionException("Too many operators");
            }
        }
        if (count > 1) {
            throw new ParseExpressionException("Too many operands");
        }
    }

    public Expression validate() {
        validateVariables();
        validateExpression();
        return this;
    }

    public Future<Double> evaluateAsync(ExecutorService executor) {
        return executor.submit(this::evaluate);
    }

    public double evaluate() {
        final Deque<Double> output = new ArrayDeque<>();
        for (Token token : tokens) {
            token.process(output, this.variables);
        }
        if (output.size() > 1) {
            throw new IllegalArgumentException("Invalid number of items on the output queue. "
                    + "Might be caused by an invalid number of arguments for a function.");
        }
        return output.pop();
    }

}
