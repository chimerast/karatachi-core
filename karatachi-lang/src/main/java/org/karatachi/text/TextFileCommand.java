package org.karatachi.text;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextFileCommand {
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) throws Exception {
        File error = new File("/portus/log/admin/error.log");
        for (String line : tail(300, error)) {
            System.out.println(line);
        }
    }

    public static List<String> tail(int count, File file) throws IOException {
        List<String> ret = new ArrayList<String>();
        if (file.length() == 0) {
            return ret;
        }

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        try {
            byte[] buffer = new byte[BUFFER_SIZE];

            long cursor = raf.length();
            int rest = 0;
            outer: while (true) {
                int bytesToRead = BUFFER_SIZE - rest;
                long startpos = cursor - bytesToRead;

                if (startpos < 0) {
                    bytesToRead = (int) -startpos;
                    startpos = 0;
                }

                for (int i = 0; i < rest; ++i) {
                    buffer[i + bytesToRead] = buffer[i];
                }
                raf.seek(startpos);
                raf.read(buffer, 0, bytesToRead);

                cursor = startpos;

                rest += bytesToRead;

                for (int i = rest - 1; i >= 0; --i) {
                    switch (buffer[i]) {
                    case '\n':
                        ret.add(new String(buffer, i + 1, rest - (i + 1),
                                Charset.forName("UTF-8")));
                        if (ret.size() == 1 && ret.get(0).length() == 0) {
                            ret.clear();
                        } else if (ret.size() == count) {
                            break outer;
                        }
                    case '\r':
                        rest = i;
                    }
                }

                if (rest > BUFFER_SIZE / 2) {
                    break outer;
                }

                if (cursor == 0) {
                    ret.add(new String(buffer, 0, rest,
                            Charset.forName("UTF-8")));
                    break outer;
                }
            }

            Collections.reverse(ret);
            return ret;
        } finally {
            raf.close();
        }
    }
}
