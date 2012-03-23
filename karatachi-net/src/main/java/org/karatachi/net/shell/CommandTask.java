package org.karatachi.net.shell;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommandTask {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public abstract void execute(CommandHandler handler) throws IOException;
}
