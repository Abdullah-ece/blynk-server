package cc.blynk.server.exp4j.function;

import cc.blynk.server.exp4j.exceptions.ParseExpressionException;

/**
 * A class representing a Function which can be used in an expression
 */
public abstract class DynamicArgumentFunction extends Function {

    private final int minArgumentsNumber;
    private final int maxArgumentsNumber;

    public DynamicArgumentFunction(String name, int minArgumentsNumber, int maxArgumentsNumber) {
        super(name, -1);
        if (minArgumentsNumber < 0 || maxArgumentsNumber < minArgumentsNumber) {
            throw new IllegalArgumentException("Maximum arguments number can't exceed minimum arguments number.");
        }
        this.minArgumentsNumber = minArgumentsNumber;
        this.maxArgumentsNumber = maxArgumentsNumber;
    }

    public abstract double apply(double... args);

    public void verify(int stackSize) {
        if (stackSize < minArgumentsNumber || stackSize > maxArgumentsNumber) {
            throw new IllegalArgumentException("Invalid number of arguments available for '" + name + "' function.");
        }
    }

    @Override
    public void validateArguments(int count) {
        if (count < this.minArgumentsNumber) {
            throw new ParseExpressionException("Not enough arguments for '" + this.name + "' function.");
        }
        if (count > this.maxArgumentsNumber) {
            throw new ParseExpressionException("Too many arguments for '" + this.name + "' function.");
        }
    }
}
