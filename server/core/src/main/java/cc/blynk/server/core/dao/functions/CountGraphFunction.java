package cc.blynk.server.core.dao.functions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 21.07.17.
 */
public final class CountGraphFunction implements GraphFunction {

    private int value = 0;

    @Override
    public void apply(double newValue) {
        this.value++;
    }

    @Override
    public double getResult() {
        return value;
    }
}
