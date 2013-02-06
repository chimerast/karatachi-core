package org.karatachi.wicket.panel;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.resolver.IComponentResolver;

public class TransparentPanel extends Panel implements IComponentResolver {
    private static final long serialVersionUID = 1L;

    public TransparentPanel(String id) {
        super(id);
        setRenderBodyOnly(true);
    }

    @Override
    public Component resolve(MarkupContainer container,
            MarkupStream markupStream, ComponentTag tag) {
        Component resolvedComponent = getParent().get(tag.getId());
        if (resolvedComponent != null
                && getPage().wasRendered(resolvedComponent)) {
            return null;
        }
        return resolvedComponent;
    }
}
