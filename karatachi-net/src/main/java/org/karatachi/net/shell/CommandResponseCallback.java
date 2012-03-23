package org.karatachi.net.shell;

public interface CommandResponseCallback {
    public void callbackStatus(int statusCode, String statusText);
}
