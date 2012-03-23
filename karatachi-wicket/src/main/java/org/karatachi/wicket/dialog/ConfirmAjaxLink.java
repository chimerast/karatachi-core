package org.karatachi.wicket.dialog;

import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxPreprocessingCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public abstract class ConfirmAjaxLink<T> extends AjaxLink<T> {
    private static final long serialVersionUID = 1L;

    private final String message;

    public ConfirmAjaxLink(String id, String message) {
        super(id);
        this.message = message;
    }

    @Override
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return new AjaxPreprocessingCallDecorator(super.getAjaxCallDecorator()) {
            private static final long serialVersionUID = 1L;

            @Override
            public CharSequence preDecorateScript(CharSequence script) {
                return "if (!confirm('" + message + "')) return false;"
                        + script;
            }
        };
    }
}
