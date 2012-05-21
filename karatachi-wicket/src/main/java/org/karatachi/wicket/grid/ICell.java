package org.karatachi.wicket.grid;

import org.apache.wicket.Component;

public interface ICell {
    public Object getValue();

    public void setValue(Object value);

    public String getClassName();

    public void setClassName(String className);

    public String getStyle();

    public void setStyle(String style);

    public void setIndent(int indent);

    public int getIndent();

    public void setRowspan(int rowspan);

    public int getRowspan();

    public void setColspan(int colspan);

    public int getColspan();

    public void setupComponent(Component component);
}
