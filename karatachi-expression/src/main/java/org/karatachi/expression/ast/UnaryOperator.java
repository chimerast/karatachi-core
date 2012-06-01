package org.karatachi.expression.ast;

import org.karatachi.expression.IConverter;
import org.karatachi.expression.IVisitor;

public abstract class UnaryOperator implements Expression {
    private static final long serialVersionUID = 1L;

    protected final Expression expression;

    public UnaryOperator(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        expression.accept(visitor);
    }

    @Override
    public <T> T accept(IConverter<T> converter) {
        return converter.convert(this);
    }
}
