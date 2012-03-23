package org.karatachi.net.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommandResponse {
    public static final int S_OK = 200;

    private int statusCode;
    private String statusText;

    private final List<String> headerName = new ArrayList<String>();
    private final Map<String, String> header = new HashMap<String, String>();
    private final List<String> body = new ArrayList<String>();

    public CommandResponse(CommandReader in, CommandResponseCallback callback)
            throws IOException {
        if (callback != null)
            readTemporaryStatus(in, callback);
        readStatus(in);
        readHeader(in);
        readBody(in);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getHeader(String name) {
        return header.get(name.toLowerCase());
    }

    public int getHeaderInt(String name) {
        try {
            return Integer.parseInt(getHeader(name));
        } catch (Exception e) {
            return -1;
        }
    }

    public long getHeaderLong(String name) {
        try {
            return Long.parseLong(getHeader(name));
        } catch (Exception e) {
            return -1;
        }
    }

    public List<String> getHeaderNames() {
        return Collections.unmodifiableList(headerName);
    }

    public List<String> getBody() {
        return Collections.unmodifiableList(body);
    }

    private void readTemporaryStatus(CommandReader in,
            CommandResponseCallback callback) throws IOException {
        String line;
        while ((line = in.readTempraryLine()) != null) {
            int pos;
            if ((pos = line.indexOf(' ')) > 0) {
                statusCode = Integer.parseInt(line.substring(0, pos));
                statusText = line.substring(pos + 1).trim();
            } else {
                statusCode = Integer.parseInt(line);
                statusText = "";
            }
            callback.callbackStatus(statusCode, statusText);
        }
    }

    private void readStatus(CommandReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Cannot read status line.");
        }

        int pos;
        if ((pos = line.indexOf(' ')) > 0) {
            statusCode = Integer.parseInt(line.substring(0, pos));
            statusText = line.substring(pos + 1).trim();
        } else {
            statusCode = Integer.parseInt(line);
            statusText = "";
        }
    }

    private void readHeader(CommandReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.trim().equals(""))
                return;
            int pos;
            if ((pos = line.indexOf(':')) > 0) {
                String name = line.substring(0, pos);
                String value = line.substring(pos + 1);
                header.put(name.trim().toLowerCase(), value.trim());
                headerName.add(name.trim());
            }
        }
    }

    private void readBody(CommandReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            body.add(line);
        }
    }
}
