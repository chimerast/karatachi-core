package org.karatachi.jmx;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.openmbean.CompositeData;

public class MBeanAccessor {
    private final String title;
    private final MBeanWrapper bean;
    private final MBeanAttributeInfo attribute;
    private final String key;

    public MBeanAccessor(String title, MBeanWrapper bean,
            MBeanAttributeInfo attribute) {
        this(title, bean, attribute, null);
    }

    public MBeanAccessor(String title, MBeanWrapper bean,
            MBeanAttributeInfo attribute, String key) {
        this.title = title;
        this.bean = bean;
        this.attribute = attribute;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public double getValue() {
        if (key == null) {
            try {
                return ((Number) bean.get(attribute)).doubleValue();
            } catch (JMException e) {
                return Double.NaN;
            }
        } else {
            try {
                CompositeData data = (CompositeData) bean.get(attribute);
                return ((Number) data.get(key)).doubleValue();
            } catch (JMException e) {
                return Double.NaN;
            }
        }
    }
}
