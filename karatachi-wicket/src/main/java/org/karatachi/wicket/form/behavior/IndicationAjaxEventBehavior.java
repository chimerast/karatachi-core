package org.karatachi.wicket.form.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;

public abstract class IndicationAjaxEventBehavior extends AjaxEventBehavior {
    private static final long serialVersionUID = 1L;

    private final Component field;
    private final String markupId;
    private final ResourceReference indicator;

    public IndicationAjaxEventBehavior(String event, Component field,
            ResourceReference indicator) {
        super(event);
        this.field = field;
        this.field.setOutputMarkupId(true);
        this.markupId = this.field.getMarkupId();
        this.indicator = indicator;
    }

    @Override
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return new AjaxCallDecorator() {
            private static final long serialVersionUID = 1L;

            @Override
            public CharSequence decorateScript(CharSequence script) {
                return String.format("showIndicator('%s', '%s');", markupId,
                        markupId + "_indicator")
                        + super.decorateScript(script);
            }
        };
    }

    @Override
    protected void onComponentRendered() {
        Response response = getComponent().getResponse();
        response.write(String.format(
                "<table id=\"%s\" style=\"display: none; position: absolute; overflow: hidden; z-index: 99; background: white; -moz-opacity: 0.7; filter:alpha(opacity=70);\">",
                markupId + "_indicator"));
        response.write(String.format(
                "  <tr><td style=\"text-align: center; vertical-align: middle; border: none 0px transparent\"><img src=\"%s\" /></td></tr>",
                getComponent().urlFor(indicator)));
        response.write("</table>");
    }
}
