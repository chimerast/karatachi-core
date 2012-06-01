package org.karatachi.expression.ast;

import org.karatachi.expression.IConverter;
import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public class Parentheses implements Expression {
    private static final long serialVersionUID = 1L;

    private final Expression expression;

    public Parentheses(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
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

    @Override
    public <T> T accept(IConverter<T> converter) {
        return converter.convert(this);
    }
}
