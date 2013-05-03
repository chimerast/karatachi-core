package org.karatachi.wicket.monitor.jmx;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.karatachi.jmx.MBeanNode;

class ValueColumn extends AbstractColumn<MBeanNode, Void> {
    private static final long serialVersionUID = 1L;

    public ValueColumn() {
        super(Model.of("Value"));
    }

    @Override
    public void populateItem(Item<ICellPopulator<MBeanNode>> cellItem,
            String componentId, final IModel<MBeanNode> rowModel) {
        MBeanNode node = rowModel.getObject();

        Component component =
                new Label(componentId, new PropertyModel<String>(node, "value"));

        cellItem.add(component);
    }
}
