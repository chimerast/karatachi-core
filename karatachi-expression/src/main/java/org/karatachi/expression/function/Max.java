package org.karatachi.expression.function;

import java.util.List;

import org.karatachi.expression.AbstractFunction;
import org.karatachi.expression.IEnvironment;

public class Max extends AbstractFunction {
    public Max() {
        super("max");
    }

    @Override
    public double evaluate(IEnvironment env, List<Double> args) {
        double max = Double.NaN;
        for (Double arg : args) {
            if (Double.isNaN(arg)) {
                continue;
            }

            if (Double.isNaN(max) || arg > max) {
                max = arg;
            }
        }
        return max;
    }

    @Override
    public String represent(IEnvironment env, List<String> args,
            List<Double> values) {
        int imax = -1;
        double max = Double.NaN;
        for (int i = 0; i < values.size(); ++i) {
            Double value = values.get(i);
            if (Double.isNaN(value)) {
                continue;
            }

            if (Double.isNaN(max) || value > max) {
                max = value;
                imax = i;
            }
        }

        if (imax >= 0) {
            return "(" + args.get(imax) + ")";
        } else {
            return "NaN";
        }
    }
}
