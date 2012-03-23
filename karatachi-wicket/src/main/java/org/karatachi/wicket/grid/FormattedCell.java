package org.karatachi.wicket.grid;

public class FormattedCell extends DefaultCell {
    private static final long serialVersionUID = 1L;

    private String format;

    public FormattedCell() {
    }

    public FormattedCell(Object value, String format) {
        super(value);
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        if (getValue() != null) {
            return String.format(format, getValue());
        } else {
            return "";
        }
    }
}
