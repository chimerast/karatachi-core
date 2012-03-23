package org.karatachi.jni.win32;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.karatachi.jni.LibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceCounter {
    private static final Logger logger = LoggerFactory
            .getLogger(PerformanceCounter.class);

    static {
        LibraryLoader.load("karatachi-jni");
    }

    public synchronized native static boolean initialize(int[] intervals);

    private synchronized native static int addPerformanceCounter(String path);

    private synchronized native static double[] getPerformanceCounterValue(
            int id);

    private static final SortedMap<String, Integer> ids = new TreeMap<String, Integer>();

    public static boolean addCounter(String path) {
        int id = addPerformanceCounter(path);
        if (id != -1) {
            ids.put(path, id);
            logger.debug("Performance counter added: {}", path);
            return true;
        } else {
            return false;
        }
    }

    public static SortedSet<String> getCounters() {
        return (SortedSet<String>) ids.keySet();
    }

    public static double[] getCounterValue(String key) {
        if (ids.containsKey(key)) {
            return getPerformanceCounterValue(ids.get(key));
        } else {
            return null;
        }
    }

}
