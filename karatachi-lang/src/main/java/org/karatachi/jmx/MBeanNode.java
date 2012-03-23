package org.karatachi.jmx;

import javax.management.JMException;

public interface MBeanNode {
    public String getName();

    public Object getValue() throws JMException;
}
