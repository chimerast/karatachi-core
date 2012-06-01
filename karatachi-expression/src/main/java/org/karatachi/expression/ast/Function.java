package org.karatachi.expression.ast;

import java.util.Collections;
import java.util.List;

import org.karatachi.expression.IConverter;
import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public class Function implements Expression {
    private static final long serialVersionUID = 1L;

    private final Identifier identifier;
    private final List<Expression> arguments;

    public Function(Identifier identifier, List<Expression> arguments) {
        this.identifier = identifier;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public double value(IEnvironment env) {
        return env.evaluateFunction(identifier.getName(), arguments);
    }

    @Override
    public String represent(IEnvironment env) {
        return env.representFunction(identifier.getName(), arguments);
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
        for (Expression arg : arguments) {
            arg.accept(visitor);
        }
    }

    @Override
    public <T> T accept(IConverter<T> converter) {
        return converter.convert(this);
    }
}
