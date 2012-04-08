package org.karatachi.expression;

import org.karatachi.expression.ast.Calculator;
import org.karatachi.expression.ast.Calculator.Negative;
import org.karatachi.expression.ast.Calculator.Parentheses;
import org.karatachi.expression.ast.Comparator;
import org.karatachi.expression.ast.Comparator.Not;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Literal;
import org.karatachi.expression.ast.Variable;

public interface IVisitor {

    public void visit(Literal literal);

    public void visit(Variable variable);

    public void visit(Function function);

    public void visit(Calculator calculator);

    public void visit(Comparator comparator);

    public void visit(Negative negative);

    public void visit(Not not);

    public void visit(Parentheses parentheses);

    public void visit(ConditionalExpression conditionalExpression);

}
