package org.karatachi.system;

import java.net.InetAddress;

public class SystemInfo {
    public static final String HOST_NAME;
    static {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (Exception e) {
            hostname = "unknown";
        }
        HOST_NAME = hostname;
    }
}
