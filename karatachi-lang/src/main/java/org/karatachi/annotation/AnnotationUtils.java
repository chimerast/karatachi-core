package org.karatachi.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtils {
    public static <T extends Annotation> T getInterfaceAnnotation(
            Method method, Class<T> annotationClass) {
        for (Class<?> iface : method.getDeclaringClass().getInterfaces()) {
            try {
                Method m =
                        iface.getMethod(method.getName(),
                                method.getParameterTypes());
                return m.getAnnotation(annotationClass);
            } catch (NoSuchMethodException ignore) {
            }
        }
        return null;
    }
}
