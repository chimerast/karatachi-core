package org.karatachi.expression.ast;

import org.karatachi.expression.IConverter;
import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public class ConditionalExpression implements Expression {
    private static final long serialVersionUID = 1L;

    private final Expression condition;
    private final Expression consequence;
    private final Expression alternative;

    public ConditionalExpression(Expression condition, Expression consequence,
            Expression alternative) {
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getConsequence() {
        return consequence;
    }

    public Expression getAlternative() {
        return alternative;
    }

    @Override
    public double value(IEnvironment env) {
        return Bool.test(condition.value(env)) ? consequence.value(env)
                : alternative.value(env);
    }

    @Override
    public String represent(IEnvironment env) {
        if (!env.isFunctionRepresentationExpanded()) {
            return condition.represent(env) + " ? "
                    + consequence.represent(env) + " : "
                    + alternative.represent(env);
        } else {
            return Bool.test(condition.value(env)) ? consequence.represent(env)
                    : alternative.represent(env);
        }
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        condition.accept(visitor);
        consequence.accept(visitor);
        alternative.accept(visitor);
    }

    @Override
    public <T> T accept(IConverter<T> converter) {
        return converter.convert(this);
    }
}
