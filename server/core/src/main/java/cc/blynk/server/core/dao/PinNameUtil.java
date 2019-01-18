package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
//todo should be removed
public final class PinNameUtil {

    private PinNameUtil() {
    }

    private static String generateFilename(char pinType, short pin, String type) {
        return generateFilename("" + pinType + pin, type);
    }

    private static String generateFilename(String pin, String type) {
        return "history_" + pin + "_" + type + ".bin";
    }

    public static String generateFilename(PinType pinType, short pin, GraphGranularityType type) {
        return generateFilename(pinType.pintTypeChar, pin, type.label);
    }
}
