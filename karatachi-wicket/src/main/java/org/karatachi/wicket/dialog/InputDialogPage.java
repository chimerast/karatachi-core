package org.karatachi.wicket.dialog;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class InputDialogPage extends WebPage implements IHeaderContributor {
    private static final long serialVersionUID = 1L;

    private TextField<String> input;

    public InputDialogPage(InputDialogParams params, Form<Void> form) {
        super(new CompoundPropertyModel<InputDialogParams>(params));
        add(form);

        add(new Label("message").setVisible(params.message != null));

        form.add(input = new TextField<String>("input"));
        input.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery));
        response.render(OnDomReadyHeaderItem.forScript(String.format(
                "jQuery('#%s').focus();", input.getMarkupId())));
    }
}
