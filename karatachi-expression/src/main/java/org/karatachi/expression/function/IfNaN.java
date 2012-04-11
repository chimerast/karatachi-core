package org.karatachi.expression.function;

import java.util.Collections;
import java.util.List;

import org.karatachi.expression.AbstractFunction;
import org.karatachi.expression.IEnvironment;

public class IfNaN extends AbstractFunction {
    public IfNaN() {
        super("ifNaN");
    }

    @Override
    public double evaluate(IEnvironment env, List<Double> args) {
        for (Double arg : args) {
            if (!Double.isNaN(arg)) {
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
            if (!Double.isNaN(values.get(i))) {
                return "(" + args.get(i) + ")";
            }
        }
        return "NaN";
    }
}
