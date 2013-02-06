package org.karatachi.wicket.converter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public class InetAddressConverter implements IConverter<InetAddress> {
    private static final long serialVersionUID = 1L;

    public InetAddress convertToObject(String value, Locale locale) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            throw new ConversionException(e).setConverter(this).setSourceValue(
                    value).setTargetType(InetAddress.class).setLocale(locale);
        }
    }

    public String convertToString(InetAddress value, Locale locale) {
        return value.getHostAddress();
    }
}
