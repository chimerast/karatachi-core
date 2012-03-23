package org.karatachi.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseDumper {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final File file;
    private final OutputStreamWriter out;

    public DatabaseDumper(File file) throws IOException {
        this.file = file;

        FileOutputStream fout = new FileOutputStream(file);
        try {
            out = new OutputStreamWriter(new GZIPOutputStream(fout), "UTF-8");
        } catch (IOException e) {
            fout.close();
            throw e;
        }
    }

    public File getFile() {
        return file;
    }

    public void close() throws IOException {
        out.close();
    }

    public long dump(ResultSet rs) throws IOException, SQLException {
        logger.debug("Dump start on {}", out);
        long start = System.currentTimeMillis();
        long count = 0;
        int columnCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnCount; ++i) {
                String str = rs.getString(i);
                if (str != null) {
                    out.write(str);
                }
                if (i != columnCount) {
                    out.write('\t');
                }
            }
            out.write("\r\n");
            count++;
        }
        long end = System.currentTimeMillis();
        logger.debug("Dump end on {} in {} ms", out, end - start);
        return count;
    }
}
