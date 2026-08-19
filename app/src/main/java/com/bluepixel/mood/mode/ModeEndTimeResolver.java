package com.bluepixel.mood.mode;

import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.model.ModeEndType;

import java.util.Calendar;

public final class ModeEndTimeResolver {

    private ModeEndTimeResolver() {
    }

    public static long resolve(ModeEntity mode, long startedAt) {
        if (ModeEndType.DURATION.equals(mode.getEndType())) {
            return startedAt
                    + Math.max(1, mode.getDurationMinutes())
                    * 60_000L;
        }

        if (ModeEndType.CLOCK.equals(mode.getEndType())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startedAt);
            calendar.set(Calendar.HOUR_OF_DAY, mode.getEndHour());
            calendar.set(Calendar.MINUTE, mode.getEndMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.getTimeInMillis() <= startedAt) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            return calendar.getTimeInMillis();
        }

        return 0L;
    }
}
