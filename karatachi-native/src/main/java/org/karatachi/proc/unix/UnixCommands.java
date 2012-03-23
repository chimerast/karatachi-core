package org.karatachi.proc.unix;

import java.io.File;
import java.io.IOException;

import org.karatachi.proc.NativeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnixCommands {
    private static final Logger logger = LoggerFactory
            .getLogger(UnixCommands.class);

    public static String tail(int count, File file) {
        if (!file.exists() || !file.canRead()) {
            return "File not found";
        }

        try {
            NativeExecutor ne = new NativeExecutor();
            StringBuilder sb = new StringBuilder();
            ne.exec(sb, "tail -n %d %s", count, file.getAbsolutePath());
            return sb.toString();
        } catch (IOException e) {
            logger.error("Fail to execute tail command", e);
            return "Fail to execute tail command";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        }
    }
}
