package org.karatachi.wicket.panel;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CheckTreePanel<T extends Serializable & Comparable<? super T>>
        extends Panel {
    private static final long serialVersionUID = 1L;

    public CheckTreePanel(String id, IModel<CheckTreeNode<T>> root) {
        super(id, new CompoundPropertyModel<CheckTreeNode<T>>(root));

        add(new ListView<CheckTreeNode<T>>("children") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<CheckTreeNode<T>> item) {
                item.add(new CheckableNode("child", item.getModelObject()));
            }
        });
    }

    private class CheckableNode extends Panel {
        private static final long serialVersionUID = 1L;

        private final WebMarkupContainer container;

        public CheckableNode(String id, final CheckTreeNode<T> node) {
            super(id, new CompoundPropertyModel<CheckTreeNode<T>>(node));

            setVisible(node.isVisible());

            final Component check;
            add(check =
                    new CheckBox("check").add(new AjaxFormComponentUpdatingBehavior(
                            "onclick") {
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            if (!node.isLeaf()) {
                                node.accept(new CheckTreeNode.Visitor<T>() {
                                    @Override
                                    public boolean visit(CheckTreeNode<T> n) {
                                        if (n.isVisible()) {
                                            n.setCheck(node.isCheck());
                                        }
                                        return true;
                                    }
                                });
                                target.add(container);
                            }
                            CheckTreePanel.this.onUpdate(target);
                        }
                    }));

            AjaxLink<CheckTreeNode<T>> link;
            add(link =
                    new AjaxLink<CheckTreeNode<T>>("link",
                            new Model<CheckTreeNode<T>>(node)) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            CheckTreeNode<T> node = getModelObject();
                            if (!node.isLeaf()) {
                                node.setOpen(!node.isOpen());
                                container.setVisible(node.isOpen());
                                target.add(container);
                            } else {
                                node.setCheck(!node.isCheck());
                                target.add(check);
                                CheckTreePanel.this.onUpdate(target);
                            }
                        }
                    });
            link.add(new Label("value", renderValue(node.getValue())));

            add(container = new WebMarkupContainer("container"));
            container.add(new ListView<CheckTreeNode<T>>("children") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(ListItem<CheckTreeNode<T>> item) {
                    item.add(new CheckableNode("child", item.getModelObject()));
                }
            });
            container.setVisible(node.isOpen());
            container.setOutputMarkupId(true);
            container.setOutputMarkupPlaceholderTag(true);
        }
    }

    protected void onUpdate(AjaxRequestTarget target) {
    }

    protected String renderValue(T value) {
        return value.toString();
    }
}
