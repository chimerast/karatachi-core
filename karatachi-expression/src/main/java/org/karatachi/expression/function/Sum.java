package org.karatachi.expression.function;

import java.util.Collections;
import java.util.List;

import org.karatachi.expression.AbstractFunction;
import org.karatachi.expression.IEnvironment;

public class Sum extends AbstractFunction {
    public Sum() {
        super("sum");
    }

    @Override
    public double evaluate(IEnvironment env, List<Double> args) {
        double sum = 0.0;
        for (Double arg : args) {
            sum += arg;
        }
        return sum;
    }

    @Override
    public List<String> getInternallyUsedVariables() {
        return Collections.emptyList();
    }

    @Override
    public String represent(IEnvironment env, List<String> args,
            List<Double> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String arg : args) {
            sb.append(arg);
            sb.append(" + ");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 3, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }
}
