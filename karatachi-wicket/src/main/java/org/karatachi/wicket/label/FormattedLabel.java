package org.karatachi.wicket.label;

import java.io.Serializable;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FormattedLabel extends Label {
    private static final long serialVersionUID = 1L;

    private final String format;

    public FormattedLabel(String id, String format, IModel<?> model) {
        super(id, model);
        this.format = format;
    }

    public <T extends Serializable> FormattedLabel(String id, String format,
            T value) {
        super(id, new Model<T>(value));
        this.format = format;
    }

    public FormattedLabel(String id, String format) {
        super(id);
        this.format = format;
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream,
            ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, String.format(format,
                getDefaultModelObject()));
    }
}
