package org.karatachi.wicket.form.field;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.karatachi.wicket.converter.DataSizeConverter;

public class DataSizeField extends TextField<Long> {
    private static final long serialVersionUID = 1L;

    public DataSizeField(String id) {
        super(id, Long.class);
    }

    public DataSizeField(String id, IModel<Long> model) {
        super(id, model, Long.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        if (type == Number.class) {
            return (IConverter<C>) new DataSizeConverter();
        } else {
            return super.getConverter(type);
        }
    }
}
