package org.karatachi.expression;

import java.util.List;

import org.karatachi.expression.ast.Expression;

public interface IEnvironment {

    /**
     * Expression.represent()の文字列表現を展開するかどうか。
     * 変数が展開される
     */
    public boolean isVariableRepresentationExpanded();

    /**
     * Expression.represent()の文字列表現を展開するかどうか。
     * 関数および三項演算子?:が展開される
     */
    public boolean isFunctionRepresentationExpanded();

    /**
     * 変数の値を取得する。
     * 
     * @param name
     *            変数名
     * @return 値
     */
    public double getVariableValue(String name);

    /**
     * 関数を評価する。
     * 
     * @param name
     *            関数名
     * @param args
     *            引数の式
     * @return 評価値
     */
    public double evaluateFunction(String name, List<Expression> args);

    /**
     * 関数を文字列表現にする。
     * 
     * @param name
     *            関数名
     * @param args
     *            引数の式
     * @return 文字列表現
     */
    public String representFunction(String name, List<Expression> args);

    /**
     * 関数の評価後の式にする。
     * 
     * @param name
     *            関数名
     * @param args
     *            引数の式
     * @return 関数の評価後の式
     */
    public Expression extractFunction(String name, List<Expression> args);

    /**
     * 直定数を文字列に変換する。
     * 
     * @param value
     *            数値
     * @return 文字列表現
     */
    public String representLiteral(double value);

    /**
     * 変数を文字列に変換する。
     * 
     * @param name
     *            変数名
     * @param value
     *            変数の値
     * @return 文字列表現
     */
    public String representVariable(String name, double value);

}
