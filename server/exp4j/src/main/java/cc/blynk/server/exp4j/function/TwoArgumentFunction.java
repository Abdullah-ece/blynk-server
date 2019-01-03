package cc.blynk.server.exp4j.function;

/**
 * A class representing a Function which can be used in an expression
 */
public abstract class TwoArgumentFunction extends Function {

    public TwoArgumentFunction(String name) {
        super(name, 2);
    }

    public abstract double apply(double arg1, double arg2);

}
