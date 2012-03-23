package org.karatachi.example.entity;

import java.io.Serializable;

public class MBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public long time;
    public String host;
    public String title;
    public int level;
    public double value;
}
