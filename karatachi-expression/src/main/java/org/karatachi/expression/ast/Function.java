package org.karatachi.expression.ast;

import java.util.Collections;
import java.util.List;

import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public class Function implements Expression {
    private static final long serialVersionUID = 1L;

    private final Identifier identifier;
    private final List<Expression> args;

    public Function(Identifier identifier, List<Expression> args) {
        this.identifier = identifier;
        this.args = Collections.unmodifiableList(args);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public double value(IEnvironment env) {
        return env.evaluateFunction(identifier.getName(), args);
    }

    @Override
    public String represent(IEnvironment env) {
        return env.representFunction(identifier.getName(), args);
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        for (Expression arg : args) {
            arg.accept(visitor);
        }
    }
}
