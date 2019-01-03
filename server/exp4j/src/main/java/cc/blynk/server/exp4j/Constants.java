package cc.blynk.server.exp4j;

import java.util.Map;

public final class Constants {

    public static final String PI   = "pi";
    public static final String PI_2 = "π";
    public static final String E    = "e";
    public static final String PHI  = "φ";
    public static final Map<String, Double> ALL = Map.of(
            PI,   Math.PI,
            PI_2, Math.PI,
            E,    Math.E,
            PHI,  1.61803398874D
    );

    private Constants() {
    }

}
