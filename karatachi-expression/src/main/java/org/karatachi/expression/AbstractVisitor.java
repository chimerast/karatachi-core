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

public abstract class AbstractVisitor implements IVisitor {

    @Override
    public void visit(Literal literal) {
    }

    @Override
    public void visit(Variable variable) {
    }

    @Override
    public void visit(Function function) {
    }

    @Override
    public void visit(Calculator calculator) {
    }

    @Override
    public void visit(Comparator comparator) {
    }

    @Override
    public void visit(Negative negative) {
    }

    @Override
    public void visit(Not not) {
    }

    @Override
    public void visit(Parentheses parentheses) {
    }

    @Override
    public void visit(ConditionalExpression conditionalExpression) {
    }
}
