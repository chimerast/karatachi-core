package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.karatachi.wicket.auto.FormComponentResolver;

public class TextFieldResolver extends FormComponentResolver {
    @Override
    public String getTypeName() {
        return "text";
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new TextField<Object>(id);
    }
}
