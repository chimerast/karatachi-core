package org.karatachi.net.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Map;

import org.karatachi.net.shell.Command;
import org.karatachi.net.shell.CommandShell;
import org.karatachi.system.ClassPropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RshShell implements Runnable, CommandShell {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static int soTimeout;
    static {
        ClassPropertyLoader loader = new ClassPropertyLoader();
        loader.loadIfNotContains(RshShell.class.getResourceAsStream("/karatachi.properties"));
        loader.loadIfNotContains(RshShell.class.getResourceAsStream("/karatachi-net.properties"));
        loader.setClassProperties(RshShell.class);
    }

    private final Socket socket;
    private final Charset charset;

    private final InputStream in;
    private final OutputStream out;

    private final Map<String, Class<? extends Command>> commands;

    private boolean echo = false;

    public RshShell(Socket socket,
            Map<String, Class<? extends Command>> commands, Charset charset)
            throws IOException {
        this.socket = socket;
        this.charset = charset;

        this.in = new BufferedInputStream(socket.getInputStream());
        this.out = socket.getOutputStream();

        this.commands = commands;

        this.socket.setKeepAlive(true);
        this.socket.setTcpNoDelay(true);
        this.socket.setSoTimeout(soTimeout);
    }

    public void executeCommand(String line) throws IOException {
        try {
            String[] args = line.trim().split(" +");

            Class<? extends Command> command =
                    commands.get(args[0].toLowerCase());
            if (command != null) {
                try {
                    int ret = command.newInstance().exec(this, args);
                    switch (ret) {
                    case Command.INVALID_ARGUMENT_COUNTS:
                        sendMessage("401 Invalid argument counts");
                        break;
                    case Command.INVALID_ARGUMENT:
                        sendMessage("402 Invalid argument(s)");
                        break;
                    }
                } catch (IllegalAccessException e) {
                    sendMessage("403 Command not accessible");
                } catch (InstantiationException e) {
                    sendMessage("403 Command cannot instantiate");
                }
            } else {
                sendMessage("403 Command not found");
            }
        } catch (IOException e) {
            sendMessage("500 Internal error");
        }
    }

    public void sendMessage(String msg) throws IOException {
        out.write((msg + "\r\n").getBytes(charset));
    }

    public void sendTemporaryMessage(String msg) throws IOException {
        out.write((msg + "\r").getBytes(charset));
    }

    @Override
    public void run() {
        try {
            sendOption();
            prompt();

            byte[] line = new byte[8096];
            int pos = 0;

            int c;
            while (!socket.isClosed() && (c = in.read()) >= 0) {
                switch (c) {
                case Rsh.LF:
                    if (echo) {
                        out.write(c);
                    }
                    executeCommand(new String(line, 0, pos, charset));
                    pos = 0;
                    prompt();
                    break;
                case Rsh.CR:
                    if (echo) {
                        out.write(c);
                    }
                    break;
                case Rsh.EOT:
                    socket.close();
                    break;
                case Rsh.BS:
                case Rsh.DEL:
                    if (pos > 0) {
                        if (echo) {
                            out.write(c);
                        }
                        --pos;
                    }
                    break;
                case Rsh.TELNET_IAC:
                    int verb = in.read();
                    int opt = in.read();
                    responseOption(verb, opt);
                    break;
                default:
                    if (c >= 32 && c < 127) {
                        if (echo) {
                            out.write(c);
                        }
                        if (pos < line.length) {
                            line[pos] = (byte) c;
                            ++pos;
                        }
                    } else {
                        // ignore
                    }
                }
            }
        } catch (IOException e) {
            if (e instanceof SocketException) {
                logger.info("Connection closed");
            } else {
                logger.error("Error", e);
            }
        } finally {
            try {
                this.socket.close();
            } catch (IOException ignore) {
            }
        }
    }

    private void sendOption() throws IOException {
        out.write(Rsh.constructOption(Rsh.TELNET_DONT, Rsh.OPT_ECHO));
        out.write(Rsh.constructOption(Rsh.TELNET_DO, Rsh.OPT_SGA));
    }

    private void responseOption(int verb, int opt) throws IOException {
        if (verb == Rsh.TELNET_DO) {
            switch (opt) {
            case Rsh.OPT_ECHO:
                echo = true;
            case Rsh.OPT_SGA:
                out.write(Rsh.constructOption(Rsh.TELNET_WILL, opt));
                break;
            default:
                out.write(Rsh.constructOption(Rsh.TELNET_WONT, opt));
                break;
            }
        }
    }

    public void prompt() throws IOException {
        out.write(Rsh.CONTROL_PROMPT.getBytes(charset));
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignore) {
        }
    }
}
