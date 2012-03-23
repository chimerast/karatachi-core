package org.karatachi.classloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.karatachi.translator.PrimitiveTranslator;

public class InstanceDelegator {
    private Class<?> clazz;
    private Object obj;

    private InstanceDelegator() {
    }

    public static InstanceDelegator newInstance(Class<?> clazz,
            Object... initargs) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        InstanceDelegator ret = new InstanceDelegator();
        ret.clazz = clazz;
        ret.obj = newInstanceInternal(clazz, initargs);
        return ret;
    }

    public static InstanceDelegator wrapInstance(Object obj) {
        InstanceDelegator ret = new InstanceDelegator();
        ret.clazz = obj.getClass();
        ret.obj = obj;
        return ret;
    }

    public static InstanceDelegator getStaticInterface(Class<?> clazz) {
        InstanceDelegator ret = new InstanceDelegator();
        ret.clazz = clazz;
        ret.obj = null;
        return ret;
    }

    public Object get(String name) throws NoSuchFieldException,
            IllegalAccessException {
        return clazz.getField(name).get(obj);
    }

    public void set(String name, Object value) throws NoSuchFieldException,
            IllegalAccessException {
        clazz.getField(name).set(obj, value);
    }

    public void setAsString(String name, String value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getField(name);
        field.set(obj, PrimitiveTranslator.valueOf(field.getType(), value));
    }

    public Object invoke(String name, Object... args)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        Method[] methods = clazz.getMethods();
        Class<?>[][] paramTypesList = new Class<?>[methods.length][];

        for (int i = 0; i < methods.length; ++i) {
            if (methods[i].getName().equals(name)) {
                paramTypesList[i] = methods[i].getParameterTypes();
            }
        }

        int index = getMachedParametersIndex(paramTypesList, args);
        if (index >= 0) {
            return methods[index].invoke(obj, args);
        } else {
            throw new NoSuchMethodException(name);
        }
    }

    private static Object newInstanceInternal(Class<?> clazz,
            Object... initargs) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Constructor<?>[] constructors = clazz.getConstructors();
        Class<?>[][] paramTypesList = new Class<?>[constructors.length][];

        for (int i = 0; i < constructors.length; ++i) {
            paramTypesList[i] = constructors[i].getParameterTypes();
        }

        int index = getMachedParametersIndex(paramTypesList, initargs);
        if (index >= 0) {
            return constructors[index].newInstance(initargs);
        } else {
            throw new IllegalArgumentException("<init>");
        }
    }

    private static int getMachedParametersIndex(Class<?>[][] paramTypesList,
            Object[] args) {
        method: for (int i = 0; i < paramTypesList.length; ++i) {
            Class<?>[] paramTypes = paramTypesList[i];
            if (paramTypes == null) {
                continue;
            }
            for (int j = 0;; ++j) {
                if (j == args.length && j == paramTypes.length) {
                    break;
                }
                if (j == args.length || j == paramTypes.length) {
                    continue method;
                }
                if (!PrimitiveTranslator.toWrapperClass(paramTypes[j]).isAssignableFrom(
                        args[j].getClass())) {
                    continue method;
                }
            }
            return i;
        }
        return -1;
    }
}
