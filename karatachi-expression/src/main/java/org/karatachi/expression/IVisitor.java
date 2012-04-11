package org.karatachi.expression;

import org.karatachi.expression.ast.BinaryOperator;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Literal;
import org.karatachi.expression.ast.Parentheses;
import org.karatachi.expression.ast.UnaryOperator;
import org.karatachi.expression.ast.Variable;

public interface IVisitor {

    public void visit(Literal literal);

    public void visit(Variable variable);

    public void visit(Function function);

    public void visit(BinaryOperator operator);

    public void visit(UnaryOperator operator);

    public void visit(Parentheses parentheses);

    public void visit(ConditionalExpression conditionalExpression);

}
