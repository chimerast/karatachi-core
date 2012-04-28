package org.karatachi.expression;

import java.util.ArrayList;
import java.util.List;

import org.karatachi.expression.ast.Bool;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Parentheses;

public class Extractor extends AbstractConverter {

    private final IEnvironment env;

    public Extractor(IEnvironment env) {
        this.env = env;
    }

    @Override
    public Expression convert(Function function) {
        List<Expression> args = new ArrayList<Expression>();
        for (Expression argument : function.getArguments()) {
            args.add(argument.accept(this));
        }
        return env.extractFunction(function.getIdentifier().getName(), args).accept(
                this);
    }

    @Override
    public Expression convert(Parentheses parentheses) {
        return parentheses.getExpression().accept(this);
    }

    @Override
    public Expression convert(ConditionalExpression conditionalExpression) {
        return Bool.test(conditionalExpression.getCondition().value(env))
                ? conditionalExpression.getConsequence().accept(this)
                : conditionalExpression.getAlternative().accept(this);
    }
}
