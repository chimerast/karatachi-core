package org.karatachi.wicket.monitor.jmx;

import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.extensions.markup.html.repeater.util.TreeModelProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.karatachi.jmx.MBeanNode;
import org.karatachi.jmx.MBeanServerWrapper;
import org.karatachi.jmx.MBeanTree;

public class MBeanTreeTable extends TableTree<MBeanNode, Void> {
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

    private static final List<AbstractColumn<MBeanNode, Void>> COLUMNS =
            Arrays.asList(new TreeColumn<MBeanNode, Void>(Model.of("Name")),
                    new ValueColumn());

    public MBeanTreeTable(String id) {
        super(id, COLUMNS, new TreeModelProvider<MBeanNode>(
                new DefaultTreeModel(new MBeanTree(new MBeanServerWrapper())),
                false) {
            private static final long serialVersionUID = 1L;

            @Override
            public IModel<MBeanNode> model(MBeanNode object) {
                return Model.of(object);
            }
        }, Integer.MAX_VALUE);

        add(new WindowsTheme());

        getTable().addTopToolbar(new HeadersToolbar<Void>(getTable(), null));
        getTable().addBottomToolbar(new NoRecordsToolbar(getTable()));
    }

    @Override
    protected Component newContentComponent(String id, IModel<MBeanNode> model) {
        String name;
        try {
            name = model.getObject().getName();
        } catch (Exception e) {
            name = "N/A";
        }
        return new Label(id, StringUtils.abbreviate(name, 40));
    }
    /*
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
      */
}
