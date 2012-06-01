package org.karatachi.expression.ast;

import org.karatachi.expression.IConverter;
import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public class Literal implements Expression {
    private static final long serialVersionUID = 1L;

    public static final Literal NaN = new Literal(Double.NaN);

    private final double value;

    public Literal(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public double value(IEnvironment env) {
        return value;
    }

    @Override
    public String represent(IEnvironment env) {
        return env.representLiteral(value);
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(IConverter<T> converter) {
        return converter.convert(this);
    }
}
