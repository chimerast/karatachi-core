package org.karatachi.expression.ast;

import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public abstract class Calculator implements Expression {
    private static final long serialVersionUID = 1L;

    protected final Expression left;
    protected final Expression right;

    public Calculator(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        left.accept(visitor);
        right.accept(visitor);
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

    public static class Negative implements Expression {
        private static final long serialVersionUID = 1L;

        private final Expression expression;

        public Negative(Expression expression) {
            this.expression = expression;
        }

        @Override
        public double value(IEnvironment env) {
            return -expression.value(env);
        }

        @Override
        public String represent(IEnvironment env) {
            return "-" + expression.represent(env);
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visit(this);
            expression.accept(visitor);
        }
    }

    public static class Parentheses implements Expression {
        private static final long serialVersionUID = 1L;

        private final Expression expression;

        public Parentheses(Expression expression) {
            this.expression = expression;
        }

        @Override
        public double value(IEnvironment env) {
            return expression.value(env);
        }

        @Override
        public String represent(IEnvironment env) {
            return "(" + expression.represent(env) + ")";
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visit(this);
            expression.accept(visitor);
        }
    }
}
