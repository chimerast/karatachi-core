package org.karatachi.wicket.dialog;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

public class InputDialog extends ModalWindow {
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

        setPageCreator(new ModalWindow.PageCreator() {
            private static final long serialVersionUID = 1L;

            public Page createPage() {
                return new InputDialogPage();
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
            }
        });
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

    private class InputDialogPage extends WebPage implements IHeaderContributor {
        private TextField<String> input;

        private InputDialogPage() {
            super(new CompoundPropertyModel<InputDialogParams>(params));

            add(new Label("message").setVisible(params.message != null));

            Form<Void> form;
            add(form = new Form<Void>("form"));

            form.add(input = new TextField<String>("input"));
            input.setOutputMarkupId(true);

            AjaxButton ok;
            form.add(ok = new AjaxButton("ok") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    InputDialog.this.params.result = true;
                    InputDialog.this.close(target);
                }
            });
            form.setDefaultButton(ok);

            form.add(new AjaxButton("cancel") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    InputDialog.this.params.result = false;
                    InputDialog.this.close(target);
                }
            });
        }

        public void renderHead(IHeaderResponse response) {
            response.renderOnLoadJavascript("Wicket.Focus.setFocusOnId('"
                    + input.getMarkupId() + "')");
        }
    }
}
