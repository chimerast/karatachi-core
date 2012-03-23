package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;

public class CheckBoxLabel extends AbstractBehavior {
    private static final long serialVersionUID = 1L;

    private String label;

    public CheckBoxLabel(String label) {
        this.label = label;
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void onRendered(Component component) {
        String format = " <label for=\"%s\" style=\"%s\">%s</label>";
        component.getResponse().write(
                String.format(format, component.getMarkupId(),
                        component.isEnabled() ? "" : "color: #AAAAAA", label));
    }
}
