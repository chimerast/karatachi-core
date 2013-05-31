package org.karatachi.wicket.dialog;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;

public abstract class ConfirmDialog extends ModalWindow {
    private static final long serialVersionUID = 1L;

    MessageDialogParams params;

    public ConfirmDialog(String id) {
        super(id);

        setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        setResizable(false);
        setInitialWidth(400);
        setInitialHeight(140);
        setWidthUnit("px");
        setHeightUnit("px");

        setPageCreator(createPageCreator());

        setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            private static final long serialVersionUID = 1L;

            @Override
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

    protected PageCreator createPageCreator() {
        return new ModalWindow.PageCreator() {
            private static final long serialVersionUID = 1L;

            @Override
            public Page createPage() {
                AjaxLink<Void> okLink = new AjaxLink<Void>("ok") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ConfirmDialog.this.params.result = true;
                        ConfirmDialog.this.params.onClosing(target);
                        ConfirmDialog.this.close(target);
                    }
                };

                AjaxLink<Void> cancelLink = new AjaxLink<Void>("cancel") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ConfirmDialog.this.params.result = false;
                        ConfirmDialog.this.params.onClosing(target);
                        ConfirmDialog.this.close(target);
                    }
                };

                return new ConfirmDialogPage(ConfirmDialog.this.params, okLink,
                        cancelLink) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void renderHead(IHeaderResponse response) {
                        super.renderHead(response);
                        ConfirmDialog.this.setHeader(response);
                    }
                };
            }
        };
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

    abstract protected void setHeader(IHeaderResponse response);
}
