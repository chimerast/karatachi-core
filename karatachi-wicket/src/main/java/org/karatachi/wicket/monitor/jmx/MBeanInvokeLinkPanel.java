package org.karatachi.wicket.monitor.jmx;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class MBeanInvokeLinkPanel<T extends Serializable> extends
        Panel {
    private static final long serialVersionUID = 1L;

    private Label result;

    public MBeanInvokeLinkPanel(String id, String label) {
        super(id, new Model<T>());

        add(result = new Label("result", getDefaultModel()));
        result.setOutputMarkupId(true);

        AjaxLink<T> link;
        add(link = new AjaxLink<T>("link", getModel()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setModelObject(MBeanInvokeLinkPanel.this.onSubmit());
                target.addComponent(result);
            }
        });

        link.add(new Label("label", label));
    }

    @SuppressWarnings("unchecked")
    public final IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    public abstract T onSubmit();
}
