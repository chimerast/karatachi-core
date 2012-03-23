package org.karatachi.wicket.converter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public class InetAddressConverter implements IConverter {
    private static final long serialVersionUID = 1L;

    public Object convertToObject(String value, Locale locale) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            throw new ConversionException(e).setConverter(this).setSourceValue(
                    value).setTargetType(InetAddress.class).setLocale(locale);
        }
    }

    public String convertToString(Object value, Locale locale) {
        InetAddress i = (InetAddress) value;
        return i.getHostAddress();
    }
}
