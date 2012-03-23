package org.karatachi.net.rsh;

public class Rsh {
    public static final String CONTROL_PROMPT = "> ";

    public static final int EOT = 0x04;
    public static final int BS = 0x08;
    public static final int LF = 0x0A;
    public static final int CR = 0x0D;
    public static final int EOF = 0x1A;
    public static final int DEL = 0x7F;

    public static final int TELNET_WILL = 0xFB;
    public static final int TELNET_WONT = 0xFC;
    public static final int TELNET_DO = 0xFD;
    public static final int TELNET_DONT = 0xFE;
    public static final int TELNET_IAC = 0xFF;

    public static final int OPT_ECHO = 1;
    public static final int OPT_SGA = 3;

    public static byte[] constructOption(int verb, int opt) {
        byte[] buff = new byte[3];
        buff[0] = (byte) TELNET_IAC;
        buff[1] = (byte) verb;
        buff[2] = (byte) opt;
        return buff;
    }
}
