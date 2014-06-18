package org.karatachi.wicket.grid;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;


/**
 * セルを表すコンポーネントを構築するためのインタフェース。
 * ICellオブジェクトがこのインタフェースを実装している場合、FixedGridは、コンポーネントの生成をICellオブジェクトに
 * 委譲します。<br>
 * ICellがこのインタフェースを実装していない場合は、FixedGridはデフォルトのコンポーネントとして{@link Label}を構築します。
 * 
 * @author t_yano
 * @since 0.5.17
 */
public interface IComponentBuilder {
    Component createComponent(String id, int row, int col, String cellValue);
}
