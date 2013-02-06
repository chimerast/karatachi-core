package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.Response;

public class RequiredComponent extends Behavior {
    private static final long serialVersionUID = 1L;

    private final String hintChar;

    public RequiredComponent() {
        this("*");
    }

    public RequiredComponent(String hintChar) {
        this.hintChar = hintChar;
    }

    @Override
    public void afterRender(Component component) {
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
