package cc.blynk.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.01.16.
 */
public final class ArrayUtil {

    private ArrayUtil() {
    }

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
