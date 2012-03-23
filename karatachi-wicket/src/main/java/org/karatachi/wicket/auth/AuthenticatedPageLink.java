package org.karatachi.wicket.auth;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class AuthenticatedPageLink extends BookmarkablePageLink<Void> {
    private static final long serialVersionUID = 1L;

    public AuthenticatedPageLink(String id, Class<? extends WebPage> pageClass,
            PageParameters parameters) {
        super(id, pageClass, parameters);
        init(pageClass);
    }

    public AuthenticatedPageLink(String id, Class<? extends WebPage> pageClass) {
        super(id, pageClass);
        init(pageClass);
    }

    private void init(Class<? extends WebPage> pageClass) {
        IAuthorizationStrategy strategy =
                getApplication().getSecuritySettings().getAuthorizationStrategy();
        if (strategy == null) {
            setVisible(false);
        }
        if (!strategy.isInstantiationAuthorized(pageClass)) {
            setVisible(false);
        }
    }
}
