package org.karatachi.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageDir {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CLASS_SUFFIX = ".class";

    private final ClassLoader loader;
    private final String packageName;
    private final String packagePath;

    private final TreeSet<String> packages;
    private final TreeSet<String> classes;
    private final TreeSet<String> resources;

    public PackageDir(String packageName) {
        this(PackageDir.class.getClassLoader(), packageName);
    }

    public PackageDir(ClassLoader loader, String packageName) {
        this.loader = loader;
        this.packageName = packageName;
        this.packagePath = packageName.replace('.', '/') + "/";
        this.packages = new TreeSet<String>();
        this.classes = new TreeSet<String>();
        this.resources = new TreeSet<String>();

        try {
            load();
        } catch (IOException e) {
            logger.warn("Fail to load package", e);
        }
    }

    private void load() throws IOException {
        Enumeration<URL> urls = loader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String filePath = url.toExternalForm().replace(" ", "%20");
            try {
                if (filePath.startsWith("jar:")) {
                    filePath = filePath.substring(4, filePath.lastIndexOf('!'));
                    JarFile jar =
                            new JarFile(new File(new URL(filePath).toURI()));
                    try {
                        loadJar(jar);
                    } finally {
                        jar.close();
                    }
                } else if (filePath.startsWith("file:")) {
                    File dir = new File(new URL(filePath).toURI());
                    loadDir(dir);
                } else {
                    logger.warn("Unknown URL format: {}", filePath);
                }
            } catch (IOException e) {
                logger.warn("Fail to load file", e);
            } catch (URISyntaxException e) {
                logger.warn("Fail to load file", e);
            }
        }
    }

    private void loadJar(JarFile jar) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().startsWith(packagePath)
                    || entry.getName().equals(packagePath)) {
                continue;
            }

            String name = entry.getName().substring(packagePath.length());
            if (entry.isDirectory()) {
                if (name.indexOf('/') != name.length() - 1) {
                    continue;
                }
                packages.add(name.substring(0, name.length() - 1));
            } else {
                if (name.indexOf('/') >= 0) {
                    continue;
                }
                if (name.endsWith(CLASS_SUFFIX)) {
                    if (name.indexOf('$') < 0) {
                        classes.add(name.substring(0, name.length()
                                - CLASS_SUFFIX.length()));
                    }
                } else {
                    resources.add(name);
                }
            }
        }
    }

    private void loadDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                packages.add(file.getName());
            } else {
                if (file.getName().endsWith(CLASS_SUFFIX)) {
                    if (file.getName().indexOf('$') < 0) {
                        String name = file.getName();
                        classes.add(name.substring(0, name.length()
                                - CLASS_SUFFIX.length()));
                    }
                } else {
                    resources.add(file.getName());
                }
            }
        }
    }

    public List<String> getPackageNames() {
        return new ArrayList<String>(packages);
    }

    public List<String> getClassNames() {
        return new ArrayList<String>(classes);
    }

    public List<String> getResourceNames() {
        return new ArrayList<String>(resources);
    }

    public List<Class<?>> getClasses() {
        List<Class<?>> ret = new ArrayList<Class<?>>();
        for (String className : classes) {
            try {
                ret.add(Class.forName(packageName + "." + className, true,
                        loader));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public <T> List<Class<? extends T>> getClasses(Class<T> superClass) {
        ArrayList<Class<? extends T>> ret = new ArrayList<Class<? extends T>>();
        for (String className : classes) {
            try {
                Class<?> clazz =
                        Class.forName(packageName + "." + className, true,
                                loader);
                if (superClass.isAssignableFrom(clazz)) {
                    ret.add((Class<? extends T>) clazz);
                }
            } catch (ClassNotFoundException ignore) {
            }
        }
        return ret;
    }
}
