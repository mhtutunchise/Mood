package com.bluepixel.mood.automation;

import com.bluepixel.mood.data.database.entity.ScheduleEntity;
import com.bluepixel.mood.util.DayMaskUtils;

import java.util.Calendar;

public final class NextRunCalculator {

    private NextRunCalculator() {
    }

    public static long nextRun(
            ScheduleEntity schedule,
            long nowMillis
    ) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(nowMillis);

        for (int offset = 0; offset < 8; offset++) {
            Calendar candidate = (Calendar) now.clone();
            candidate.add(Calendar.DAY_OF_YEAR, offset);
            candidate.set(
                    Calendar.HOUR_OF_DAY,
                    schedule.getHour()
            );
            candidate.set(
                    Calendar.MINUTE,
                    schedule.getMinute()
            );
            candidate.set(Calendar.SECOND, 0);
            candidate.set(Calendar.MILLISECOND, 0);

            int bit = DayMaskUtils.bitForCalendarDay(
                    candidate.get(Calendar.DAY_OF_WEEK)
            );

            if ((schedule.getDaysMask() & bit) != 0
                    && candidate.getTimeInMillis() > nowMillis) {
                return candidate.getTimeInMillis();
            }
        }

        return nowMillis + 24L * 60L * 60L * 1000L;
    }
}
