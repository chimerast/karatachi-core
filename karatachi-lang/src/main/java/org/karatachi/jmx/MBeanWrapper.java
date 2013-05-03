package org.karatachi.jmx;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.karatachi.exception.IncompatiblePlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBeanWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger =
            LoggerFactory.getLogger(MBeanWrapper.class);

    private final Map<String, MBeanWrapper> children =
            new TreeMap<String, MBeanWrapper>();

    private final MBeanServerWrapper server;
    private final ObjectName objectName;
    private final String name;
    private final MBeanInfo info;

    public MBeanWrapper(MBeanServerWrapper server, ObjectName objectName)
            throws JMException {
        this(server, objectName, objectName.getCanonicalName());
    }

    public MBeanWrapper(MBeanServerWrapper server, ObjectName objectName,
            String name) throws JMException {
        this.server = server;
        this.objectName = objectName;
        this.name = name;

        MBeanInfo info = null;
        try {
            info = server.get().getMBeanInfo(objectName);
        } catch (InstanceNotFoundException ignore) {
        }
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public MBeanAttributeInfo[] getAttributes() {
        if (info != null) {
            return info.getAttributes();
        } else {
            return new MBeanAttributeInfo[0];
        }
    }

    public MBeanOperationInfo[] getOperations() {
        if (info != null) {
            return info.getOperations();
        } else {
            return new MBeanOperationInfo[0];
        }
    }

    public MBeanWrapper[] getChildren() {
        return children.values().toArray(new MBeanWrapper[0]);
    }

    public Object get(MBeanAttributeInfo attribute) throws JMException {
        return server.get().getAttribute(objectName, attribute.getName());
    }

    public void set(MBeanAttributeInfo attribute, Object value)
            throws JMException {
        server.get().setAttribute(objectName,
                new Attribute(attribute.getName(), value));
    }

    public Object invoke(MBeanOperationInfo operation) throws JMException {
        return server.get().invoke(objectName, operation.getName(),
                new Object[0], new String[0]);
    }

    @Override
    public String toString() {
        return objectName.getCanonicalName();
    }

    public static Map<String, MBeanWrapper> createDomainTree(
            MBeanServerWrapper server, String domain) {
        TreeMap<String, MBeanWrapper> ret = new TreeMap<String, MBeanWrapper>();
        try {
            Set<ObjectName> objectNames =
                    server.get().queryNames(
                            new ObjectName(String.format("%s:*", domain)), null);
            for (ObjectName objectName : objectNames) {
                try {
                    String type;
                    if ((type = objectName.getKeyProperty("type")) != null) {
                        if (!ret.containsKey(type)) {
                            ret.put(type,
                                    new MBeanWrapper(server, new ObjectName(
                                            String.format("%s:type=%s", domain,
                                                    type)), type));
                        }
                    } else if ((type = objectName.getKeyProperty("j2eeType")) != null) {
                        if (!ret.containsKey(type)) {
                            ret.put(type,
                                    new MBeanWrapper(server, new ObjectName(
                                            String.format("%s:j2eeType=%s",
                                                    domain, type)), type));
                        }
                    } else {
                        continue;
                    }

                    if (objectName.getKeyPropertyList().size() > 1) {
                        MBeanWrapper typeBean = ret.get(type);
                        typeBean.children.put(objectName.getCanonicalName(),
                                new MBeanWrapper(server, objectName));
                    }
                } catch (JMException e) {
                    logger.warn("Fail to create object", e);
                }
            }
        } catch (MalformedObjectNameException e) {
            logger.error("Fail to create domain tree", e);
            throw new IncompatiblePlatformException(e);
        }
        return ret;
    }
}
