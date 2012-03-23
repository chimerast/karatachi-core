package org.karatachi.example.web.auth;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.auth.SignInPanel;

public class SignInPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public SignInPage() {
        add(new SignInPanel("signin"));
    }
}
