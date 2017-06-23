package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.04.17.
 */
public class DataStream {

    public String name;

    public MeasurementUnit units;

    public double min;

    public double max;

    public byte pin;

    public DataStream() {
    }

    public DataStream(String name, MeasurementUnit units, double min, double max, byte pin) {
        this.name = name;
        this.units = units;
        this.min = min;
        this.max = max;
        this.pin = pin;
    }
}
