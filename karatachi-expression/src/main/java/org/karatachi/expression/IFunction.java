package org.karatachi.expression;

import java.util.List;

import org.karatachi.expression.ast.Expression;

public interface IFunction {

    /**
     * 関数名の取得。
     * 
     * @return 関数名
     */
    public String getName();

    /**
     * 関数で内部的に使用している変数一覧の取得。
     * 
     * @return 変数一覧
     */
    public List<String> getInternallyUsedVariables();

    /**
     * 関数の評価。
     * 
     * @param env
     *            環境
     * @param args
     *            引数
     * @return 評価値
     */
    public double evaluate(IEnvironment env, List<Double> args);

    /**
     * 関数の文字列表現。
     * 
     * @param env
     *            環境
     * @param args
     *            引数の文字列表現
     * @param values
     *            引数
     * @return 文字列表現
     */
    public String represent(IEnvironment env, List<String> args,
            List<Double> values);

    /**
     * 関数の評価後の式。
     * 
     * @param env
     *            環境
     * @param args
     *            引数の文字列表現
     * @param values
     *            引数
     * @return 評価後の式
     */
    public Expression extract(IEnvironment env, List<Expression> args,
            List<Double> values);

}
