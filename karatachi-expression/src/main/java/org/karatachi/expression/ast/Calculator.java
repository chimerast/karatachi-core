package org.karatachi.expression.ast;

import org.karatachi.expression.IEnvironment;

public abstract class Calculator extends BinaryOperator {
    private static final long serialVersionUID = 1L;

    public Calculator(Expression left, Expression right) {
        super(left, right);
    }

    public static class Add extends Calculator {
        private static final long serialVersionUID = 1L;

        public Add(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) + right.value(env);
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " + " + right.represent(env);
        }
    }

    public static class Subtract extends Calculator {
        private static final long serialVersionUID = 1L;

        public Subtract(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) - right.value(env);
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " - " + right.represent(env);
        }
    }

    public static class Multiply extends Calculator {
        private static final long serialVersionUID = 1L;

        public Multiply(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) * right.value(env);
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " * " + right.represent(env);
        }
    }

    public static class Divide extends Calculator {
        private static final long serialVersionUID = 1L;

        public Divide(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) / right.value(env);
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " / " + right.represent(env);
        }
    }

    public static class Negative extends UnaryOperator {
        private static final long serialVersionUID = 1L;

        public Negative(Expression expression) {
            super(expression);
        }

        @Override
        public double value(IEnvironment env) {
            return -expression.value(env);
        }

        @Override
        public String represent(IEnvironment env) {
            return "-" + expression.represent(env);
        }
    }
}
