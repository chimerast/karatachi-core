package org.karatachi.expression.function;

import java.util.Collections;
import java.util.List;

import org.karatachi.expression.AbstractFunction;
import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.ast.Literal;

public class IfZero extends AbstractFunction {
    public static final String FUNCTION_NAME = "ifzero";

    public IfZero() {
        super(FUNCTION_NAME);
    }

    @Override
    public double evaluate(IEnvironment env, List<Double> args) {
        for (Double arg : args) {
            if (!Double.isNaN(arg) && arg != 0.0) {
                return arg;
            }
        }
        return Double.NaN;
    }

    @Override
    public List<String> getInternallyUsedVariables() {
        return Collections.emptyList();
    }

    @Override
    public String represent(IEnvironment env, List<String> args,
            List<Double> values) {
        for (int i = 0; i < values.size(); ++i) {
            if (!Double.isNaN(values.get(i)) && values.get(i) != 0.0) {
                return "(" + args.get(i) + ")";
            }
        }
        return "NaN";
    }

    @Override
    public Expression extract(IEnvironment env, List<Expression> args,
            List<Double> values) {
        for (int i = 0; i < values.size(); ++i) {
            if (!Double.isNaN(values.get(i)) && values.get(i) != 0.0) {
                return args.get(i);
            }
        }
        return Literal.NaN;
    }
}
