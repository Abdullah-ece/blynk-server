package cc.blynk.server.exp4j.tokenizer.variable;

public class DoubleArrayValue extends VariableValue {

    private final double[] values;

    public DoubleArrayValue(double[] values) {
        this.values = values;
    }

    @Override
    public double doubleValue() {
        return values[0];
    }

    @Override
    public double[] doubleValues() {
        return values;
    }

}
