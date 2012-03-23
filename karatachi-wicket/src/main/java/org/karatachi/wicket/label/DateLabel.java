package org.karatachi.wicket.label;

import java.io.Serializable;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.karatachi.wicket.converter.DateTimeConverter;

public class DateLabel extends Label {
    private static final long serialVersionUID = 1L;

    private final DateTimeConverter converter;

    public DateLabel(String id, IModel<Serializable> model) {
        super(id, model);
        converter = new DateTimeConverter();
    }

    public DateLabel(String id, Date date) {
        super(id, new Model<Serializable>(date));
        converter = new DateTimeConverter();
    }

    public DateLabel(String id) {
        super(id);
        converter = new DateTimeConverter();
    }

    public DateLabel(String id, IModel<Serializable> model, int dateStyle,
            int timeStyle) {
        super(id, model);
        converter = new DateTimeConverter(dateStyle, timeStyle);
    }

    public DateLabel(String id, Date date, int dateStyle, int timeStyle) {
        super(id, new Model<Serializable>(date));
        converter = new DateTimeConverter(dateStyle, timeStyle);
    }

    public DateLabel(String id, int dateStyle, int timeStyle) {
        super(id);
        converter = new DateTimeConverter(dateStyle, timeStyle);
    }

    @Override
    public IConverter getConverter(Class<?> type) {
        return converter;
    }
}
