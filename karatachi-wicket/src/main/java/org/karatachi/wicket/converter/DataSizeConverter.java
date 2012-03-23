package org.karatachi.wicket.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;
import org.karatachi.translator.DataSizeTranslator;

public class DataSizeConverter implements IConverter {
    private static final long serialVersionUID = 1L;

    public Object convertToObject(String value, Locale locale) {
        return DataSizeTranslator.toLong(value);
    }

    public String convertToString(Object value, Locale locale) {
        Number l = (Number) value;
        if (l.longValue() < 0) {
            return "無効";
        } else {
            return DataSizeTranslator.toString(l.longValue());
        }
    }
}
