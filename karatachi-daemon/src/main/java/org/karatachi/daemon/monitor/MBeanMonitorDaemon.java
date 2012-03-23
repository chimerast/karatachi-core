package org.karatachi.daemon.monitor;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.karatachi.jmx.MBeanAccessor;
import org.karatachi.jmx.MBeanServerWrapper;
import org.karatachi.jmx.MBeanWrapper;
import org.karatachi.system.SystemInfo;
import org.karatachi.translator.PrimitiveTranslator;

public abstract class MBeanMonitorDaemon extends MonitorDaemon {
    private final MBeanServerWrapper server;
    private final Map<String, MBeanAccessor> accessors;

    public MBeanMonitorDaemon(String name, String table) {
        this(name, table, new MBeanServerWrapper());
    }

    public MBeanMonitorDaemon(String name, String table,
            MBeanServerWrapper server) {
        super(name, table, SystemInfo.HOST_NAME);
        this.server = server;
        this.accessors = new ConcurrentSkipListMap<String, MBeanAccessor>();
    }

    @Override
    protected void collectData() throws SQLException {
        for (MBeanAccessor accessor : accessors.values()) {
            storeValue(accessor.getTitle(), accessor.getValue());
        }
    }

    public void addAccessor(String title, String accessor) throws JMException {
        int idx1 = accessor.lastIndexOf(":");
        String name = accessor.substring(0, idx1);
        String attribute = accessor.substring(idx1 + 1);
        String key = null;

        if (attribute.contains(".")) {
            int idx2 = attribute.lastIndexOf(".");
            key = attribute.substring(idx2 + 1);
            attribute = attribute.substring(0, idx2);
        }

        MBeanWrapper bean =
                new MBeanWrapper(server, new ObjectName(name), name);
        for (MBeanAttributeInfo info : bean.getAttributes()) {
            if (info.getName().equals(attribute)) {
                try {
                    Class<?> clazz =
                            Class.forName(PrimitiveTranslator.toWrapperClassName(info.getType()));
                    if (Number.class.isAssignableFrom(clazz) && key == null) {
                        accessors.put(accessor, new MBeanAccessor(title, bean,
                                info));
                    } else if (CompositeData.class.isAssignableFrom(clazz)
                            && key != null) {
                        accessors.put(accessor, new MBeanAccessor(title, bean,
                                info, key));
                    } else {
                        logger.error("Accessor '{}' does not return number",
                                accessor);
                        throw new InvalidAttributeValueException(
                                "Accessor does not return number.");
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("Accessor '{}' is unknown type: {}", accessor,
                            info.getType());
                }
            }
        }
    }

    public void removeAccessor(String accessor) {
        accessors.remove(accessor);
    }
}
