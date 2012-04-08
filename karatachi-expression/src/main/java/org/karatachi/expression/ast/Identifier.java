package org.karatachi.expression.ast;

import java.io.Serializable;

public class Identifier implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
