package org.karatachi.beans;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.commons.lang.StringEscapeUtils;

public class XmlSerializer {
    public static String toXml(Object obj) {
        StringBuilder str = new StringBuilder();
        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        str.append("<root>\n");
        toXml(str, obj);
        str.append("</root>\n");
        return str.toString();
    }

    public static byte[] toXmlBytes(Object obj) {
        try {
            return toXml(obj).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static void toXml(StringBuilder str, Object obj) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.PUBLIC) != 0) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    String name = field.getName();
                    objectToXml(str, name, value);
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

    private static void objectToXml(StringBuilder str, String name, Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> type = obj.getClass();
        if (type.isPrimitive() || Number.class.isAssignableFrom(type)
                || Boolean.class.isAssignableFrom(type) || type == String.class) {
            str.append("<" + name + ">");
            str.append(StringEscapeUtils.escapeXml(obj.toString()));
            str.append("</" + name + ">\n");
        } else if (type.isArray()) {
            arrayToXml(str, name, obj);
        } else if (Iterable.class.isAssignableFrom(type)) {
            iterableToXml(str, name, (Iterable<?>) obj);
        } else {
            str.append("<" + name + ">\n");
            toXml(str, obj);
            str.append("</" + name + ">\n");
        }
    }

    private static void arrayToXml(StringBuilder str, String name, Object array) {
        int len = Array.getLength(array);
        for (int i = 0; i < len; ++i) {
            objectToXml(str, name, Array.get(array, i));
        }
    }

    private static void iterableToXml(StringBuilder str, String name,
            Iterable<?> collection) {
        for (Object obj : collection) {
            objectToXml(str, name, obj);
        }
    }
}
