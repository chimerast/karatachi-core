package org.karatachi.net.shell;

import java.io.IOException;
import java.util.List;

public abstract class CommandHandler {
    public final void executeTask(CommandTask task) throws IOException {
        task.execute(this);
    }

    public final void executeTask(List<Class<? extends CommandTask>> tasks)
            throws IOException, InstantiationException, IllegalAccessException {
        for (Class<? extends CommandTask> clazz : tasks) {
            executeTask(clazz.newInstance());
        }
    }

    public final CommandResponse executeCommand(String command)
            throws IOException {
        return executeCommand(command, null);
    }

    public abstract CommandResponse executeCommand(String command,
            CommandResponseCallback callback) throws IOException;

    public abstract void close() throws IOException;
}
