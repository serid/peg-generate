package org.example.util;

import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class Util {
    private Util() {
    }

    public static String multiplyChar(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    public static String multiplyString(String s, int n) {
        var str = new StringBuilder();
        for (; n >= 0; n--) {
            str.append(s);
        }
        return str.toString();
    }

    public static String tabulate(int depth) {
        return multiplyChar(' ', depth * 4);
    }

    public static void tabulate(StringBuilder out, int depth) {
        out.append(multiplyChar(' ', depth * 4));
    }

    public static boolean isLineFeed(char c) {
        return c == '\n' || c == '\r';
    }

    public static <T> T[] concat(T[] a, T[] b) {
        T[] c = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static <T> T[] append(T[] array, T element) {
        T[] array2 = Arrays.copyOf(array, array.length + 1);
        array2[array.length] = element;
        return array2;
    }

    public static <T> T[] prepend(T[] array, T element) {
        // create new T array of length (array.length + 1)
        @SuppressWarnings("unchecked")
        T[] array2 = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + 1);

        array2[0] = element;
        System.arraycopy(array, 0, array2, 1, array.length);
        return array2;
    }

    public static <T> void reverse(T[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            T temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    public static <T> T[] reversed_in_place(T[] array) {
        reverse(array);
        return array;
    }

    public static String readString(Path path) {
        try {
            return Files.readString(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String readString(String path) {
        return readString(Path.of(path));
    }

    // use like this
    //try {
    //} catch (Exception e) {
    //    Util.unwrapThrowable(e).printStackTrace();
    //}
    public static Throwable unwrapThrowable(Throwable throwable) {
        while (throwable.getCause() != null)
            throwable = throwable.getCause();
        return throwable;
    }
}
