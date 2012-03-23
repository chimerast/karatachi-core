package org.karatachi.translator;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

public class ByteArrayTranslator {
    public static String toHex(byte[] data) {
        if (data == null)
            return null;

        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            if (b >= 16) {
                buf.append(Integer.toString(b, 16));
            } else if (b >= 0) {
                buf.append("0").append(Integer.toString(b, 16));
            } else {
                buf.append(Integer.toString(b + 256, 16));
            }
        }
        return buf.toString();
    }

    public static byte[] fromHex(String hexdata) {
        if (hexdata == null)
            return null;

        if (hexdata.length() % 2 != 0) {
            hexdata = "0" + hexdata;
        }

        int len = hexdata.length() / 2;
        byte[] buf = new byte[len];
        for (int i = 0; i < len; ++i) {
            int d = Integer.parseInt(hexdata.substring(i * 2, i * 2 + 2), 16);
            if (d > 127) {
                buf[i] = (byte) d;
            } else {
                buf[i] = (byte) (d - 256);
            }
        }
        return buf;
    }

    private static char[] base64EncodeTable = new char[] { 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/' };

    public static String toBase64(byte[] data) {
        StringBuffer encoded = new StringBuffer(data.length * 4 / 3);

        char[] c = new char[4];

        int i = 0;
        while (i < data.length - 2) {
            c[0] = base64EncodeTable[(data[i] & 0xfc) >> 2];
            c[1] = base64EncodeTable[((data[i] & 0x03) << 4)
                    | ((data[++i] & 0xf0) >> 4)];
            c[2] = base64EncodeTable[((data[i] & 0x0f) << 2)
                    | ((data[++i] & 0xc0) >> 6)];
            c[3] = base64EncodeTable[data[i] & 0x3f];

            encoded.append(c);
            i++;
        }

        if (i == data.length - 2) {
            c[0] = base64EncodeTable[(data[i] & 0xfc) >> 2];
            c[1] = base64EncodeTable[((data[i] & 0x03) << 4)
                    | ((data[++i] & 0xf0) >> 4)];
            c[2] = base64EncodeTable[(data[i] & 0x0f) << 2];
            c[3] = '=';
            encoded.append(c);
        } else if (i == data.length - 1) {
            c[0] = base64EncodeTable[(data[i] & 0xfc) >> 2];
            c[1] = base64EncodeTable[(data[i] & 0x03) << 4];
            c[2] = '=';
            c[3] = '=';
            encoded.append(c);
        }

        return encoded.toString();
    }

    private static int[] base64DecodeTable = new int[] { -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
            -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1,
            -1, -1 };

    public static byte[] fromBase64(String buf) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                (buf.length() * 3 / 4));

        StringReader sr;

        if (buf.endsWith("=")) {
            sr = new StringReader(buf.substring(0, buf.indexOf('=')));
        } else {
            sr = new StringReader(buf);
        }

        char[] c = new char[4];
        int[] b = new int[4];
        int len;
        try {
            int count = 0;
            while ((len = sr.read(c, 0, 4)) == 4) {
                b[0] = base64DecodeTable[c[0]];
                b[1] = base64DecodeTable[c[1]];
                b[2] = base64DecodeTable[c[2]];
                b[3] = base64DecodeTable[c[3]];

                baos.write(((b[0] & 0x3f) << 2) | ((b[1] & 0x30) >> 4));
                baos.write(((b[1] & 0x0f) << 4) | ((b[2] & 0x3c) >> 2));
                baos.write(((b[2] & 0x03) << 6) | (b[3] & 0x3f));

                count++;
                if (count == 19) {
                    sr.mark(1);
                    count = 0;

                    if (sr.read() != 10) {
                        sr.reset();
                    }
                }
            }

            b[0] = base64DecodeTable[c[0]];
            b[1] = base64DecodeTable[c[1]];
            b[2] = base64DecodeTable[c[2]];
            b[3] = base64DecodeTable[c[3]];

            if (len == 2) {
                baos.write(((b[0] & 0x3f) << 2) | ((b[1] & 0x30) >> 4));
            } else if (len == 3) {
                baos.write(((b[0] & 0x3f) << 2) | ((b[1] & 0x30) >> 4));
                baos.write(((b[1] & 0x0f) << 4) | ((b[2] & 0x3c) >> 2));
            }

            return baos.toByteArray();
        } catch (java.io.IOException e) {
            return null;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
