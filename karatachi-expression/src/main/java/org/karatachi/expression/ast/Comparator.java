package org.karatachi.expression.ast;

import org.karatachi.expression.IEnvironment;

public abstract class Comparator extends BinaryOperator {
    private static final long serialVersionUID = 1L;

    public Comparator(Expression left, Expression right) {
        super(left, right);
    }

    public static class GreaterThan extends Comparator {
        private static final long serialVersionUID = 1L;

        public GreaterThan(Expression left, Expression right) {
            super(left, right);
        }

        @Override
        public double value(IEnvironment env) {
            return left.value(env) > right.value(env) ? Bool.TRUE : Bool.FALSE;
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
            return left.value(env) < right.value(env) ? Bool.TRUE : Bool.FALSE;
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
            return left.value(env) >= right.value(env) ? Bool.TRUE : Bool.FALSE;
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
            return left.value(env) <= right.value(env) ? Bool.TRUE : Bool.FALSE;
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
            return left.value(env) == right.value(env) ? Bool.TRUE : Bool.FALSE;
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
            return left.value(env) != right.value(env) ? Bool.TRUE : Bool.FALSE;
        }

        @Override
        public String represent(IEnvironment env) {
            return left.represent(env) + " != " + right.represent(env);
        }
    }

    public static class Not extends UnaryOperator {
        private static final long serialVersionUID = 1L;

        public Not(Expression expression) {
            super(expression);
        }

        @Override
        public double value(IEnvironment env) {
            return Bool.test(expression.value(env)) ? Bool.FALSE : Bool.TRUE;
        }

        @Override
        public String represent(IEnvironment env) {
            return "!" + expression.represent(env);
        }
    }
}
