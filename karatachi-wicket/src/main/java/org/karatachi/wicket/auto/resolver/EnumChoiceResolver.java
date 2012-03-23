package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.markup.html.form.FormComponent;
import org.karatachi.wicket.auto.ChoiceFormComponentResolver;
import org.karatachi.wicket.form.field.EnumDropDownChoice;

public class EnumChoiceResolver<E extends Enum<E>> extends
        ChoiceFormComponentResolver<E> {
    @Override
    public String getTypeName() {
        return "enum";
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        return new EnumDropDownChoice<E>(id, elementType);
    }
}
