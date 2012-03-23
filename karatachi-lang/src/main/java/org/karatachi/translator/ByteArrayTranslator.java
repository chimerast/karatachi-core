package org.karatachi.translator;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Random;

public class ByteArrayTranslator {
    public static int toInt(byte[] data) {
        int ret = 0;
        for (byte b : data) {
            ret <<= 8;
            ret |= b & 0xFF;
        }
        return ret;
    }

    public static long toLong(byte[] data) {
        long ret = 0;
        for (byte b : data) {
            ret <<= 8;
            ret |= b & 0xFF;
        }
        return ret;
    }

    public static String toHex(byte[] data) {
        if (data == null) {
            return null;
        }

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

    public static byte[] fromHex(String code) {
        if (code == null) {
            return null;
        }

        if (code.length() % 2 != 0) {
            code = "0" + code;
        }

        int len = code.length() / 2;
        byte[] buf = new byte[len];
        for (int i = 0; i < len; ++i) {
            int d = Integer.parseInt(code.substring(i * 2, i * 2 + 2), 16);
            if (d > 127) {
                buf[i] = (byte) d;
            } else {
                buf[i] = (byte) (d - 256);
            }
        }
        return buf;
    }

    private static class Base64 {
        private final char padding;
        private final char[] encode;
        private final int[] decode;

        public Base64(String code, char padding) {
            this.padding = padding;

            this.encode = code.toCharArray();
            this.decode = new int[128];
            for (int i = 0; i < decode.length; ++i) {
                decode[i] = -1;
            }
            for (int i = 0; i < encode.length; ++i) {
                decode[encode[i]] = i;
            }
        }

        public String encode(byte[] data) {
            StringBuffer encoded = new StringBuffer(data.length * 4 / 3);

            char[] c = new char[4];

            int i = 0;
            while (i < data.length - 2) {
                c[0] = encode[(data[i] & 0xfc) >> 2];
                c[1] =
                        encode[((data[i] & 0x03) << 4)
                                | ((data[++i] & 0xf0) >> 4)];
                c[2] =
                        encode[((data[i] & 0x0f) << 2)
                                | ((data[++i] & 0xc0) >> 6)];
                c[3] = encode[data[i] & 0x3f];

                encoded.append(c);
                i++;
            }

            if (i == data.length - 2) {
                c[0] = encode[(data[i] & 0xfc) >> 2];
                c[1] =
                        encode[((data[i] & 0x03) << 4)
                                | ((data[++i] & 0xf0) >> 4)];
                c[2] = encode[(data[i] & 0x0f) << 2];
                c[3] = padding;
                encoded.append(c);
            } else if (i == data.length - 1) {
                c[0] = encode[(data[i] & 0xfc) >> 2];
                c[1] = encode[(data[i] & 0x03) << 4];
                c[2] = padding;
                c[3] = padding;
                encoded.append(c);
            }

            return encoded.toString();
        }

        public byte[] decode(String code) {
            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream((code.length() * 3 / 4));

            StringReader sr;

            if (code.endsWith(Character.toString(padding))) {
                sr = new StringReader(code.substring(0, code.indexOf(padding)));
            } else {
                sr = new StringReader(code);
            }

            char[] c = new char[4];
            int[] b = new int[4];
            int len;
            try {
                int count = 0;
                while ((len = sr.read(c, 0, 4)) == 4) {
                    b[0] = decode[c[0]];
                    b[1] = decode[c[1]];
                    b[2] = decode[c[2]];
                    b[3] = decode[c[3]];

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

                b[0] = decode[c[0]];
                b[1] = decode[c[1]];
                b[2] = decode[c[2]];
                b[3] = decode[c[3]];

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

    private static final Base64 BASE64_INSTANCE = new Base64(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",
            '=');

    public static String toBase64(byte[] data) {
        return BASE64_INSTANCE.encode(data);
    }

    public static byte[] fromBase64(String code) {
        return BASE64_INSTANCE.decode(code);
    }

    private static final Base64 URL_BASE64_INSTANCE = new Base64(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789._",
            '-');

    public static String toURLFormedBase64(byte[] data) {
        return URL_BASE64_INSTANCE.encode(data);
    }

    public static byte[] fromURLFormedBase64(String code) {
        return URL_BASE64_INSTANCE.decode(code);
    }

    public static void main(String[] args) {
        Random rand = new Random();
        for (int i = 0; i < 16; ++i) {
            byte[] a = new byte[1024 + rand.nextInt(2048)];
            rand.nextBytes(a);
            byte[] a2 = fromBase64(toBase64(a));
            System.out.println(Arrays.equals(a, a2));
        }
    }
}
