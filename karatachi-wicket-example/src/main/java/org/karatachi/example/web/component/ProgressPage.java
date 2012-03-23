package org.karatachi.example.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.ajax.ProgressBar;
import org.karatachi.wicket.dialog.ProgressDialog;
import org.karatachi.wicket.dialog.ProgressDialogParams;

public class ProgressPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    private ProgressDialog dialog;

    public ProgressPage() {
        add(new ProgressBar("progress", new LoadableDetachableModel<Double>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Double load() {
                return (System.currentTimeMillis() / 1000 % 100) / 100.0;
            }
        }, 400));

        add(new AjaxLink<Void>("link") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.show(target, new ProgressDialogParams("処理を行っています。") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void execute() throws InterruptedException {
                        for (int i = 0; i < 40; ++i) {
                            Thread.sleep(100);
                            setProgress(i / 40.0);
                        }
                    }

                    @Override
                    public void onSuccess(AjaxRequestTarget target) {
                        dialog.close(target);
                    }
                });
            }
        });
        add(dialog = new ProgressDialog("dialog"));
    }
}
