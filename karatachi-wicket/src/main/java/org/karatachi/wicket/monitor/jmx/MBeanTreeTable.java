package org.karatachi.wicket.monitor.jmx;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.karatachi.jmx.MBeanNode;
import org.karatachi.jmx.MBeanServerWrapper;
import org.karatachi.jmx.MBeanTree;
import org.karatachi.jmx.MBeanTree.AttributeNode;
import org.karatachi.jmx.MBeanTree.BeanNode;
import org.karatachi.jmx.MBeanTree.DomainNode;
import org.karatachi.jmx.MBeanTree.OperationNode;

public class MBeanTreeTable extends TableTree {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference ICON_DOMAIN =
            new PackageResourceReference(MBeanTreeTable.class, "domain.png");
    private static final ResourceReference ICON_BEAN =
            new PackageResourceReference(MBeanTreeTable.class, "bean.png");
    private static final ResourceReference ICON_ATTRIBUTE =
            new PackageResourceReference(MBeanTreeTable.class, "attribute.png");
    private static final ResourceReference ICON_ATTRIBUTE_EDIT =
            new PackageResourceReference(MBeanTreeTable.class,
                    "attribute_edit.png");
    private static final ResourceReference ICON_OPERATION =
            new PackageResourceReference(MBeanTreeTable.class, "operation.png");

    public MBeanTreeTable(String id) {
        super(id, new IColumn[] { new NameColumn(), new ValueColumn() });
        setDefaultModel(new Model<DefaultTreeModel>(new DefaultTreeModel(
                new MBeanTree(new MBeanServerWrapper()))));
        setRootLess(true);
    }

    @Override
    protected MarkupContainer newNodeLink(MarkupContainer parent, String id,
            TreeNode node) {
        MarkupContainer ret = super.newNodeLink(parent, id, node);
        ret.add(new SimpleAttributeModifier("title",
                ((MBeanNode) node).getName()));
        return ret;
    }

    @Override
    protected ResourceReference getNodeIcon(TreeNode node) {
        if (node instanceof DomainNode) {
            return ICON_DOMAIN;
        } else if (node instanceof BeanNode) {
            return ICON_BEAN;
        } else if (node instanceof AttributeNode) {
            if (((AttributeNode) node).isEditable()) {
                return ICON_ATTRIBUTE_EDIT;
            } else {
                return ICON_ATTRIBUTE;
            }
        } else if (node instanceof OperationNode) {
            return ICON_OPERATION;
        } else {
            return super.getNodeIcon(node);
        }
    }
}
