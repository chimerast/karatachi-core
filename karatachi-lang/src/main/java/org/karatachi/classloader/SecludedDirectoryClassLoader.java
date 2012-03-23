package org.karatachi.classloader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.karatachi.exception.IncompatiblePlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecludedDirectoryClassLoader extends URLClassLoader {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final File dir;

    public SecludedDirectoryClassLoader(File dir) throws FileNotFoundException {
        super(new URL[0], null);

        if (dir == null || !dir.isDirectory())
            throw new FileNotFoundException();

        this.dir = dir;

        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (!pathname.isFile())
                    return false;
                if (pathname.getName().endsWith(".jar"))
                    return true;
                return false;
            }
        });

        if (files.length == 0) {
            files = new File[] { dir };
        }

        logger.info("SecludedClassLoader created: " + dir.getAbsolutePath());

        URL[] urls = new URL[files.length];
        for (int i = 0; i < urls.length; ++i) {
            try {
                addURL(files[i].toURI().toURL());
                logger.debug("  {}", files[i].getName());
            } catch (MalformedURLException e) {
                throw new IncompatiblePlatformException();
            }
        }
    }

    public File getDir() {
        return dir;
    }

    public InstanceDelegator getStaticInterface(String className)
            throws ClassNotFoundException {
        return InstanceDelegator.getStaticInterface(loadClass(className));
    }

    public InstanceDelegator newInstance(String className, Object... initargs)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException {
        return InstanceDelegator.newInstance(loadClass(className), initargs);
    }
}
