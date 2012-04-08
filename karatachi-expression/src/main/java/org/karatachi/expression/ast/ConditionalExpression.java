package org.karatachi.expression.ast;

import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public class ConditionalExpression implements Expression {
    private static final long serialVersionUID = 1L;

    private final Expression cond;
    private final Expression consequence;
    private final Expression alternative;

    public ConditionalExpression(Expression cond, Expression consequence,
            Expression alternative) {
        this.cond = cond;
        this.consequence = consequence;
        this.alternative = alternative;
    }

    @Override
    public double value(IEnvironment env) {
        return Boolean.test(cond.value(env)) ? consequence.value(env)
                : alternative.value(env);
    }

    @Override
    public String represent(IEnvironment env) {
        if (!env.isRepresentationExpanded()) {
            return cond.represent(env) + " ? " + consequence.represent(env)
                    + " : " + alternative.represent(env);
        } else {

            return Boolean.test(cond.value(env)) ? consequence.represent(env)
                    : alternative.represent(env);
        }
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        cond.accept(visitor);
        consequence.accept(visitor);
        alternative.accept(visitor);
    }
}
