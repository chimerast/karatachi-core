package org.karatachi.expression.ast;

import org.karatachi.expression.IConverter;
import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public class Variable implements Expression {
    private static final long serialVersionUID = 1L;

    private final Identifier identifier;

    public Variable(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public double value(IEnvironment env) {
        return env.getVariableValue(identifier.getName());
    }

    @Override
    public String represent(IEnvironment env) {
        if (!env.isVariableRepresentationExpanded()) {
            return identifier.getName();
        } else {
            return env.representVariable(identifier.getName(),
                    env.getVariableValue(identifier.getName()));
        }
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
