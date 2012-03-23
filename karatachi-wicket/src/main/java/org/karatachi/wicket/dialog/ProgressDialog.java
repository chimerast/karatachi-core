package org.karatachi.wicket.dialog;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.karatachi.wicket.ajax.ProgressBar;

public class ProgressDialog extends ModalWindow {
    private static final long serialVersionUID = 1L;

    private ProgressDialogParams params;

    public ProgressDialog(String id) {
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
                return new ProgressDialogPage();
            }
        });

        setWindowClosedCallback(new WindowClosedCallback() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClose(AjaxRequestTarget target) {
                if (params.result) {
                    params.onSuccess(target);
                } else {
                    if (params.thread != null) {
                        params.thread.interrupt();
                        params.thread = null;
                        /*
                        try {
                            params.thread.join();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        */
                    }
                    params.onCancel(target);
                }
            }
        });
    }

    @Override
    public void show(AjaxRequestTarget target) {
        throw new UnsupportedOperationException(
                "Use ProgressDialog.show(AjaxRequestTarget, ConfirmDialogParam)");
    }

    public void show(AjaxRequestTarget target, ProgressDialogParams params) {
        this.params = params;
        this.params.progress = 0.0;
        super.show(target);
        this.params.thread = new Thread(this.params);
        this.params.thread.start();
    }

    private class ProgressDialogPage extends WebPage {
        private ProgressDialogPage() {
            super(new CompoundPropertyModel<ProgressDialogParams>(params));

            add(new Label("message").setVisible(params.message != null));

            add(new ProgressBar("progress", 350) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onProcessTarget(AjaxRequestTarget target) {
                    if (params.terminated) {
                        ProgressDialog.this.close(target);
                    }
                }
            });

            add(new AjaxLink<Void>("cancel") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    if (params.thread != null) {
                        params.thread.interrupt();
                        try {
                            params.thread.join();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
        }
    }
}
