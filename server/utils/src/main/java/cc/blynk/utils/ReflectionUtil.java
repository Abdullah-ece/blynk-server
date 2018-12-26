package cc.blynk.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/4/2015.
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    /**
     * Used to generate map of class fields where key is field value and value is field name.
     */
    public static Map<Integer, String> generateMapOfValueNameInteger(Class<?> clazz) {
        var valuesName = new HashMap<Integer, String>();
        try {
            for (var field : clazz.getFields()) {
                valuesName.put((Integer) field.get(int.class), field.getName());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return valuesName;
    }

    public static Map<Short, String> generateMapOfValueNameShort(Class<?> clazz) {
        var valuesName = new HashMap<Short, String>();
        try {
            for (var field : clazz.getFields()) {
                if (field.getType() == short.class) {
                    valuesName.put((Short) field.get(short.class), formatCommandName(field.getName()));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return valuesName;
    }

    private static String formatCommandName(String fieldName) {
        String[] split = fieldName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(capitilizeFirstLetter(s));
        }
        return sb.toString();
    }

    private static String capitilizeFirstLetter(String name) {
        return name.substring(0, 1) + name.substring(1).toLowerCase();
    }

    public static Object castTo(Class type, String value) {
        if (type == byte.class || type == Byte.class) {
            return Byte.valueOf(value);
        }
        if (type == short.class || type == Short.class) {
            return Short.valueOf(value);
        }
        if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.valueOf(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        }
        return value;
    }
}
