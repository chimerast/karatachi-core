package org.karatachi.expression;

import java.util.ArrayList;
import java.util.List;

import org.karatachi.expression.ast.BinaryOperator;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Literal;
import org.karatachi.expression.ast.Parentheses;
import org.karatachi.expression.ast.UnaryOperator;
import org.karatachi.expression.ast.Variable;

public abstract class AbstractExpressionConverter implements IConverter<Expression> {

    @Override
    public Expression convert(Literal literal) {
        return literal;
    }

    @Override
    public Expression convert(Variable variable) {
        return variable;
    }

    @Override
    public Expression convert(Function function) {
        List<Expression> args = new ArrayList<Expression>();
        for (Expression argument : function.getArguments()) {
            args.add(argument.accept(this));
        }
        return new Function(function.getIdentifier(), args);
    }

    @Override
    public Expression convert(BinaryOperator operator) {
        try {
            Class<?> clazz = operator.getClass();
            return (Expression) clazz.getConstructor(Expression.class,
                    Expression.class).newInstance(
                    operator.getLeft().accept(this),
                    operator.getRight().accept(this));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public Expression convert(UnaryOperator operator) {
        try {
            Class<?> clazz = operator.getClass();
            return (Expression) clazz.getConstructor(Expression.class).newInstance(
                    operator.getExpression().accept(this));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public Expression convert(Parentheses parentheses) {
        return new Parentheses(parentheses.getExpression().accept(this));
    }

    @Override
    public Expression convert(ConditionalExpression conditionalExpression) {
        return new ConditionalExpression(
                conditionalExpression.getCondition().accept(this),
                conditionalExpression.getConsequence().accept(this),
                conditionalExpression.getAlternative().accept(this));
    }
}
