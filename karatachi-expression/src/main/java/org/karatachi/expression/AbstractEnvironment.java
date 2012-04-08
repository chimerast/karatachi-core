package org.karatachi.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Variable;

public abstract class AbstractEnvironment implements IEnvironment {

    /** 文字列表現時に式を展開するか */
    private boolean representationExpanded;
    /** 関数 */
    private Map<String, IFunction> functions = new HashMap<String, IFunction>();

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IEnvironment#isRepresentationExpanded()
     */
    @Override
    public boolean isRepresentationExpanded() {
        return representationExpanded;
    }

    public void setRepresentationExpanded(boolean expanded) {
        this.representationExpanded = expanded;
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IEnvironment#getVariableValue(java.lang.String)
     */
    @Override
    public double getVariableValue(String name) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IEnvironment#evaluateFunction(java.lang.String, java.util.List)
     */
    @Override
    public final double evaluateFunction(String name, List<Expression> args) {
        if (!functions.containsKey(name)) {
            throw new UnknownIdentifierException();
        }

        // 高速化する際は関数内での遅延評価にするべき
        List<Double> evaluated = new ArrayList<Double>();
        for (Expression expression : args) {
            evaluated.add(expression.value(this));
        }
        return functions.get(name).evaluate(this, evaluated);
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IEnvironment#representFunction(java.lang.String, java.util.List)
     */
    @Override
    public final String representFunction(String name, List<Expression> args) {
        if (!functions.containsKey(name)) {
            throw new UnknownIdentifierException();
        }

        // 高速化する際は関数内での遅延評価にするべき
        List<String> represented = new ArrayList<String>();
        for (Expression expression : args) {
            represented.add(expression.represent(this));
        }
        List<Double> evaluated = new ArrayList<Double>();
        for (Expression expression : args) {
            evaluated.add(expression.value(this));
        }

        if (!isRepresentationExpanded()) {
            StringBuilder sb = new StringBuilder();
            for (String arg : represented) {
                sb.append(arg);
                sb.append(", ");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }
            return functions.get(name).getName() + "(" + sb.toString() + ")";
        } else {
            // 展開するときはIFunctionに任せる
            return functions.get(name).represent(this, represented, evaluated);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IEnvironment#representLiteral(double)
     */
    @Override
    public String representLiteral(double value) {
        if (value == (int) value) {
            return String.format("%,.0f", value);
        } else {
            return String.format("%,.2f", value);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.uzabase.speeda.expression.IEnvironment#representVariable(java.lang.String, double)
     */
    @Override
    public String representVariable(String name, double value) {
        return representLiteral(value);
    }

    /**
     * 関数の追加
     * 
     * @param function
     *            追加する関数
     */
    public void addFunction(IFunction function) {
        functions.put(function.getName(), function);
    }

    /**
     * 式に使われている変数を全て列挙。関数で内部的に使われてる変数も取り出す。
     * 
     * @param expression
     *            列挙する式
     * @return 変数一覧
     */
    public Set<String> getUsedVariables(Expression expression) {
        Set<String> ret = getVariables(expression);
        for (String name : getFunctions(expression)) {
            if (!functions.containsKey(name)) {
                throw new UnknownIdentifierException();
            }
            ret.addAll(functions.get(name).getInternallyUsedVariables());
        }
        return ret;
    }

    /*
     * 変数を全て列挙する
     */
    private static Set<String> getVariables(Expression expression) {
        final Set<String> ret = new HashSet<String>();
        expression.accept(new AbstractVisitor() {
            @Override
            public void visit(Variable variable) {
                ret.add(variable.getIdentifier().getName());
            }
        });
        return ret;
    }

    /*
     * 関数を全て列挙する
     */
    private static Set<String> getFunctions(Expression expression) {
        final Set<String> ret = new HashSet<String>();
        expression.accept(new AbstractVisitor() {
            @Override
            public void visit(Function function) {
                ret.add(function.getIdentifier().getName());
            }
        });
        return ret;
    }
}
