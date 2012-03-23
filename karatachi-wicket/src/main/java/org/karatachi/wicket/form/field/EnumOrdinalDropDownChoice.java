package org.karatachi.wicket.form.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

public class EnumOrdinalDropDownChoice<E extends Enum<E>> extends
        DropDownChoice<Integer> {
    private static final long serialVersionUID = 1L;

    public EnumOrdinalDropDownChoice(String id, Class<E> elementType) {
        super(id);
        commonInit(elementType);
    }

    public EnumOrdinalDropDownChoice(String id, IModel<Integer> model,
            Class<E> elementType) {
        super(id);
        setModel(model);
        commonInit(elementType);
    }

    public void commonInit(final Class<E> elementType) {
        ArrayList<Integer> choices = new ArrayList<Integer>();

        Collection<E> elements = EnumSet.allOf(elementType);
        if (Comparable.class.isAssignableFrom(elementType)) {
            List<E> temp = new ArrayList<E>(elements);
            Collections.sort(temp);
            elements = temp;
        }

        for (E e : elements) {
            if (!e.name().startsWith("N_")) {
                choices.add(e.ordinal());
            }
        }
        setChoices(choices);

        setChoiceRenderer(new ChoiceRenderer<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Integer object) {
                for (E e : EnumSet.allOf(elementType)) {
                    if (e.ordinal() == object) {
                        return e;
                    }
                }
                return null;
            }
        });
    }
}
