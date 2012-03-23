package org.karatachi.example.web.dialog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.dialog.ProgressDialog;
import org.karatachi.wicket.dialog.ProgressDialogParams;

public class ProgressDialogPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    private ProgressDialog dialog;

    public ProgressDialogPage() {
        add(dialog = new ProgressDialog("dialog"));

        add(new AjaxLink<Void>("link") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.show(target, new ProgressDialogParams("ファイルを削除します。") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void execute() throws InterruptedException {
                        for (int i = 0; i < 100; ++i) {
                            Thread.sleep(100);
                            setProgress(i / 100.0);
                        }
                    }

                    @Override
                    public void onSuccess(AjaxRequestTarget target) {
                        dialog.close(target);
                    }
                });
            }
        });
    }
}
