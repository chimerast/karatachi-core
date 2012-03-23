package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.karatachi.wicket.auto.FormComponentResolver;
import org.karatachi.wicket.form.field.DataSizeField;
import org.karatachi.wicket.label.DataSizeLabel;

public class DataSizeFieldResolver extends FormComponentResolver {
    @Override
    public String getTypeName() {
        return "datasize";
    }

    @Override
    public Component createViewComponent(String id) {
        return new DataSizeLabel(id);
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new DataSizeField(id);
    }
}
