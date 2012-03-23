package org.karatachi.wicket.auto.resolver;

import java.text.DateFormat;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.karatachi.wicket.auto.FormComponentResolver;
import org.karatachi.wicket.form.field.DateField;
import org.karatachi.wicket.label.DateLabel;

public class DateFieldResolver extends FormComponentResolver {
    @Override
    public String getTypeName() {
        return "date";
    }

    @Override
    public Component createViewComponent(String id) {
        return new DateLabel(id, DateFormat.FULL, -1);
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new DateField(id);
    }
}
