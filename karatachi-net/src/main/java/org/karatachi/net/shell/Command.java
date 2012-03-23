package org.karatachi.net.shell;

import java.io.IOException;

public interface Command {
    public static final int OK = 0;
    public static final int INVALID_ARGUMENT_COUNTS = -1;
    public static final int INVALID_ARGUMENT = -2;

    public String getCommand();

    public int exec(CommandShell shell, String[] args) throws IOException;
}
