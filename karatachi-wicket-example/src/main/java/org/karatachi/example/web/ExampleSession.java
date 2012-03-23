package org.karatachi.example.web;

import org.apache.wicket.Request;
import org.karatachi.example.web.auth.Role;
import org.karatachi.wicket.auth.AuthenticatedWebSession;

public class ExampleSession extends AuthenticatedWebSession<Role> {
    private static final long serialVersionUID = 1L;

    private Role role;

    public ExampleSession(Request request) {
        super(request);
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (username.equals("chimera")) {
            role = Role.USER;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Role getRole() {
        return role;
    }
}
