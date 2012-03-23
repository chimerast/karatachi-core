package org.karatachi.wicket.auto.resolver;

import org.apache.wicket.markup.html.form.FormComponent;
import org.karatachi.wicket.auto.ChoiceFormComponentResolver;
import org.karatachi.wicket.form.field.EnumRadioChoice;

public class RadioChoiceResolver<E extends Enum<E>> extends
        ChoiceFormComponentResolver<E> {
    @Override
    public String getTypeName() {
        return "radio";
    }

    @Override
    public FormComponent<?> createFormComponent(String id) {
        EnumRadioChoice<E> ret = new EnumRadioChoice<E>(id, elementType);
        ret.setPrefix("").setSuffix("");
        ret.setRenderBodyOnly(true);
        return ret;
    }
}
