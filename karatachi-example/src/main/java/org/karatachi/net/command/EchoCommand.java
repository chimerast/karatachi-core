package org.karatachi.net.command;

import java.io.IOException;

import org.karatachi.net.shell.Command;
import org.karatachi.net.shell.CommandShell;

public class EchoCommand implements Command {
    @Override
    public int exec(CommandShell shell, String[] args) throws IOException {
        if (args.length == 0) {
            return Command.INVALID_ARGUMENT_COUNTS;
        }

        shell.sendMessage(args[0]);
        return Command.OK;
    }

    @Override
    public String getCommand() {
        return "echo";
    }
}
