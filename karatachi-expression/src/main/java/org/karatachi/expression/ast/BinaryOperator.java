package org.karatachi.expression.ast;

import org.karatachi.expression.IVisitor;

public abstract class BinaryOperator implements Expression {
    private static final long serialVersionUID = 1L;

    protected final Expression left;
    protected final Expression right;

    public BinaryOperator(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        left.accept(visitor);
        right.accept(visitor);
    }
}
