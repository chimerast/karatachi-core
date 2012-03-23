package org.karatachi.wicket.ajax;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class PromptAppender extends AbstractBehavior {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference JS =
            new ResourceReference(PromptAppender.class, "karatachi-prompt.js");

    private Component component;

    private final String prompt;

    public PromptAppender(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public void bind(Component component) {
        this.component = component;
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavascriptReference(AjaxLibrariesReference.prototype);
        response.renderJavascriptReference(JS);

        response.renderOnDomReadyJavascript(String.format(
                "promptText('%s', '%s')", component.getMarkupId(), prompt));
    }
}
