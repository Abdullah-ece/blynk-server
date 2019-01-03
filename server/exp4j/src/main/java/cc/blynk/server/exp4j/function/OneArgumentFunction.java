package cc.blynk.server.exp4j.function;

/**
 * A class representing a Function which can be used in an expression
 */
public abstract class OneArgumentFunction extends Function {

    public OneArgumentFunction(String name) {
        super(name, 1);
    }

    public abstract double apply(double arg);

}
