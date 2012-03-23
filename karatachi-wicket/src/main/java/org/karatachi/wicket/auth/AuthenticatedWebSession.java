package org.karatachi.wicket.auth;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

public abstract class AuthenticatedWebSession<R> extends WebSession {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public static <T> AuthenticatedWebSession<T> getSession() {
        return (AuthenticatedWebSession<T>) Session.get();
    }

    private boolean signedIn;

    public AuthenticatedWebSession(Request request) {
        super(request);
    }

    protected abstract boolean authenticate(String username, String password);

    public abstract R getRole();

    public final boolean isSignedIn() {
        return signedIn;
    }

    public final boolean signIn(final String username, final String password) {
        return signedIn = authenticate(username, password);
    }

    public void signOut() {
        signedIn = false;
        invalidate();
    }
}
