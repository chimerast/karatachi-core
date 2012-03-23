package org.karatachi.text;

public class UnicodeCharacter {
    private static final int[] WHITE_SPACE_CODE =
            { 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x0020, 0x0085, 0x00A0,
                    0x1680, 0x180E, 0x2000, 0x2001, 0x2002, 0x2003, 0x2004,
                    0x2005, 0x2006, 0x2007, 0x2008, 0x2009, 0x200A, 0x200B,
                    0x200C, 0x200D, 0x200E, 0x200F, 0x2028, 0x2029, 0x202F,
                    0x205F, 0x3000 };

    public static final String WHITE_SPACE_PATTERN;
    static {
        StringBuilder pattern = new StringBuilder();
        for (int c : WHITE_SPACE_CODE) {
            pattern.append(String.format("\\u%04X", c));
        }
        WHITE_SPACE_PATTERN = pattern.toString();
    }

    private static final int[] DASH_CODE =
            { 0x002D, 0x2010, 0x2011, 0x2012, 0x2013, 0x2014, 0x2015, 0x2042,
                    0x2212 };

    public static final String DASH_PATTERN;
    static {
        StringBuilder pattern = new StringBuilder();
        for (int c : DASH_CODE) {
            pattern.append(String.format("\\u%04X", c));
        }
        DASH_PATTERN = pattern.toString();
    }

    public static final String ISO_PATTERN = "\\u0000-\\u001F\\u007F-\\u009F";
}
