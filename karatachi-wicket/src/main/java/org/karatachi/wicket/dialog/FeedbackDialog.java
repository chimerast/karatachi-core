package org.karatachi.wicket.dialog;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

public class FeedbackDialog extends ModalWindow {
    private static final long serialVersionUID = 1L;

    private MessageDialogParams params;

    public FeedbackDialog(String id) {
        super(id);

        setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        setResizable(false);
        setInitialWidth(400);
        setInitialHeight(120);
        setWidthUnit("px");
        setHeightUnit("px");

        setPageCreator(new ModalWindow.PageCreator() {
            private static final long serialVersionUID = 1L;

            public Page createPage() {
                return new FeedbackDialogPage();
            }
        });

        setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            private static final long serialVersionUID = 1L;

            public void onClose(AjaxRequestTarget target) {
                params.onSuccess(target);
            }
        });
    }

    @Override
    public void show(AjaxRequestTarget target) {
        throw new UnsupportedOperationException(
                "Use NotifyDialog.show(AjaxRequestTarget, ConfirmDialogParam)");
    }

    public void show(AjaxRequestTarget target, MessageDialogParams params) {
        this.params = params;
        super.show(target);
    }

    private class FeedbackDialogPage extends WebPage {
        private FeedbackDialogPage() {
            super(new CompoundPropertyModel<MessageDialogParams>(params));

            add(new Label("message").setVisible(params.message != null));

            FeedbackPanel feedback;
            add(feedback = new FeedbackPanel("feedback"));
            feedback.anyMessage();

            add(new AjaxLink<Object>("ok") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    FeedbackDialog.this.close(target);
                }
            });
        }
    }
}
