package org.karatachi.net.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.karatachi.net.shell.CommandHandler;
import org.karatachi.net.shell.CommandResponse;
import org.karatachi.net.shell.CommandResponseCallback;
import org.karatachi.translator.IntervalTranslator;

public class RshHandler extends CommandHandler {
    private final int connectionTimeout = (int) IntervalTranslator.sec(2);
    private final int readTimeout = (int) IntervalTranslator.sec(30);

    private final SocketChannel channel;
    private final Charset charset;

    private final BufferedInputStream in;
    private final OutputStream out;

    public RshHandler(InetSocketAddress sockaddr, Charset charset)
            throws IOException {
        this.channel = SocketChannel.open();
        this.charset = charset;
        try {
            Socket socket = this.channel.socket();

            socket.connect(sockaddr, connectionTimeout);
            socket.setSoTimeout(readTimeout);

            this.in = new BufferedInputStream(socket.getInputStream());
            this.out = socket.getOutputStream();

            sendOption();
            negotiation();
        } catch (RuntimeException e) {
            channel.close();
            throw e;
        } catch (IOException e) {
            channel.close();
            throw e;
        }
    }

    @Override
    public CommandResponse executeCommand(String command,
            CommandResponseCallback callback) throws IOException {
        RshReader reader = new RshReader(in, charset);
        if (!reader.readUntilPrompt()) {
            throw new IOException("Negotiation failure.");
        }
        out.write((command + "\r\n").getBytes(charset));
        return new CommandResponse(reader, callback);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    private void sendOption() throws IOException {
        out.write(Rsh.constructOption(Rsh.TELNET_DONT, Rsh.OPT_ECHO));
        out.write(Rsh.constructOption(Rsh.TELNET_DO, Rsh.OPT_SGA));
    }

    private void negotiation() throws IOException {
        while (true) {
            int[] buff = new int[3];
            in.mark(buff.length);
            buff[0] = in.read();
            buff[1] = in.read();
            buff[2] = in.read();

            if (buff[0] != Rsh.TELNET_IAC) {
                in.reset();
                return;
            } else {
                response(buff[1], buff[2]);
            }
        }
    }

    private void response(int verb, int opt) throws IOException {
        if (verb == Rsh.TELNET_DO) {
            switch (opt) {
            case Rsh.OPT_SGA:
                out.write(Rsh.constructOption(Rsh.TELNET_WILL, opt));
                break;
            default:
                out.write(Rsh.constructOption(Rsh.TELNET_WONT, opt));
                break;
            }
        }
    }
}
