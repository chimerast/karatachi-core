package org.karatachi.wicket.dialog;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;

public class NotifyDialog extends ModalWindow {
    private static final long serialVersionUID = 1L;

    private MessageDialogParams params;

    public NotifyDialog(String id) {
        this(id, 400, 120);
    }

    public NotifyDialog(String id, int width, int height) {
        super(id);

        setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        setResizable(false);
        setInitialWidth(width);
        setInitialHeight(height);
        setWidthUnit("px");
        setHeightUnit("px");

        setPageCreator(new ModalWindow.PageCreator() {
            private static final long serialVersionUID = 1L;

            public Page createPage() {
                return new NotifyDialogPage();
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

    private class NotifyDialogPage extends WebPage {
        private static final long serialVersionUID = 1L;

        private NotifyDialogPage() {
            super(new CompoundPropertyModel<MessageDialogParams>(params));

            add(new Label("message").setEscapeModelStrings(false).setVisible(
                    params.message != null));

            add(new AjaxLink<Void>("ok") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    NotifyDialog.this.close(target);
                }
            });
        }
    }
}
