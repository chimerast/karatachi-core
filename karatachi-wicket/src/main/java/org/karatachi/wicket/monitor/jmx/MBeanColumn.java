package org.karatachi.wicket.monitor.jmx;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableChoiceLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.karatachi.jmx.MBeanNode;
import org.karatachi.jmx.MBeanTree.AttributeNode;
import org.karatachi.jmx.MBeanTree.OperationNode;

/*
 * public NameColumn() {
 * super(new ColumnLocation(Alignment.LEFT, 36, Unit.EM), "Name");
 * }
 * public ValueColumn() {
 * super(new ColumnLocation(Alignment.MIDDLE, 30, Unit.PROPORTIONAL),
 * "Value");
 * }
 */
class NameColumn extends AbstractColumn<MBeanNode, Void> {
    private static final long serialVersionUID = 1L;

    public NameColumn() {
        super(Model.of("Name"));
    }

    @Override
    public void populateItem(Item<ICellPopulator<MBeanNode>> cellItem,
            String componentId, IModel<MBeanNode> rowModel) {
        MBeanNode node = rowModel.getObject();

        cellItem.add(new Label(componentId, StringUtils.abbreviate(
                node.getName(), 40)));
    }
}

class ValueColumn extends AbstractColumn<MBeanNode, Void> {
    private static final long serialVersionUID = 1L;

    public ValueColumn() {
        super(Model.of("Value"));
    }

    @Override
    public void populateItem(Item<ICellPopulator<MBeanNode>> cellItem,
            String componentId, final IModel<MBeanNode> rowModel) {
        MBeanNode node = rowModel.getObject();

        Component component = null;
        if (node instanceof OperationNode) {
            final OperationNode operation = (OperationNode) node;
            if (operation.isInvokable()) {
                component = new MBeanInvokeLinkPanel(componentId) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String onSubmit() {
                        try {
                            return operation.invoke().toString();
                        } catch (Exception e) {
                            return "error: " + e.getMessage();
                        }
                    }
                };
            }
        } else if (node instanceof AttributeNode) {
            final AttributeNode attribute = (AttributeNode) node;
            if (attribute.isEditable() && attribute.getType().equals("boolean")) {
                component =
                        new AjaxEditableChoiceLabel<Boolean>(componentId,
                                new PropertyModel<Boolean>(attribute, "value"),
                                Arrays.asList(new Boolean[] { Boolean.TRUE,
                                        Boolean.FALSE }));
            }
        }

        if (component == null) {
            component =
                    new Label(componentId, new PropertyModel<String>(node,
                            "value"));
        }

        cellItem.add(component);
    }
}
