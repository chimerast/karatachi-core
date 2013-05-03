package org.karatachi.wicket.monitor.jmx;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.karatachi.jmx.MBeanNode;
import org.karatachi.jmx.MBeanTree.AttributeNode;
import org.karatachi.jmx.MBeanTree.BeanNode;
import org.karatachi.jmx.MBeanTree.DomainNode;
import org.karatachi.jmx.MBeanTree.OperationNode;

public class MBeanLabel extends Panel {
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

    public MBeanLabel(String id, IModel<MBeanNode> model) {
        super(id, model);

        MBeanNode node = model.getObject();

        String label;
        try {
            label = node.getName();
        } catch (Exception e) {
            label = e.getMessage();
        }

        add(new Label("label", label));
        add(new Image("icon", getNodeIcon(node)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean shouldAddAntiCacheParameter() {
                return false;
            }
        });
    }

    private ResourceReference getNodeIcon(MBeanNode node) {
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
            return null;
        }
    }
}
