package org.karatachi.system;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import org.karatachi.translator.ByteArrayTranslator;

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

    public enum OSType {
        UNKNOWN, WINDOWS, LINUX
    }

    public static final OSType OS_TYPE;
    static {
        String os = System.getProperty("os.name");
        if (os == null) {
            OS_TYPE = OSType.UNKNOWN;
        } else if (os.startsWith("Windows")) {
            OS_TYPE = OSType.WINDOWS;
        } else if (os.startsWith("Linux")) {
            OS_TYPE = OSType.LINUX;
        } else {
            OS_TYPE = OSType.UNKNOWN;
        }
    }

    public static long getMacAddress(String name) {
        try {
            NetworkInterface nic = NetworkInterface.getByName(name);
            if (nic == null) {
                return 0;
            }
            byte[] mac = nic.getHardwareAddress();
            if (mac == null) {
                return 0;
            }
            return ByteArrayTranslator.toLong(mac);
        } catch (SocketException e) {
            return 0;
        }
    }
}
