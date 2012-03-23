package org.karatachi.wicket.modeless;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ModelessWindow extends Panel {
    private static final long serialVersionUID = 1L;

    public ModelessWindow(String id) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        setVisible(false);
    }

    public abstract Panel createPaenl(String id);

    public void show(AjaxRequestTarget target) {
        addOrReplace();

    }
}
