package org.karatachi.example.web.form;

import java.net.InetAddress;
import java.util.Date;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.auto.SelfResolveForm;

public class SimpleFormPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public SimpleFormPage() {
        add(new SimpleExampleForm("form"));
    }

    private class SimpleExampleForm extends SelfResolveForm {
        private static final long serialVersionUID = 1L;

        String form1 = "";
        String form2 = "";
        InetAddress addr;
        Date date = new Date();
        boolean check;

        public SimpleExampleForm(String id) {
            super(id);
        }

        @Override
        protected void init(boolean confirm) {
            setRequiredComponentBorder(new RequiredComponentBorder());
        }

        @Override
        protected void onSubmit() {
            if (!isConfirm()) {
                setConfirm(true);
            }
        }
    }
}
