package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.FormComponent;

public class ValidationMessage extends Behavior {
    private static final long serialVersionUID = 1L;

    @Override
    public void afterRender(Component component) {
        FormComponent<?> formComponent = (FormComponent<?>) component;
        if (!formComponent.isValid()) {
            String error;
            if (formComponent.hasFeedbackMessage()) {
                StringBuffer sb = new StringBuffer();
                for (FeedbackMessage message : formComponent.getFeedbackMessages()) {
                    sb.append(message.getMessage());
                }
                error = sb.toString();
            } else {
                error = component.getString(getClass().getName());
            }
            formComponent.getResponse().write(
                    "<div class=\"component_feedback\">" + error + "</div>");
        }
    }
}
