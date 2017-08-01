package cc.blynk.utils;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.web.product.*;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.01.16.
 */
public class ArrayUtil {

    public static final int[] EMPTY_INTS = {};
    public static final DashBoard[] EMPTY_DASHBOARDS = {};
    public static final Tag[] EMPTY_TAGS = {};
    public static final Device[] EMPTY_DEVICES = {};
    public static final Widget[] EMPTY_WIDGETS = {};
    public static final byte[] EMPTY_BYTES = {};
    public static final App[] EMPTY_APPS = {};
    public static final MetaField[] EMPTY_META_FIELDS = {};
    public static final Product[] EMPTY_PRODUCTS = {};
    public static final Event[] EMPTY_EVENTS = {};
    public static final WebDataStream[] EMPTY_WEB_DATA_STREAMS = {};
    public static final EventReceiver[] EMPTY_RECEIVERS = {};
    public static final GraphDataStream[] EMPTY_DATA_STREAMS = {};

    public static <T> T[] add(T[] array, T element, Class<T> type) {
        T[] newArray = copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] copyArrayGrow1(final T[] array, Class<T> type) {
        T[] newArray = (T[]) Array.newInstance(type, array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] remove(final T[] array, final int index, Class<T> type) {
        final T[] result = (T[]) Array.newInstance(type, array.length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < array.length - 1) {
            System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] cloneArray(T[] array, Class<T> type) {
        if (array == null || array.length == 0) {
            return (T[]) Array.newInstance(type, 0);
        }

        T[] result = (T[]) Array.newInstance(type, array.length);
        int i = 0;
        for (T value : array) {
            try {
                result[i++] = type.getConstructor(type).newInstance(value);
            } catch (Exception e){
                throw new RuntimeException("Error cloning array.");
            }
        }
        return result;
    }

    public static boolean contains(final int[] ar, final int val) {
        for (final int arVal : ar) {
            if (arVal == val) {
                return true;
            }
        }
        return false;
    }

    public static int[] substruct(int in[], int in2[]) {
        Set<Integer> inSet = arrayToSet(in);
        Set<Integer> existingSet = arrayToSet(in2);
        inSet.removeAll(existingSet);
        return toInt(inSet);
    }

    public static Set<Integer> arrayToSet(int[] array) {
        if (array.length == 0) {
            return Collections.emptySet();
        }
        HashSet<Integer> set = new HashSet<>();
        for (int i : array) {
            set.add(i);
        }
        return set;
    }

    public static int[] toInt(Set<Integer> set) {
        int[] a = new int[set.size()];
        int i = 0;
        for (Integer val : set) {
            a[i++] = val;
        }
        return a;
    }

}
