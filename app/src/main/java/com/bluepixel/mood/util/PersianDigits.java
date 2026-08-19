package com.bluepixel.mood.util;

public final class PersianDigits {

    private static final char[] ENGLISH =
            {'0','1','2','3','4','5','6','7','8','9'};
    private static final char[] PERSIAN =
            {'۰','۱','۲','۳','۴','۵','۶','۷','۸','۹'};

    private PersianDigits() {
    }

    public static String from(long value) {
        return convert(String.valueOf(value));
    }

    public static String convert(String value) {
        if (value == null) return "";
        String result = value;
        for (int i = 0; i < ENGLISH.length; i++) {
            result = result.replace(ENGLISH[i], PERSIAN[i]);
        }
        return result;
    }
}
