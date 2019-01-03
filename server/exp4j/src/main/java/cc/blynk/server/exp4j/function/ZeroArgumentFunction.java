package cc.blynk.server.exp4j.function;

/**
 * A class representing a Function which can be used in an expression
 */
public abstract class ZeroArgumentFunction extends Function {

    public ZeroArgumentFunction(String name) {
        super(name, 0);
    }

    public abstract double apply();

}
