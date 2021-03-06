package org.karatachi.wicket.grid;

import java.io.Serializable;

import org.apache.wicket.Component;

public class DefaultCell implements ICell, Serializable {
    private static final long serialVersionUID = 1L;

    private Object value;
    private String className;
    private String style;
    private int indent;
    private int rowspan;
    private int colspan;

    public DefaultCell() {
    }

    public DefaultCell(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public int getIndent() {
        return indent;
    }

    @Override
    public void setIndent(int indent) {
        this.indent = indent;
    }

    @Override
    public int getRowspan() {
        return rowspan;
    }

    @Override
    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    @Override
    public int getColspan() {
        return colspan;
    }

    @Override
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    @Override
    public void setupComponent(Component component) {
    }

    @Override
    public String toString() {
        if (value != null) {
            return value.toString();
        } else {
            return "";
        }
    }
}
