package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.karatachi.wicket.auto.FormComponentResolver;

public class TextAreaResolver extends FormComponentResolver {
    @Override
    public String getTypeName() {
        return "textarea";
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new TextArea<Object>(id);
    }
}
