package org.karatachi.expression.ast;

import java.io.Serializable;

import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public interface Expression extends Serializable {

    public double value(IEnvironment env);

    public String represent(IEnvironment env);

    public void accept(IVisitor visitor);
}
