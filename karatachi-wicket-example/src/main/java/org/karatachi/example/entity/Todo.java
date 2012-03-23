package org.karatachi.example.entity;

import java.io.Serializable;
import java.util.Date;

import org.seasar.dao.annotation.tiger.Bean;

@Bean(timeStampProperty = "timestamp")
public class Todo implements Serializable {
    private static final long serialVersionUID = 1L;

    public String title;
    public Date date;
    public String message;
    public Date timestamp;
}
