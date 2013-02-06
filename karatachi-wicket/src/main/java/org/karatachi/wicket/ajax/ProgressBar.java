package org.karatachi.wicket.ajax;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Duration;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class ProgressBar extends Panel implements IHeaderContributor {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference CSS = new CssResourceReference(
            ProgressBar.class, "karatachi-progress.css");

    private final int width;

    private WebMarkupContainer bar;
    private Label label;

    public ProgressBar(String id, int width) {
        this(id, null, width);
    }

    public ProgressBar(String id, IModel<Double> model, int width) {
        super(id, model);

        this.width = width;

        add(new AttributeModifier("style", new Model<String>(String.format(
                "width: %dpx", this.width))));
        add(new AjaxSelfUpdatingTimerBehavior(Duration.ONE_SECOND) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                target.prependJavaScript(String.format(
                        "bar_%1$s_old = jQuery('#%1$s').width();",
                        bar.getMarkupId()));
                target.appendJavaScript(String.format(
                        "bar_%1$s_new = jQuery('#%1$s').width();"
                                + "jQuery('#%1$s').width(bar_%1$s_old);"
                                + "jQuery('#%1$s').animate({width: bar_%1$s_new + 'px'}, 500, 'easeInOutQuart');",
                        bar.getMarkupId()));
                onProcessTarget(target);
            }
        });

        bar = new WebMarkupContainer("bar");
        bar.setOutputMarkupId(true);
        bar.add(new AttributeModifier("style", new PropertyModel<String>(this,
                "barStyle")));
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

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
        response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery));
        response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery_easing));
    }
}
