package org.karatachi.wicket.label;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.karatachi.wicket.converter.InetAddressConverter;

public class InetAddressLabel extends Label {
    private static final long serialVersionUID = 1L;

    public InetAddressLabel(String id) {
        super(id);
    }

    public InetAddressLabel(String id, long size) {
        super(id, new Model<Long>(size));
    }

    public InetAddressLabel(String id, IModel<Long> model) {
        super(id, model);
    }

    @Override
    public IConverter getConverter(Class<?> type) {
        return new InetAddressConverter();
    }
}
