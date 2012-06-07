package org.karatachi.expression.converter;

import org.karatachi.expression.AbstractExpressionConverter;
import org.karatachi.expression.ast.BinaryOperator;
import org.karatachi.expression.ast.Bool.And;
import org.karatachi.expression.ast.Bool.Or;
import org.karatachi.expression.ast.Calculator.Add;
import org.karatachi.expression.ast.Calculator.Divide;
import org.karatachi.expression.ast.Calculator.Multiply;
import org.karatachi.expression.ast.Calculator.Subtract;
import org.karatachi.expression.ast.Comparator.Equal;
import org.karatachi.expression.ast.Comparator.GreaterEqual;
import org.karatachi.expression.ast.Comparator.GreaterThan;
import org.karatachi.expression.ast.Comparator.LessEqual;
import org.karatachi.expression.ast.Comparator.LessThan;
import org.karatachi.expression.ast.Comparator.NotEqual;
import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.ast.Parentheses;

public class ParenthesesAdder extends AbstractExpressionConverter {
    @Override
    public Expression convert(BinaryOperator operator) {
        Class<?> clazz = operator.getClass();
        boolean l = compareLeft(clazz, operator.getLeft().getClass());
        boolean r = compareRight(clazz, operator.getRight().getClass());
        if (l || r) {
            try {
                Expression left = operator.getLeft().accept(this);
                Expression right = operator.getRight().accept(this);
                if (l) {
                    left = new Parentheses(left);
                }
                if (r) {
                    right = new Parentheses(right);
                }
                return (Expression) clazz.getConstructor(Expression.class,
                        Expression.class).newInstance(left, right);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        } else {
            return super.convert(operator);
        }
    }

    private boolean compareLeft(Class<?> parent, Class<?> child) {
        return getPriority(parent) > getPriority(child);
    }

    private boolean compareRight(Class<?> parent, Class<?> child) {
        return getPriority(parent) >= getPriority(child);
    }

    private int getPriority(Class<?> operator) {
        if (operator == Multiply.class || operator == Divide.class) {
            return 70;
        } else if (operator == Add.class || operator == Subtract.class) {
            return 60;
        } else if (operator == GreaterThan.class || operator == LessThan.class
                || operator == GreaterEqual.class
                || operator == LessEqual.class) {
            return 50;
        } else if (operator == Equal.class || operator == NotEqual.class) {
            return 40;
        } else if (operator == And.class) {
            return 30;
        } else if (operator == Or.class) {
            return 20;
        } else {
            return 80;
        }
    }
}
