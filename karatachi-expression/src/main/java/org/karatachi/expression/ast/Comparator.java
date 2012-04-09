package org.karatachi.expression.ast;

import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public abstract class Comparator implements Expression {
    private static final long serialVersionUID = 1L;

    protected final Expression left;
    protected final Expression right;

    public Comparator(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        left.accept(visitor);
        right.accept(visitor);
    }

    public static class GreaterThan extends Comparator {
        private static final long serialVersionUID = 1L;

        public GreaterThan(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) > right.value(env) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " > " + right.represent(env);
        }
    }

    public static class LessThan extends Comparator {
        private static final long serialVersionUID = 1L;

        public LessThan(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) < right.value(env) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " < " + right.represent(env);
        }
    }

    public static class GreaterEqual extends Comparator {
        private static final long serialVersionUID = 1L;

        public GreaterEqual(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) >= right.value(env) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " >= " + right.represent(env);
        }
    }

    public static class LessEqual extends Comparator {
        private static final long serialVersionUID = 1L;

        public LessEqual(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) <= right.value(env) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " <= " + right.represent(env);
        }
    }

    public static class Equal extends Comparator {
        private static final long serialVersionUID = 1L;

        public Equal(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) == right.value(env) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " == " + right.represent(env);
        }
    }

    public static class NotEqual extends Comparator {
        private static final long serialVersionUID = 1L;

        public NotEqual(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) != right.value(env) ? Bool.TRUE
                    : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " != " + right.represent(env);
        }
    }

    public static class Not implements Expression {
        private static final long serialVersionUID = 1L;

        private final Expression expression;

        public Not(Expression expression) {
            this.expression = expression;
        }

        @Override
        public double value(IEnvironment env) {
            return Bool.test(expression.value(env)) ? Bool.FALSE
                    : Bool.TRUE;
        }

        @Override
        public String represent(IEnvironment env) {
            return "!" + expression.represent(env);
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visit(this);
            expression.accept(visitor);
        }
    }
}
