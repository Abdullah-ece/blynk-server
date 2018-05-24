package cc.blynk.core.http.utils;

import cc.blynk.core.http.model.NameCountResponse;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.stats.model.CommandStat;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.12.15.
 */
public final class AdminHttpUtil {

    private AdminHttpUtil() {
    }

    @SuppressWarnings("unchecked")
    public static List<?> sortStringAsInt(List<?> list, String orderField, SortOrder order) {
        if (list.size() == 0) {
            return list;
        }

        if (orderField == null) {
            orderField = "name";
        }
        if (order == null) {
            order = SortOrder.ASC;
        }


        Comparator c = new GenericStringAsIntComparator(list.get(0).getClass(), orderField);
        list.sort(order == SortOrder.ASC ? c : Collections.reverseOrder(c));

        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> sort(List<T> list, String[] orderFields, SortOrder order) {
        if (list.size() == 0 || orderFields == null || orderFields.length == 0) {
            return list;
        }

        if (order == null) {
            order = SortOrder.ASC;
        }

        Comparator c = new MultiFieldComparator(list.get(0).getClass(), orderFields);
        list.sort(order == SortOrder.ASC ? c : Collections.reverseOrder(c));

        return list;
    }

    public static <T> List<T> sort(List<T> list, String orderField, SortOrder order) {
        if (list.size() == 0) {
            return list;
        }

        if (orderField == null) {
            orderField = "name";
        }
        if (order == null) {
            order = SortOrder.ASC;
        }

        Comparator c = new GenericComparator(list.get(0).getClass(), orderField);
        list.sort(order == SortOrder.ASC ? c : Collections.reverseOrder(c));

        return list;
    }

    public static List<NameCountResponse> convertMapToPair(Map<String, ?> map) {
        return map.entrySet().stream().map(NameCountResponse::new).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static List<NameCountResponse> convertObjectToMap(CommandStat commandStat) {
        return convertMapToPair(JsonParser.MAPPER.convertValue(commandStat, Map.class));
    }

    /**
     * The Blynk Project.
     * Created by Dmitriy Dumanskiy.
     * Created on 10.12.15.
     */
    public static class GenericComparator implements Comparator {

        private final Class<?> fieldType;
        private final Field field;

        GenericComparator(Class<?> type, String sortField) {
            try {
                this.field = type.getField(sortField);
                this.fieldType = field.getType();
            } catch (NoSuchFieldException nsfe) {
                throw new RuntimeException("Can't find field " + sortField + " for " + type.getName());
            }
        }

        @Override
        public int compare(Object o1, Object o2) {
            try {
                Object v1 = field.get(o1);
                Object v2 = field.get(o2);

                return compareActual(v1, v2, fieldType);
            } catch (Exception e) {
                throw new RuntimeException("Error on compare during sorting. Type : " + e.getMessage());
            }
        }

        public int compareActual(Object v1, Object v2, Class<?> returnType) {
            if (returnType == int.class || returnType == Integer.class) {
                return Integer.compare((int) v1, (int) v2);
            }
            if (returnType == long.class || returnType == Long.class) {
                return Long.compare((long) v1, (long) v2);
            }
            if (returnType == String.class) {
                return v1 == null
                        ? (v2 == null ? 0 : Integer.MIN_VALUE)
                        : (v2 == null ? Integer.MAX_VALUE : ((String) v1).compareTo((String) v2));
            }

            throw new RuntimeException("Unexpected field type. Type : " + returnType.getName());
        }
    }

    public static class MultiFieldComparator implements Comparator {

        private final GenericComparator[] fieldComparators;

        public MultiFieldComparator(Class<?> type, String[] sortFields) {
            this.fieldComparators = new GenericComparator[sortFields.length];
            int i = 0;
            for (String field : sortFields) {
                fieldComparators[i++] = new GenericComparator(type, field);
            }
        }

        @Override
        public int compare(Object o1, Object o2) {
            int r = 0;
            for (GenericComparator genericComparator : fieldComparators) {
                r = genericComparator.compare(o1, o2);
                if (r != 0) {
                    return r;
                }
            }
            return r;
        }
    }

    public static class GenericStringAsIntComparator extends GenericComparator {

        GenericStringAsIntComparator(Class<?> type, String sortField) {
            super(type, sortField);
        }

        @Override
        public int compareActual(Object v1, Object v2, Class<?> returnType) {
            if (returnType == int.class || returnType == Integer.class) {
                return Integer.compare((int) v1, (int) v2);
            }
            if (returnType == long.class || returnType == Long.class) {
                return Long.compare((long) v1, (long) v2);
            }
            if (returnType == String.class) {
                return Integer.valueOf((String) v1).compareTo(Integer.valueOf((String) v2));
            }

            throw new RuntimeException("Unexpected field type. Type : " + returnType.getName());
        }

    }
}
