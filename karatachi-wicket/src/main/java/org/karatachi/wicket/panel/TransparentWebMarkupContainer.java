package org.karatachi.wicket.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;

public class TransparentWebMarkupContainer extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    public TransparentWebMarkupContainer(String id) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public boolean isTransparentResolver() {
        return true;
    }
}
