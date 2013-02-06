package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.string.StringList;

public class ValidationMessage extends Behavior {
    private static final long serialVersionUID = 1L;

    @Override
    public void afterRender(Component component) {
        FormComponent<?> formComponent = (FormComponent<?>) component;
        if (!formComponent.isValid()) {
            String error;
            if (formComponent.hasFeedbackMessage()) {
                error =
                        StringList.valueOf(
                                formComponent.getFeedbackMessages().toList()).toString();
            } else {
                error = component.getString(getClass().getName());
            }
            formComponent.getResponse().write(
                    "<div class=\"component_feedback\">" + error + "</div>");
        }
    }
}
