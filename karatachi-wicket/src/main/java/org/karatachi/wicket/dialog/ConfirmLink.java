package org.karatachi.wicket.dialog;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.link.Link;

public abstract class ConfirmLink<T> extends Link<T> {
    private static final long serialVersionUID = 1L;

    public ConfirmLink(String id, String message) {
        super(id);
        add(new SimpleAttributeModifier("onclick", "if (!confirm('" + message
                + "')) return false;"));
    }
}
