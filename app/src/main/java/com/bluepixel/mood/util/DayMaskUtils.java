package com.bluepixel.mood.util;

import java.util.Calendar;

public final class DayMaskUtils {

    public static final int SATURDAY = 1;
    public static final int SUNDAY = 1 << 1;
    public static final int MONDAY = 1 << 2;
    public static final int TUESDAY = 1 << 3;
    public static final int WEDNESDAY = 1 << 4;
    public static final int THURSDAY = 1 << 5;
    public static final int FRIDAY = 1 << 6;

    private DayMaskUtils() {
    }

    public static int bitForCalendarDay(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SATURDAY:
                return SATURDAY;
            case Calendar.SUNDAY:
                return SUNDAY;
            case Calendar.MONDAY:
                return MONDAY;
            case Calendar.TUESDAY:
                return TUESDAY;
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case Calendar.THURSDAY:
                return THURSDAY;
            case Calendar.FRIDAY:
            default:
                return FRIDAY;
        }
    }

    public static String label(int mask) {
        if (mask == 127) return "هر روز";

        StringBuilder builder = new StringBuilder();
        append(builder, mask, SATURDAY, "ش");
        append(builder, mask, SUNDAY, "ی");
        append(builder, mask, MONDAY, "د");
        append(builder, mask, TUESDAY, "س");
        append(builder, mask, WEDNESDAY, "چ");
        append(builder, mask, THURSDAY, "پ");
        append(builder, mask, FRIDAY, "ج");

        return builder.length() == 0
                ? "بدون روز"
                : builder.toString();
    }

    private static void append(
            StringBuilder builder,
            int mask,
            int bit,
            String label
    ) {
        if ((mask & bit) == 0) return;
        if (builder.length() > 0) builder.append("، ");
        builder.append(label);
    }
}
