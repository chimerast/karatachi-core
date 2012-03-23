package org.karatachi.wicket.auto;

import java.io.Serializable;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class AutoResolveWebMarkupContainer<T extends Serializable> extends
        WebMarkupContainer implements IComponentResolver {
    private static final long serialVersionUID = 1L;

    public AutoResolveWebMarkupContainer(String id) {
        this(id, (T) null);
    }

    public AutoResolveWebMarkupContainer(String id, T object) {
        super(id, new CompoundPropertyModel<T>(object));
        commonInit();
    }

    public AutoResolveWebMarkupContainer(String id, IModel<T> model) {
        super(id, new CompoundPropertyModel<T>(model));
        commonInit();
    }

    private void commonInit() {
        init();
    }

    protected void init() {
    }

    public boolean resolve(MarkupContainer container,
            MarkupStream markupStream, ComponentTag tag) {
        if (tag.isAutoComponentTag()) {
            return false;
        }

        String tagId = tag.getId();
        String type = tag.getAttribute("type");

        String wicketType = null;
        if (tag.getAttributes().containsKey("wicket:type")) {
            String[] wicketTypeValue =
                    tag.getAttribute("wicket:type").split(":");
            wicketType = wicketTypeValue[0];
        }

        ComponentResolver resolver =
                ComponentResolver.getResolver(wicketType, type);

        if (resolver != null) {
            return container.autoAdd(resolver.createViewComponent(tagId),
                    markupStream);
        } else {
            return container.autoAdd(
                    ComponentResolver.getDefaultResolver().createViewComponent(
                            tagId), markupStream);
        }
    }

    @SuppressWarnings("unchecked")
    public final IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    public final T getModelObject() {
        return (T) getDefaultModelObject();
    }

    public final void setModelObject(T object) {
        setDefaultModelObject(object);
    }
}
