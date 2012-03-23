package org.karatachi.wicket.ajax;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class ProgressBar extends Panel implements IHeaderContributor {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference CSS =
            new ResourceReference(ProgressBar.class, "karatachi-progress.css");

    private final int width;

    private WebMarkupContainer bar;
    private Label label;

    public ProgressBar(String id, int width) {
        this(id, null, width);
    }

    public ProgressBar(String id, IModel<Double> model, int width) {
        super(id, model);

        this.width = width;

        add(new AttributeModifier("style", true, new Model<String>(
                String.format("width: %dpx", this.width))));
        add(new AjaxSelfUpdatingTimerBehavior(Duration.ONE_SECOND) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                target.prependJavascript(String.format(
                        "bar_%1$s_old = $('%1$s').style.width;",
                        bar.getMarkupId()));
                target.appendJavascript(String.format(
                        "bar_%1$s_new = $('%1$s').style.width;"
                                + "$('%1$s').style.width = bar_%1$s_old;"
                                + "$('%1$s').morph('width : ' + bar_%1$s_new);",
                        bar.getMarkupId()));
                onProcessTarget(target);
            }
        });

        bar = new WebMarkupContainer("bar");
        bar.setOutputMarkupId(true);
        bar.add(new AttributeModifier("style", true, new PropertyModel<String>(
                this, "barStyle")));
        add(bar);

        label = new Label("label", new PropertyModel<String>(this, "label"));
        label.setOutputMarkupId(true);
        add(label);
    }

    public double getModelObject() {
        double value = (Double) getDefaultModelObject();
        if (value < 0.0) {
            return 0.0;
        } else if (value > 1.0) {
            return 1.0;
        } else {
            return value;
        }
    }

    public String getBarStyle() {
        return String.format("width: %dpx", (int) (width * getModelObject()));
    }

    public String getLabel() {
        return String.format("%.1f%%", 100 * getModelObject());
    }

    protected void onProcessTarget(AjaxRequestTarget target) {
    }

    public void renderHead(IHeaderResponse response) {
        response.renderJavascriptReference(AjaxLibrariesReference.prototype);
        response.renderJavascriptReference(AjaxLibrariesReference.scriptaculous);
        response.renderCSSReference(CSS);
    }
}
