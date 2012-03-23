package org.karatachi.wicket.grid;

import org.apache.wicket.ajax.IAjaxCallDecorator;

public abstract class DefaultLinkCell extends DefaultCell implements ILinkCell {
    private static final long serialVersionUID = 1L;

    public DefaultLinkCell() {
    }

    public DefaultLinkCell(Object value) {
        super(value);
    }

    @Override
    public IAjaxCallDecorator getAjaxCallDecorator() {
        return null;
    }
}
