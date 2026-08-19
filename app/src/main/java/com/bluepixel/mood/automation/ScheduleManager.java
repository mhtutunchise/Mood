package com.bluepixel.mood.automation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.bluepixel.mood.core.AppExecutors;
import com.bluepixel.mood.data.database.AppDatabase;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;
import com.bluepixel.mood.receiver.ScheduleReceiver;

import java.util.List;

public class ScheduleManager {

    public static final String EXTRA_SCHEDULE_ID =
            "schedule_id";

    private final Context context;
    private final AlarmManager alarmManager;
    private final AppDatabase database;

    public ScheduleManager(Context context) {
        this.context = context.getApplicationContext();
        alarmManager = (AlarmManager)
                this.context.getSystemService(Context.ALARM_SERVICE);
        database = AppDatabase.getInstance(this.context);
    }

    public void scheduleAllAsync() {
        AppExecutors.io().execute(() -> {
            List<ScheduleEntity> schedules =
                    database.scheduleDao().getEnabledBlocking();

            for (ScheduleEntity schedule : schedules) {
                schedule(schedule);
            }
        });
    }

    public void schedule(ScheduleEntity schedule) {
        cancel(schedule.getId());

        if (!schedule.isEnabled()) return;

        long triggerAt = NextRunCalculator.nextRun(
                schedule,
                System.currentTimeMillis()
        );

        alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent(schedule.getId())
        );
    }

    public void cancel(long scheduleId) {
        alarmManager.cancel(pendingIntent(scheduleId));
    }

    private PendingIntent pendingIntent(long scheduleId) {
        Intent intent = new Intent(
                context,
                ScheduleReceiver.class
        );
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);

        return PendingIntent.getBroadcast(
                context,
                requestCode(scheduleId),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private int requestCode(long id) {
        return (int) (9000 + (id % 1_000_000));
    }
}
