package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.karatachi.wicket.auto.FormComponentResolver;

public class CheckBoxResolver extends FormComponentResolver {
    @Override
    public String getTypeName() {
        return "checkbox";
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new CheckBox(id);
    }
}
