package org.karatachi.translator;

public class IntervalTranslator {
    public static long day(long day) {
        return day * 24 * 60 * 60 * 1000;
    }

    public static long hour(long hour) {
        return hour * 60 * 60 * 1000;
    }

    public static long min(long min) {
        return min * 60 * 1000;
    }

    public static long sec(long sec) {
        return sec * 1000;
    }
}
