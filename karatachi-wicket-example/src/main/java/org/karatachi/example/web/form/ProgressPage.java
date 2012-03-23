package org.karatachi.example.web.form;

import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.ajax.ProgressBar;

public class ProgressPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public ProgressPage() {
        add(new ProgressBar("progress", new LoadableDetachableModel<Double>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Double load() {
                return Math.random();
            }
        }, 400));
    }
}
