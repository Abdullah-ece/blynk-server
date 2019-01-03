package cc.blynk.server.exp4j.function;

import cc.blynk.server.exp4j.ArrayStack;

/**
 * A class representing a Function which can be used in an expression
 */
public abstract class PredefinedArgumentFunction extends Function {

    public PredefinedArgumentFunction(String name, int numberOfArguments) {
        super(name, numberOfArguments);
    }

    public abstract double apply(double... args);

    public double apply(ArrayStack output) {
        /* collect the arguments from the stack */
        verify(output.size());
        double[] args = new double[numberOfArguments];
        for (int j = numberOfArguments - 1; j >= 0; j--) {
            args[j] = output.pop();
        }
        return apply(args);
    }

    public void verify(int stackSize) {
        if (stackSize != this.numberOfArguments) {
            throw new IllegalArgumentException("Invalid number of arguments for '" + this.name + "' function");
        }
    }
}
