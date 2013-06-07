package org.karatachi.wicket.dialog;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;

public abstract class InputDialog extends ModalWindow {
    private static final long serialVersionUID = 1L;

    private InputDialogParams params;

    public InputDialog(String id) {
        super(id);

        setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        setResizable(false);
        setInitialWidth(400);
        setInitialHeight(120);
        setWidthUnit("px");
        setHeightUnit("px");

        final Form<Void> form;
        form = new Form<Void>("form");

        AjaxButton ok;
        form.add(ok = new AjaxButton("ok") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                InputDialog.this.params.result = true;
                InputDialog.this.params.onClosing(target);
                InputDialog.this.close(target);
            }
        });
        form.setDefaultButton(ok);

        form.add(new AjaxButton("cancel") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                InputDialog.this.params.result = false;
                InputDialog.this.params.onClosing(target);
                InputDialog.this.close(target);
            }
        });

        setPageCreator(new ModalWindow.PageCreator() {
            private static final long serialVersionUID = 1L;

            @Override
            public Page createPage() {
                return InputDialog.this.createPage(params, form);
            }

        });

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

    protected InputDialogPage createPage(InputDialogParams params,
            Form<Void> form) {
        return new InputDialogPage(params, form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                setHeader(response);
            }
        };
    }

    @Override
    public void show(AjaxRequestTarget target) {
        throw new UnsupportedOperationException(
                "Use InputDialog.show(AjaxRequestTarget, InputDialogParam)");
    }

    public void show(AjaxRequestTarget target, InputDialogParams params) {
        this.params = params;
        super.show(target);
    }

    abstract protected void setHeader(IHeaderResponse response);

}
