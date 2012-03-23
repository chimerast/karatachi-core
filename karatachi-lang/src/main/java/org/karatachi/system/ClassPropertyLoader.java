package org.karatachi.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.karatachi.classloader.InstanceDelegator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPropertyLoader {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Properties properties = new Properties();

    public void loadIfNotContains(Class<?> clazz, String path) {
        loadIfNotContains(clazz.getResourceAsStream(path));
    }

    public void loadIfNotContains(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            loadIfNotContains(in);
        } catch (FileNotFoundException ignore) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    public void loadIfNotContains(InputStream in) {
        if (in == null) {
            return;
        }

        try {
            Properties props = new Properties();
            props.load(in);
            for (Object key : props.keySet()) {
                if (!properties.containsKey(key)) {
                    properties.put(key, props.get(key));
                }
            }
        } catch (Exception e) {
            logger.warn("Fail to load properties", e);
        }
    }

    public void setClassProperties(Class<?> clazz) {
        String canonicalPrefix = clazz.getCanonicalName() + ".";
        String simplePrefix = clazz.getSimpleName() + ".";

        InstanceDelegator iface = InstanceDelegator.getStaticInterface(clazz);

        for (Object k : properties.keySet()) {
            try {
                String key = (String) k;
                if (key.startsWith(canonicalPrefix)) {
                    iface.setAsString(key.substring(canonicalPrefix.length()),
                            properties.getProperty(key));
                } else if (key.startsWith(simplePrefix)) {
                    iface.setAsString(key.substring(simplePrefix.length()),
                            properties.getProperty(key));
                }
            } catch (NoSuchFieldException e) {
                ;
            } catch (Exception e) {
                logger.warn("Fail to set property", e);
            }
        }
    }

    public void logProperties() {
        if (logger.isDebugEnabled()) {
            StringWriter buffer = new StringWriter();
            Set<Object> keys = new TreeSet<Object>(properties.keySet());
            for (Object key : keys) {
                buffer.write(String.format("%s=%s\n", key,
                        properties.getProperty((String) key)));
            }
            logger.debug(buffer.getBuffer().toString());
        }
    }
}
