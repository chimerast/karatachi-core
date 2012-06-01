package org.karatachi.expression;

import org.karatachi.expression.ast.BinaryOperator;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Literal;
import org.karatachi.expression.ast.Parentheses;
import org.karatachi.expression.ast.UnaryOperator;
import org.karatachi.expression.ast.Variable;

public interface IConverter<T> {

    public T convert(Literal literal);

    public T convert(Variable variable);

    public T convert(Function function);

    public T convert(BinaryOperator operator);

    public T convert(UnaryOperator operator);

    public T convert(Parentheses parentheses);

    public T convert(ConditionalExpression conditionalExpression);

}
