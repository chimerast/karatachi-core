package org.karatachi.wicket.auto;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.form.FormComponent;
import org.karatachi.classloader.PackageDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FormComponentResolver extends ComponentResolver {
    private final static String ROOT_PACKAGE =
            "org.karatachi.wicket.auto.resolver";
    private final static Map<String, FormComponentResolver> map;

    static {
        map = new HashMap<String, FormComponentResolver>();

        PackageDir dir =
                new PackageDir(FormComponentResolver.class.getClassLoader(),
                        ROOT_PACKAGE);
        for (Class<? extends FormComponentResolver> clazz : dir.getClasses(FormComponentResolver.class)) {
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }
            try {
                FormComponentResolver resolver = clazz.newInstance();
                map.put(resolver.getTypeName(), resolver);
            } catch (Exception e) {
                Logger logger =
                        LoggerFactory.getLogger(FormComponentResolver.class);
                logger.error("FormComponentResolver registering error.", e);
            }
        }
    }

    public static FormComponentResolver getResolver(String... types) {
        for (String type : types) {
            FormComponentResolver resolver = map.get(type);
            if (resolver != null) {
                return resolver;
            }
        }
        return null;
    }

    public static FormComponentResolver getDefaultResolver() {
        return map.get("text");
    }

    public static FormComponentResolver getTextareaResolver() {
        return map.get("textarea");
    }

    public abstract FormComponent<?> createFormComponent(String id);
}
