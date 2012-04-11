package org.karatachi.expression;

import org.karatachi.expression.ast.BinaryOperator;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Literal;
import org.karatachi.expression.ast.Parentheses;
import org.karatachi.expression.ast.UnaryOperator;
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
    public void visit(BinaryOperator operator) {
    }

    @Override
    public void visit(UnaryOperator operator) {
    }

    @Override
    public void visit(Parentheses parentheses) {
    }

    @Override
    public void visit(ConditionalExpression conditionalExpression) {
    }
}
