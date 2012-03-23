package org.karatachi.net.shell;

import java.io.IOException;

public abstract class CommandReader {
    protected boolean temporaryLine;
    protected String readLine;

    public String readLine() throws IOException {
        if (readLine != null && !temporaryLine) {
            String ret = readLine;
            readLine = null;
            return ret;
        }

        do {
            if (!fillLine())
                return null;
        } while (!temporaryLine);

        String ret = readLine;
        readLine = null;
        return ret;
    }

    public String readTempraryLine() throws IOException {
        if (readLine != null && !temporaryLine)
            return null;
        if (!fillLine())
            return null;
        if (!temporaryLine)
            return null;
        return readLine;
    }

    protected abstract boolean fillLine() throws IOException;
}
