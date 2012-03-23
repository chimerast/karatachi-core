package org.karatachi.wicket.panel;

import org.apache.wicket.markup.html.panel.Panel;

public class TransparentPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public TransparentPanel(String id) {
        super(id);
        setRenderBodyOnly(true);
    }

    @Override
    public boolean isTransparentResolver() {
        return true;
    }
}
