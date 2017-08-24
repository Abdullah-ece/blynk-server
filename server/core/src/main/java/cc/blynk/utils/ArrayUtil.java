package cc.blynk.utils;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDataStream;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.widgets.CopyObject;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static boolean contains(final int[] ar, final int val) {
        for (final int arVal : ar) {
            if (arVal == val) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    @SuppressWarnings("unchecked")
    public static MetaField[] substruct(MetaField[] in, MetaField[] in2) {
        List<MetaField> inSet = arrayToList(in);
        List<MetaField> existingSet = arrayToList(in2);
        inSet.removeAll(existingSet);
        return inSet.toArray(new MetaField[0]);
    }

    public static <T> List<T> arrayToList(T[] array) {
        if (array.length == 0) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }


    public static int[] substruct(int[] in, int[] in2) {
        List<Integer> inSet = arrayToList(in);
        List<Integer> existingSet = arrayToList(in2);
        inSet.removeAll(existingSet);
        return toInt(inSet);
    }


    private static List<Integer> arrayToList(int[] array) {
        if (array.length == 0) {
            return Collections.emptyList();
        }
        List<Integer> set = new ArrayList<>();
        for (int i : array) {
            set.add(i);
        }
        return set;
    }

    private static int[] toInt(List<Integer> list) {
        int[] a = new int[list.size()];
        int i = 0;
        for (Integer val : list) {
            a[i++] = val;
        }
        return a;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] copy(CopyObject[] copyObjects, Class<T> clazz) {
        if (copyObjects == null || copyObjects.length == 0) {
            return (T[]) Array.newInstance(clazz, 0);
        }

        T[] result = (T[]) Array.newInstance(clazz, copyObjects.length);
        int i = 0;
        for (CopyObject obj : copyObjects) {
            result[i++] = (T) obj.copy();
        }
        return result;
    }

}
