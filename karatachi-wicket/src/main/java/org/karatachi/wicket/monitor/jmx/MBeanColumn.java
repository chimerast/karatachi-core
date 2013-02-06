package org.karatachi.wicket.monitor.jmx;

import java.io.Serializable;
import java.util.Arrays;

import javax.management.JMException;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableChoiceLabel;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.AbstractTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;
import org.apache.wicket.extensions.markup.html.tree.table.IRenderable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.karatachi.jmx.MBeanNode;
import org.karatachi.jmx.MBeanTree.AttributeNode;
import org.karatachi.jmx.MBeanTree.OperationNode;

class NameColumn extends AbstractTreeColumn {
    private static final long serialVersionUID = 1L;

    public NameColumn() {
        super(new ColumnLocation(Alignment.LEFT, 36, Unit.EM), "Name");
    }

    @Override
    public String renderNode(TreeNode node) {
        MBeanNode n = (MBeanNode) node;
        return StringUtils.abbreviate(n.getName(), 40);
    }
}

class ValueColumn extends AbstractColumn {
    private static final long serialVersionUID = 1L;

    public ValueColumn() {
        super(new ColumnLocation(Alignment.MIDDLE, 30, Unit.PROPORTIONAL),
                "Value");
    }

    public Component newCell(MarkupContainer parent, String id, TreeNode node,
            int level) {
        Component component = null;
        if (node instanceof OperationNode) {
            final OperationNode operation = (OperationNode) node;
            if (operation.isInvokable()) {
                component = new MBeanInvokeLinkPanel<Serializable>(id, "Exec") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Serializable onSubmit() {
                        try {
                            return (Serializable) operation.invoke();
                        } catch (JMException e) {
                            return "error: " + e.getMessage();
                        }
                    }
                };
            }
        } else if (node instanceof AttributeNode) {
            final AttributeNode attribute = (AttributeNode) node;
            if (attribute.isEditable() && attribute.getType().equals("boolean")) {
                component =
                        new AjaxEditableChoiceLabel<Boolean>(id,
                                new PropertyModel<Boolean>(attribute, "value"),
                                Arrays.asList(new Boolean[] { Boolean.TRUE,
                                        Boolean.FALSE }));
            } else {
                component =
                        new Label(id, new LoadableDetachableModel<Object>() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            protected Object load() {
                                try {
                                    return attribute.getValue();
                                } catch (Exception e) {
                                    return "N/A";
                                }
                            }
                        });
            }
        }

        if (component == null) {
            component = new Label(id, new PropertyModel<Object>(node, "value"));
        }
        parent.add(component);
        return component;
    }

    public IRenderable newCell(TreeNode node, int level) {
        return null;
    }
}
