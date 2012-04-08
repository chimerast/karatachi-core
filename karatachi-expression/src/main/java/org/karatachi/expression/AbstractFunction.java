package org.karatachi.expression;

import java.util.List;

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
}
