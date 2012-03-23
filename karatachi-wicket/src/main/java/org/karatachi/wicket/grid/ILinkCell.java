package org.karatachi.wicket.grid;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;

public interface ILinkCell extends ICell {
    public void onClick(AjaxRequestTarget target);

    public IAjaxCallDecorator getAjaxCallDecorator();
}
