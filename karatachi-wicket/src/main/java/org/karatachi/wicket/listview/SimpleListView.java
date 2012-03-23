package org.karatachi.wicket.listview;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class SimpleListView<T> extends ListView<T> implements
        IComponentResolver {
    private static final long serialVersionUID = 1L;

    public static String EVEN = "even";
    public static String ODD = "odd";

    public SimpleListView(String id) {
        super(id);
    }

    public SimpleListView(String id, List<T> data) {
        super(id, data);
    }

    public SimpleListView(String id, IModel<List<T>> model) {
        super(id, model);
    }

    @Override
    protected ListItem<T> newItem(int index) {
        final ListItem<T> ret = super.newItem(index);
        ret.add(new AttributeAppender("class",
                new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return getClassAttribute(ret.getId(), ret.getIndex(),
                                ret.getModelObject());
                    }
                }, " "));
        return ret;
    }

    protected String getClassAttribute(String id, int index, T modelObject) {
        return (index % 2) == 0 ? EVEN : ODD;
    }

    @Override
    protected IModel<T> getListItemModel(
            IModel<? extends List<T>> listViewModel, int index) {
        return new CompoundPropertyModel<T>(super.getListItemModel(
                listViewModel, index));
    }

    @Override
    public boolean resolve(MarkupContainer container,
            MarkupStream markupStream, ComponentTag tag) {
        if (tag.isAutoComponentTag()) {
            return false;
        }
        return container.autoAdd(new Label(tag.getId()), markupStream);
    }

    @Override
    protected void populateItem(final ListItem<T> item) {
        ;
    }
}
