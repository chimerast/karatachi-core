package org.karatachi.wicket.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;
import org.karatachi.translator.DataSizeTranslator;

public class DataSizeConverter implements IConverter<Number> {
    private static final long serialVersionUID = 1L;

    public Number convertToObject(String value, Locale locale) {
        return DataSizeTranslator.toLong(value);
    }

    public String convertToString(Number value, Locale locale) {
        if (value.longValue() < 0) {
            return "無効";
        } else {
            return DataSizeTranslator.toString(value.longValue());
        }
    }
}
