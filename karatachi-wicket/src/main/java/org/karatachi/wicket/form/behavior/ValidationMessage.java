package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.form.FormComponent;

public class ValidationMessage extends AbstractBehavior {
    private static final long serialVersionUID = 1L;

    @Override
    public void onRendered(Component component) {
        FormComponent<?> formComponent = (FormComponent<?>) component;
        if (!formComponent.isValid()) {
            String error;
            if (formComponent.hasFeedbackMessage()) {
                error =
                        formComponent.getFeedbackMessage().getMessage().toString();
            } else {
                error = component.getString(getClass().getName());
            }
            formComponent.getResponse().write(
                    "<div class=\"component_feedback\">" + error + "</div>");
        }
    }
}
