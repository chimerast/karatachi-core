package org.karatachi.wicket.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public class DateTimeConverter implements IConverter<Date> {
    private static final long serialVersionUID = 1L;

    private final int dateStyle;
    private final int timeStyle;

    public DateTimeConverter() {
        this(DateFormat.MEDIUM, DateFormat.MEDIUM);
    }

    public DateTimeConverter(int dateStyle, int timeStyle) {
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }

    private DateFormat getFormatter(Locale locale) {
        if (timeStyle < 0) {
            return DateFormat.getDateInstance(dateStyle, locale);
        } else if (dateStyle < 0) {
            return DateFormat.getTimeInstance(timeStyle, locale);
        } else {
            return DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
        }
    }

    public Date convertToObject(String value, Locale locale) {
        try {
            return getFormatter(locale).parse(value);
        } catch (ParseException e) {
            ConversionException ex = new ConversionException(e);
            ex.setConverter(this);
            ex.setSourceValue(value);
            ex.setLocale(locale);
            throw ex;
        }
    }

    public String convertToString(Date value, Locale locale) {
        return getFormatter(locale).format(value);
    }
}
