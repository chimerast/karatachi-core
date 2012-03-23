package org.karatachi.net.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.karatachi.net.shell.CommandReader;

public class RshReader extends CommandReader {
    private final BufferedInputStream in;
    private final Charset charset;
    private final byte[] promptBytes;

    private final byte[] buffer;
    private int count;

    public RshReader(BufferedInputStream in, Charset charset) {
        this.in = in;
        this.charset = charset;
        this.promptBytes = Rsh.CONTROL_PROMPT.getBytes(charset);

        this.temporaryLine = false;
        this.readLine = null;

        this.buffer = new byte[8192];
        this.count = 0;
    }

    @Override
    protected boolean fillLine() throws IOException {
        byte[] buff = new byte[promptBytes.length];
        in.mark(buff.length);
        for (int i = 0; i < buff.length; ++i) {
            buff[i] = (byte) in.read();
        }
        in.reset();

        if (Arrays.equals(buff, promptBytes))
            return false;

        int c;
        while ((c = in.read()) >= 0) {
            switch (c) {
            case Rsh.LF:
                temporaryLine = false;
                return true;
            case Rsh.CR:
                readLine = new String(buffer, 0, count, charset);
                count = 0;
                temporaryLine = true;
                return true;
            default:
                if (c >= 32 && c < 127) {
                    if (count == buffer.length)
                        throw new IOException("Line buffer overflow.");
                    buffer[count] = (byte) c;
                    ++count;
                } else {
                }
            }
        }

        return false;
    }

    public boolean readUntilPrompt() throws IOException {
        byte[] buff = new byte[promptBytes.length];
        while (true) {
            in.mark(buff.length);
            for (int i = 0; i < buff.length; ++i) {
                buff[i] = (byte) in.read();
            }

            if (Arrays.equals(buff, promptBytes)) {
                return true;
            } else {
                in.reset();

                while (true) {
                    int c = in.read();
                    if (c < 0)
                        return false;
                    if (c == Rsh.LF)
                        break;
                }
            }
        }
    }
}
