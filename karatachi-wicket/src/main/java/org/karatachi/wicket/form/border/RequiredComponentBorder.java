package org.karatachi.wicket.form.border;

import org.apache.wicket.Component;
import org.apache.wicket.IComponentBorder;
import org.apache.wicket.Response;
import org.apache.wicket.markup.html.form.FormComponent;

public class RequiredComponentBorder implements IComponentBorder {
    private static final long serialVersionUID = 1L;

    private final String hintChar;

    public RequiredComponentBorder() {
        this("*");
    }

    public RequiredComponentBorder(String hintChar) {
        this.hintChar = hintChar;
    }

    @Override
    public void renderBefore(Component component) {
    }

    @Override
    public void renderAfter(Component component) {
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
