package org.karatachi.jni;

import java.io.File;

import org.karatachi.classloader.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryLoader {
    public static final Logger logger = LoggerFactory
            .getLogger(LibraryLoader.class);

    public static void load(String libname) {
        try {
            File dir = ResourceResolver.getBaseDir(LibraryLoader.class);
            while (dir != null) {
                File lib = new File(dir, System.mapLibraryName(libname));
                if (lib.exists()) {
                    System.load(lib.getAbsolutePath());
                    logger.info("Success to load native library: {} from {}",
                            libname, lib.getAbsolutePath());
                    return;
                }
                dir = dir.getParentFile();
            }

            System.loadLibrary(libname);
            logger.info("Success to load native library: {} from {}", libname,
                    System.getProperty("java.library.path"));
        } catch (Error e) {
            logger.error("Fail to load native library {}", libname, e);
            throw e;
        }
    }
}
