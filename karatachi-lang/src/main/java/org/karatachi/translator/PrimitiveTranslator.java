package org.karatachi.translator;

import org.karatachi.exception.IncompatiblePlatformException;

public class PrimitiveTranslator {
    public static Object valueOf(Class<?> clazz, String value) {
        Class<?> wrapper = toWrapperClass(clazz);
        if (value == null) {
            return null;
        } else if (wrapper == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (wrapper == Byte.class) {
            return Byte.valueOf(value);
        } else if (wrapper == Character.class) {
            return value.charAt(0);
        } else if (wrapper == Short.class) {
            return Short.valueOf(value);
        } else if (wrapper == Integer.class) {
            return Integer.valueOf(value);
        } else if (wrapper == Long.class) {
            return Long.valueOf(value);
        } else if (wrapper == Float.class) {
            return Float.valueOf(value);
        } else if (wrapper == Double.class) {
            return Double.valueOf(value);
        } else if (wrapper == String.class) {
            return value;
        } else {
            throw new IncompatiblePlatformException();
        }
    }

    public static Class<?> toWrapperClass(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        } else if (clazz == boolean.class) {
            return Boolean.class;
        } else if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == char.class) {
            return Character.class;
        } else if (clazz == short.class) {
            return Short.class;
        } else if (clazz == int.class) {
            return Integer.class;
        } else if (clazz == long.class) {
            return Long.class;
        } else if (clazz == float.class) {
            return Float.class;
        } else if (clazz == double.class) {
            return Double.class;
        } else {
            throw new IncompatiblePlatformException();
        }
    }

    public static String toWrapperClassName(String className) {
        if ("boolean".equals(className)) {
            return Boolean.class.getName();
        } else if ("byte".equals(className)) {
            return Byte.class.getName();
        } else if ("char".equals(className)) {
            return Character.class.getName();
        } else if ("short".equals(className)) {
            return Short.class.getName();
        } else if ("int".equals(className)) {
            return Integer.class.getName();
        } else if ("long".equals(className)) {
            return Long.class.getName();
        } else if ("float".equals(className)) {
            return Float.class.getName();
        } else if ("double".equals(className)) {
            return Double.class.getName();
        } else {
            return className;
        }
    }
}
