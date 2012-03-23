package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.karatachi.wicket.auto.FormComponentResolver;

public class PasswordTextFieldResolver extends FormComponentResolver {
    @Override
    public String getTypeName() {
        return "password";
    }

    @Override
    public Component createViewComponent(String id) {
        return new Label(id, "********");
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new PasswordTextField(id);
    }
}
