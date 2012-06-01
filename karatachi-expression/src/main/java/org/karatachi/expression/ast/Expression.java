package org.karatachi.expression.ast;

import java.io.Serializable;

import org.karatachi.expression.IConverter;
import org.karatachi.expression.IEnvironment;
import org.karatachi.expression.IVisitor;

public interface Expression extends Serializable {

    /**
     * 式を評価して値を返す。
     * 
     * @param env
     *            環境
     * @return 値
     */
    public double value(IEnvironment env);

    /**
     * 式の文字列表現を返す
     * 
     * @param env
     *            環境
     * @return 文字列
     */
    public String represent(IEnvironment env);

    /**
     * Visitor。子を再帰的にたどる。
     * 
     * @param visitor
     *            Visitor
     */
    public void accept(IVisitor visitor);

    /**
     * Converter。converterを呼び出し新しい木を返す。
     * 
     * @param converter
     * @return
     */
    public <T> T accept(IConverter<T> converter);
}
