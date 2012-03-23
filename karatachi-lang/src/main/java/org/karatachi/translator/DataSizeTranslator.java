package org.karatachi.translator;

import java.text.DecimalFormat;

public class DataSizeTranslator {
    public static final String[] UNIT_SUFFIX = { "", "K", "M", "G", "T", "P" };
    public static final long DEFAULT_BINARY_UNITS = 1024;

    public static final DecimalFormat DECIMAL_FORMAT =
            new DecimalFormat("0.00");

    public static long toLong(String value) {
        value = value.trim();
        if (value.length() == 0) {
            return -1;
        }

        try {
            int exp;
            switch (Character.toLowerCase(value.charAt(value.length() - 1))) {
            case 'k':
                exp = 1;
                break;
            case 'm':
                exp = 2;
                break;
            case 'g':
                exp = 3;
                break;
            case 't':
                exp = 4;
                break;
            case 'p':
                exp = 5;
                break;
            default:
                exp = 0;
                break;
            }

            if (exp != 0) {
                value = value.substring(0, value.length() - 1).trim();
            }

            double ret = Double.parseDouble(value);
            for (int i = 0; i < exp; ++i) {
                ret *= DEFAULT_BINARY_UNITS;
            }

            return (long) ret;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String toString(long value) {
        double ret = ((Long) value).doubleValue();

        int exp = 0;
        while (ret >= DEFAULT_BINARY_UNITS) {
            ret /= DEFAULT_BINARY_UNITS;
            ++exp;
        }

        if (exp != 0) {
            return DECIMAL_FORMAT.format(ret) + " " + UNIT_SUFFIX[exp];
        } else {
            return Long.toString((long) ret);
        }
    }
}
