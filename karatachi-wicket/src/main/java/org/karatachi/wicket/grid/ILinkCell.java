package org.karatachi.wicket.grid;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;

public interface ILinkCell extends ICell {
    void onClick(AjaxRequestTarget target);

    void updateAjaxAttributes(AjaxRequestAttributes attributes);
}
