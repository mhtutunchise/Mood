package com.bluepixel.mood.mode;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.bluepixel.mood.receiver.ModeEndReceiver;

public class ModeScheduler {

    private static final int REQUEST_CODE = 7201;

    private final Context context;
    private final AlarmManager alarmManager;

    public ModeScheduler(Context context) {
        this.context = context.getApplicationContext();
        alarmManager = (AlarmManager)
                this.context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(long triggerAtMillis) {
        cancel();

        long delay = Math.max(
                1_000L,
                triggerAtMillis - System.currentTimeMillis()
        );

        alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delay,
                pendingIntent()
        );
    }

    public void cancel() {
        alarmManager.cancel(pendingIntent());
    }

    private PendingIntent pendingIntent() {
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                new Intent(context, ModeEndReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
