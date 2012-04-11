package org.karatachi.expression;

import org.karatachi.expression.ast.BinaryOperator;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Literal;
import org.karatachi.expression.ast.Parentheses;
import org.karatachi.expression.ast.UnaryOperator;
import org.karatachi.expression.ast.Variable;

public interface IConverter {

    public Expression convert(Literal literal);

    public Expression convert(Variable variable);

    public Expression convert(Function function);

    public Expression convert(BinaryOperator operator);

    public Expression convert(UnaryOperator operator);

    public Expression convert(Parentheses parentheses);

    public Expression convert(ConditionalExpression conditionalExpression);

}
