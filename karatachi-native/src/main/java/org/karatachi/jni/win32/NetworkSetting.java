package org.karatachi.jni.win32;

import java.net.Inet4Address;

import org.karatachi.jni.LibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkSetting {
    private static final Logger logger = LoggerFactory
            .getLogger(NetworkSetting.class);

    static {
        LibraryLoader.load("karatachi-jni");
    }

    private native static boolean addIpAddress(int org, int append, int mask);

    private static int addressToIntForm(Inet4Address addr) {
        assert addr != null;

        byte[] b = addr.getAddress();
        return ((b[0] & 0xFF) << 0) | ((b[1] & 0xFF) << 8)
                | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);
    }

    public static boolean addIPAddress(Inet4Address org, Inet4Address append) {
        if (org == null || append == null)
            return false;

        if (addIpAddress(addressToIntForm(org), addressToIntForm(append),
                0x00FFFFFF)) {
            logger.info("Success to add IP address: {} to {}", append, org);
            return true;
        } else {
            logger.info("Fail to add IP address: {} to {}", append, org);
            return false;
        }
    }
}
