package org.karatachi.wicket.monitor.jmx;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class MBeanInvokeLinkPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Label result;

    public MBeanInvokeLinkPanel(String id) {
        super(id, new Model<String>());

        add(result = new Label("result", getModel()));
        result.setOutputMarkupId(true);

        add(new AjaxLink<String>("link", getModel()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setModelObject(MBeanInvokeLinkPanel.this.onSubmit());
                target.add(result);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public final IModel<String> getModel() {
        return (IModel<String>) getDefaultModel();
    }

    public abstract String onSubmit();
}
