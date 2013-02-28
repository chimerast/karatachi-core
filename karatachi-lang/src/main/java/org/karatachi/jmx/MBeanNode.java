package org.karatachi.jmx;

import java.io.Serializable;

import javax.management.JMException;

public interface MBeanNode extends Serializable {
    public String getName();

    public Object getValue() throws JMException;
}
