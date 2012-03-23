package org.karatachi.jmx;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

public class MBeanServerWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    protected MBeanServer get() {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
