package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class Placeholder extends Behavior {
    private static final long serialVersionUID = 1L;

    private final IModel<String> model;

    public Placeholder(String prompt) {
        this(new Model<String>(prompt));
    }

    public Placeholder(IModel<String> model) {
        this.model = model;
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (this.isEnabled(component)) {
            tag.getAttributes().put("placeholder", model.getObject());
        }
    }

    @Override
    public void detach(Component component) {
        if (model != null) {
            model.detach();
        }
    }
}
