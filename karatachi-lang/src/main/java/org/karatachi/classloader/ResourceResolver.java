package org.karatachi.classloader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.karatachi.exception.IncompatiblePlatformException;

public class ResourceResolver {
    public static File getBaseDir(Class<?> clazz) {
        String classFile =
                clazz.getCanonicalName().replace(".", "/") + ".class";
        URL url = clazz.getClassLoader().getResource(classFile);
        String path = url.toExternalForm();

        if (path.startsWith("jar:")) {
            path = path.substring(4, path.lastIndexOf('!'));
            path = path.substring(0, path.lastIndexOf('/') + 1);
        } else if (path.endsWith(classFile)) {
            path = path.substring(0, path.length() - classFile.length());
        }

        try {
            return new File(new URI(path));
        } catch (URISyntaxException e) {
            throw new IncompatiblePlatformException();
        }
    }

    public static URL getClassURL(Class<?> clazz) {
        String classFile =
                clazz.getCanonicalName().replace(".", "/") + ".class";
        return clazz.getClassLoader().getResource(classFile);
    }
}
