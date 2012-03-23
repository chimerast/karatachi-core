package org.karatachi.wicket.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.karatachi.wicket.auto.SelfResolveForm;

public class SignInPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public SignInPanel(String id) {
        super(id);
        add(new SignInForm("form"));
    }

    private class SignInForm extends SelfResolveForm {
        private static final long serialVersionUID = 1L;

        private String username = "";
        private String password = "";
        private boolean rememberMe = true;

        public SignInForm(String id) {
            super(id, "feedback");
        }

        @Override
        protected void init(boolean confirm) {
            add(new TextField<String>("username").setRequired(true).setPersistent(
                    true));
        }

        @Override
        protected void onSubmit() {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("username", username);
            parameters.put("password", password);

            if (AuthenticatedWebSession.getSession().authenticate(parameters)) {
                if (!rememberMe) {
                    getPage().removePersistedFormData(SignInForm.class, true);
                }

                if (!continueToOriginalDestination()) {
                    setResponsePage(getApplication().getHomePage());
                }
            } else {
                error(getLocalizer().getString("signInFailed", this));
            }
        }
    }
}
