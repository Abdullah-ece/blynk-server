package cc.blynk.server.exp4j.tokenizer;

import cc.blynk.server.exp4j.function.DynamicArgumentFunction;
import cc.blynk.server.exp4j.function.Function;
import cc.blynk.server.exp4j.function.OneArgumentFunction;
import cc.blynk.server.exp4j.function.PredefinedArgumentFunction;
import cc.blynk.server.exp4j.function.TwoArgumentFunction;
import cc.blynk.server.exp4j.tokenizer.variable.DoubleValue;
import cc.blynk.server.exp4j.tokenizer.variable.VariableValue;

import java.util.Deque;
import java.util.Map;

public class FunctionToken extends Token {

    private final Function function;
    private int dynamicNumberOfArguments;

    public FunctionToken(Function function) {
        super(TOKEN_FUNCTION);
        this.function = function;
        this.dynamicNumberOfArguments = 1;
    }

    public Function getFunction() {
        return function;
    }

    @Override
    public void process(Deque<VariableValue> output, Map<String, VariableValue> variables) {
        double result = apply(output);
        output.push(new DoubleValue(result));
    }

    private double apply(Deque<VariableValue> output) {
        if (this.function instanceof OneArgumentFunction) {
            OneArgumentFunction oneArgumentFunction = (OneArgumentFunction) this.function;
            int functionArguments = oneArgumentFunction.getNumberOfArguments();
            verify(output.size(), functionArguments);
            return oneArgumentFunction.apply(output.pop().doubleValue());
        } else if (this.function instanceof TwoArgumentFunction) {
            TwoArgumentFunction twoArgumentFunction = (TwoArgumentFunction) this.function;
            int functionArguments = twoArgumentFunction.getNumberOfArguments();
            verify(output.size(), functionArguments);
            double right = output.pop().doubleValue();
            double left = output.pop().doubleValue();
            return twoArgumentFunction.apply(left, right);
        } else if (this.function instanceof PredefinedArgumentFunction) {
            PredefinedArgumentFunction predefinedArgumentFunction = (PredefinedArgumentFunction) this.function;
            return predefinedArgumentFunction.apply(output);
        } else {
            DynamicArgumentFunction dynamicArgumentFunction = (DynamicArgumentFunction) this.function;
            /* collect the arguments from the stack */
            int functionArguments = dynamicNumberOfArguments;
            dynamicArgumentFunction.verify(functionArguments);

            double[] args;
            if (dynamicArgumentFunction.acceptsMultiValues) {
                args = output.pop().doubleValues();
            } else {
                args = new double[functionArguments];
                for (int j = functionArguments - 1; j >= 0; j--) {
                    args[j] = output.pop().doubleValue();
                }
            }

            return dynamicArgumentFunction.apply(args);
        }
    }

    private void verify(int stackSize, int numberOfArguments) {
        if (stackSize < numberOfArguments) {
            throw new IllegalArgumentException("Invalid number of arguments available for '"
                    + function.getName() + "' function");
        }
    }

    public int getDynamicNumberOfArguments() {
        if (function instanceof DynamicArgumentFunction) {
            return dynamicNumberOfArguments;
        }
        return function.getNumberOfArguments();
    }

    public void incrArgumentsCounter() {
        this.dynamicNumberOfArguments++;
    }
}
