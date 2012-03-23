package org.karatachi.net.shell;

import java.io.IOException;

public interface CommandShell {
    public void sendTemporaryMessage(String msg) throws IOException;

    public void sendMessage(String msg) throws IOException;
}
