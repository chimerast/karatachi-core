package org.karatachi.expression;

import java.util.List;

import org.karatachi.expression.ast.Expression;

public abstract class AbstractFunction implements IFunction {

    /** 関数名 */
    private final String name;

    public AbstractFunction(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IFunction#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IFunction#getInternallyUsedVariables()
     */
    @Override
    public List<String> getInternallyUsedVariables() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IFunction#represent(com.uzabase.speeda.expression.IEnvironment, java.util.List)
     */
    @Override
    public String represent(IEnvironment env, List<String> args,
            List<Double> values) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see org.karatachi.expression.IFunction#extract(org.karatachi.expression.IEnvironment, java.util.List, java.util.List)
     */
    @Override
    public Expression extract(IEnvironment env, List<Expression> args,
            List<Double> values) {
        throw new UnsupportedOperationException();
    }
}
