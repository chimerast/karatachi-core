package org.karatachi.proc;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeExecutor {
    private static final Logger logger = LoggerFactory
            .getLogger(NativeExecutor.class);

    private final int maxLine;
    private final Charset cs;

    public NativeExecutor() {
        this(300, Charset.defaultCharset());
    }

    public NativeExecutor(int maxLine, Charset cs) {
        this.maxLine = maxLine;
        this.cs = cs;
    }

    public int exec(String format, Object... args) throws IOException,
            InterruptedException {
        return exec(null, null, format, args);
    }

    public int exec(StringBuilder out, String format, Object... args)
            throws IOException, InterruptedException {
        return exec(out, null, format, args);
    }

    public int exec(StringBuilder out, StringBuilder err, String format,
            Object... args) throws IOException, InterruptedException {
        Process process = null;
        ExecutorService executor = null;
        try {
            process = Runtime.getRuntime().exec(String.format(format, args));

            closeQuietly(process.getOutputStream());

            StreamHandler output = new StreamHandler(process.getInputStream(),
                    maxLine, cs);
            StreamHandler error = new StreamHandler(process.getErrorStream(),
                    maxLine, cs);

            executor = Executors.newFixedThreadPool(2);
            List<Future<String>> result = executor.invokeAll(Arrays.asList(
                    output, error));

            if (out != null) {
                try {
                    out.append(result.get(0).get());
                } catch (Exception e) {
                    out.append(e.getMessage());
                }
            }

            if (err != null) {
                try {
                    err.append(result.get(1).get());
                } catch (Exception e) {
                    err.append(e.getMessage());
                }
            }

            return process.waitFor();
        } finally {
            if (process != null) {
                closeQuietly(process.getErrorStream());
                closeQuietly(process.getInputStream());
                closeQuietly(process.getOutputStream());
                process.destroy();
            }
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            logger.error("Fail to close stream", e);
        }
    }

    private static class StreamHandler implements Callable<String> {
        private final BufferedReader reader;
        private final int maxLine;

        public StreamHandler(InputStream in, int maxLine, Charset cs) {
            this.reader = new BufferedReader(new InputStreamReader(in, cs));
            this.maxLine = maxLine;
        }

        @Override
        public String call() throws Exception {
            LinkedList<String> lines = new LinkedList<String>();
            {
                String line;
                while ((line = reader.readLine()) != null) {
                    while (!(lines.size() < maxLine))
                        lines.removeFirst();
                    lines.addLast(line);
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line);
                sb.append("\r\n");
            }

            return sb.toString();
        }
    }
}
