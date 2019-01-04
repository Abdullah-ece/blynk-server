package cc.blynk.server.exp4j.tokenizer.variable;

public class DoubleValue extends VariableValue {

    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public double[] doubleValues() {
        return new double[] {value};
    }

}
