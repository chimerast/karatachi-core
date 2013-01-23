package org.karatachi.wicket.grid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

public class ExcelResourceStream implements IResourceStream {
    private static final long serialVersionUID = 1L;

    private Cells cells;

    private Locale locale;

    public ExcelResourceStream(Cells cells) {
        this.cells = cells;
    }

    @Override
    public String getContentType() {
        return "application/vnd.ms-excel";
    }

    @Override
    public long length() {
        return -1;
    }

    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(Locale.JAPAN);
            ws.setEncoding("Windows-31J");

            WritableWorkbook workbook = Workbook.createWorkbook(os, ws);
            WritableSheet sheet = workbook.createSheet("Sheet1", 0);
            setupSheet(sheet);

            int rows = cells.getRows();
            int cols = cells.getCols();
            for (int r = 0; r < rows; ++r) {
                for (int c = 0; c < cols; ++c) {
                    ICell cell = cells.getCell(r + 1, c + 1);
                    if (cell == null || cell.getValue() == null) {
                        continue;
                    }

                    Object obj = cell.getValue();

                    WritableCell w;
                    if (obj instanceof Number) {
                        w =
                                new jxl.write.Number(c, r,
                                        ((Number) obj).doubleValue());
                    } else if (obj instanceof Date) {
                        w =
                                new DateTime(c, r, (Date) obj,
                                        new WritableCellFormat(
                                                DateFormats.DEFAULT));
                    } else {
                        w = new Label(c, r, obj.toString());
                    }

                    setupCell(cell, w);

                    sheet.addCell(w);
                }
            }
            workbook.write();
            workbook.close();
            os.close();

            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            throw new ResourceStreamNotFoundException(e);
        }
    }

    protected void setupSheet(WritableSheet w) {
    }

    protected void setupCell(ICell cell, WritableCell w) throws WriteException {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Time lastModifiedTime() {
        return Time.now();
    }
}
