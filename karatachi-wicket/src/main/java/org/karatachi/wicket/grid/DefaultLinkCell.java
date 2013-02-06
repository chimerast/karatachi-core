package org.karatachi.wicket.grid;

import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;

public abstract class DefaultLinkCell extends DefaultCell implements ILinkCell {
    private static final long serialVersionUID = 1L;

    public DefaultLinkCell() {
    }

    public DefaultLinkCell(Object value) {
        super(value);
    }

    @Override
    public void updateAjaxAttributes(AjaxRequestAttributes attributes) {
    }
}
