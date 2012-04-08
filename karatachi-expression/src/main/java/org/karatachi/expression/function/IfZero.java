package org.karatachi.expression.function;

import java.util.List;

import org.karatachi.expression.AbstractFunction;
import org.karatachi.expression.IEnvironment;

public class IfZero extends AbstractFunction {
    public IfZero() {
        super("ifzero");
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
    public String represent(IEnvironment env, List<String> args,
            List<Double> values) {
        for (int i = 0; i < values.size(); ++i) {
            if (!Double.isNaN(values.get(i)) && values.get(i) != 0.0) {
                return args.get(i);
            }
        }
        return "NaN";
    }
}
