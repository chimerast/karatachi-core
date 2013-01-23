package org.karatachi.wicket.auth;

import java.util.Map;

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

    protected abstract boolean authenticate(Map<String, String> parameters);

    public abstract R getRole();

    public final boolean isSignedIn() {
        return signedIn;
    }

    public final boolean signIn(Map<String, String> parameters) {
        return signedIn = authenticate(parameters);
    }

    public void signOut() {
        signedIn = false;
    }
}
