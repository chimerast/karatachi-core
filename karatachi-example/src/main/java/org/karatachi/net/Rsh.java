package org.karatachi.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.karatachi.classloader.PackageDir;
import org.karatachi.net.rsh.RshService;
import org.karatachi.net.shell.Command;

public class Rsh extends RshService {
    public static void main(String[] args) throws Exception {
        Rsh rsh = new Rsh();
        System.in.read();
        rsh.stop();
    }

    public Rsh() throws IOException {
        super(new ServerSocket(23), getCommands(), Charset.defaultCharset());
    }

    public static Map<String, Class<? extends Command>> getCommands() {
        Map<String, Class<? extends Command>> ret =
                new HashMap<String, Class<? extends Command>>();
        PackageDir dir =
                new PackageDir(Rsh.class.getClassLoader(),
                        "org.karatachi.net.command");
        for (Class<? extends Command> clazz : dir.getClasses(Command.class)) {
            try {
                Command command = clazz.newInstance();
                ret.put(command.getCommand(), clazz);
            } catch (IllegalAccessException ignore) {
            } catch (InstantiationException ignore) {
            }
        }
        return ret;
    }
}
