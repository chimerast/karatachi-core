package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

public class ErrorHighlightBehavior extends AbstractBehavior {
    private static final long serialVersionUID = 1L;

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        FormComponent<?> formComponent = (FormComponent<?>) component;
        if (!formComponent.isValid()) {
            tag.put("class", "component_error");
        }
    }
}
