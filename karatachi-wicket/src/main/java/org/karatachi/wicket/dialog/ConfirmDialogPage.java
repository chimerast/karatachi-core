package org.karatachi.wicket.dialog;

import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.CompoundPropertyModel;

public class ConfirmDialogPage extends WebPage implements IHeaderContributor {
    private static final long serialVersionUID = 1L;

    public ConfirmDialogPage(MessageDialogParams params, WebMarkupContainer ok,
            WebMarkupContainer cancel) {
        super(new CompoundPropertyModel<MessageDialogParams>(params));

        add(new MultiLineLabel("message").setVisible(params.message != null));

        add(ok);
        add(cancel);
    }
}
