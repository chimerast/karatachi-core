package org.karatachi.wicket.dialog;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.CompoundPropertyModel;

public class ConfirmDialog extends ModalWindow {
    private static final long serialVersionUID = 1L;

    private MessageDialogParams params;

    public ConfirmDialog(String id) {
        super(id);

        setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        setResizable(false);
        setInitialWidth(400);
        setInitialHeight(140);
        setWidthUnit("px");
        setHeightUnit("px");

        setPageCreator(new ModalWindow.PageCreator() {
            private static final long serialVersionUID = 1L;

            public Page createPage() {
                return new ConfirmDialogPage();
            }
        });

        setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            private static final long serialVersionUID = 1L;

            public void onClose(AjaxRequestTarget target) {
                if (params.result) {
                    params.onSuccess(target);
                } else {
                    params.onCancel(target);
                }
                params.onClosed(target);
            }
        });
    }

    @Override
    public void show(AjaxRequestTarget target) {
        throw new UnsupportedOperationException(
                "Use ConfirmDialog.show(AjaxRequestTarget, ConfirmDialogParam)");
    }

    public void show(AjaxRequestTarget target, MessageDialogParams params) {
        this.params = params;
        super.show(target);
    }

    private class ConfirmDialogPage extends WebPage {
        private static final long serialVersionUID = 1L;

        private ConfirmDialogPage() {
            super(new CompoundPropertyModel<MessageDialogParams>(params));

            add(new MultiLineLabel("message").setVisible(params.message != null));

            add(new AjaxLink<Void>("ok") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    ConfirmDialog.this.params.result = true;
                    ConfirmDialog.this.params.onClosing(target);
                    ConfirmDialog.this.close(target);
                }
            });

            add(new AjaxLink<Void>("cancel") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    ConfirmDialog.this.params.result = false;
                    ConfirmDialog.this.params.onClosing(target);
                    ConfirmDialog.this.close(target);
                }
            });
        }
    }
}
