package org.karatachi.example.web.form;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.border.MarkupComponentBorder;
import org.apache.wicket.markup.html.form.FormComponent;

public class RequiredComponentBorder extends MarkupComponentBorder {
    private static final long serialVersionUID = 1L;

    @Override
    public void renderBefore(Component component) {
        if (component instanceof FormComponent) {
            FormComponent<?> formComponent = (FormComponent<?>) component;
            if (formComponent.isRequired()) {
                super.renderBefore(component);
            }
        }
    }

    @Override
    public void renderAfter(Component component) {
        if (component instanceof FormComponent) {
            FormComponent<?> formComponent = (FormComponent<?>) component;
            if (formComponent.isRequired()) {
                super.renderAfter(component);
            }
        }
    }
}
