package org.karatachi.expression.ast;

public class Boolean {

    public static final double TRUE = 1.0;
    public static final double FALSE = 0.0;

    public static boolean test(double value) {
        return value != FALSE && !Double.isNaN(value);
    }
}
