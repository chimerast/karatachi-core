package org.karatachi.classloader;

public class Reflection {
    public static String getParentMethodInfo() {
        return new Throwable().getStackTrace()[2].toString();
    }

    public static String getAncestorMethodInfo(int index) {
        return new Throwable().getStackTrace()[1 + index].toString();
    }
}
