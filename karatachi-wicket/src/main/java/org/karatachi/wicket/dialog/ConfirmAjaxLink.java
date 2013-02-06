package org.karatachi.wicket.dialog;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public abstract class ConfirmAjaxLink<T> extends AjaxLink<T> {
    private static final long serialVersionUID = 1L;

    private final String message;

    public ConfirmAjaxLink(String id, String message) {
        super(id);
        this.message = message;
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        attributes.getAjaxCallListeners().add(new AjaxCallListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public CharSequence getPrecondition(Component component) {
                return "return confirm('" + message + "');";
            }
        });
    }
}
