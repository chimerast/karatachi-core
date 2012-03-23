package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.Response;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.form.FormComponent;

public class RequiredComponent extends AbstractBehavior {
    private static final long serialVersionUID = 1L;

    private final String hintChar;

    public RequiredComponent() {
        this("*");
    }

    public RequiredComponent(String hintChar) {
        this.hintChar = hintChar;
    }

    @Override
    public void onRendered(Component component) {
        if (component instanceof FormComponent) {
            FormComponent<?> formComponent = (FormComponent<?>) component;
            if (formComponent.isRequired()) {
                Response response = component.getResponse();
                response.write("<span class=\"required_hint\">" + hintChar
                        + "</span>");
            }
        }
    }
}
