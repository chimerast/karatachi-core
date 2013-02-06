package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

public class CheckBoxLabel extends Behavior {
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
    public void afterRender(Component component) {
        String format = " <label for=\"%s\" style=\"%s\">%s</label>";
        component.getResponse().write(
                String.format(format, component.getMarkupId(),
                        component.isEnabled() ? "" : "color: #AAAAAA", label));
    }
}
