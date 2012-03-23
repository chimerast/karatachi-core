package org.karatachi.wicket.auto;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.karatachi.classloader.PackageDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ComponentResolver {
    private final static String ROOT_PACKAGE =
            "org.karatachi.wicket.auto.resolver";
    private final static Map<String, ComponentResolver> map;

    static {
        map = new HashMap<String, ComponentResolver>();

        PackageDir dir =
                new PackageDir(ComponentResolver.class.getClassLoader(),
                        ROOT_PACKAGE);
        for (Class<? extends ComponentResolver> clazz : dir.getClasses(ComponentResolver.class)) {
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }
            try {
                ComponentResolver resolver = clazz.newInstance();
                map.put(resolver.getTypeName(), resolver);
            } catch (Exception e) {
                Logger logger =
                        LoggerFactory.getLogger(ComponentResolver.class);
                logger.error("ComponentResolver registering error.", e);
            }
        }
    }

    public static ComponentResolver getResolver(String... types) {
        for (String type : types) {
            ComponentResolver resolver = map.get(type);
            if (resolver != null) {
                return resolver;
            }
        }
        return null;
    }

    public static ComponentResolver getDefaultResolver() {
        return map.get("text");
    }

    public abstract String getTypeName();

    public Component createViewComponent(String id) {
        return new Label(id);
    }
}
