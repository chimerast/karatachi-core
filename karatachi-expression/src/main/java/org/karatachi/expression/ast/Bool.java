package org.karatachi.expression.ast;

import org.karatachi.expression.IEnvironment;

public abstract class Bool extends BinaryOperator {
    private static final long serialVersionUID = 1L;

    public static final double TRUE = 1.0;
    public static final double FALSE = Double.NaN;

    public static boolean test(double value) {
        return !Double.isNaN(value);
    }

    public Bool(Expression left, Expression right) {
        super(left, right);
    }

    public static class And extends Bool {
        private static final long serialVersionUID = 1L;

        public And(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return test(left.value(env)) && test(right.value(env)) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " && " + right.represent(env);
        }
    }

    public static class Or extends Bool {
        private static final long serialVersionUID = 1L;

        public Or(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return test(left.value(env)) || test(right.value(env)) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " || " + right.represent(env);
        }
    }
}
