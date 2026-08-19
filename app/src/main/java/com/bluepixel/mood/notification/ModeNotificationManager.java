package com.bluepixel.mood.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bluepixel.mood.R;
import com.bluepixel.mood.mode.ActiveMode;
import com.bluepixel.mood.receiver.ModeActionReceiver;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.bluepixel.mood.ui.main.MainActivity;
import com.bluepixel.mood.util.TimeFormatter;

public class ModeNotificationManager {

    public static final String ACTION_STOP =
            "com.bluepixel.mood.action.STOP";

    public static final String ACTION_EXTEND_30 =
            "com.bluepixel.mood.action.EXTEND_30";

    private static final String CHANNEL_ID =
            "active_mode_channel";

    private static final int NOTIFICATION_ID = 501;

    private final Context context;
    private final NotificationManager manager;

    public ModeNotificationManager(Context context) {
        this.context =
                context.getApplicationContext();

        manager =
                (NotificationManager)
                        this.context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );
    }

    public void show(ActiveMode activeMode) {
        if (!canPostNotifications()) {
            return;
        }

        createChannel();

        PendingIntent openApp =
                PendingIntent.getActivity(
                        context,
                        1,
                        new Intent(
                                context,
                                MainActivity.class
                        ),
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        String modeName =
                ModeTextResolver.name(
                        context,
                        activeMode.visualType,
                        activeMode.modeName
                );

        String content =
                activeMode.hasAutomaticEnd()
                        ? context.getString(
                        R.string.notification_auto_end_at,
                        TimeFormatter.time(
                                context,
                                activeMode.expectedEndAt
                        )
                )
                        : context.getString(
                        R.string.notification_manual_end_hint
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                R.drawable.ic_notification_mood
                        )
                        .setContentTitle(
                                context.getString(
                                        R.string.notification_active_mode_title,
                                        modeName
                                )
                        )
                        .setContentText(content)
                        .setContentIntent(openApp)
                        .setOnlyAlertOnce(true)
                        .setOngoing(true)
                        .setCategory(
                                NotificationCompat.CATEGORY_STATUS
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_LOW
                        )
                        .addAction(
                                R.drawable.ic_stop,
                                context.getString(
                                        R.string.notification_action_stop
                                ),
                                actionPendingIntent(
                                        ACTION_STOP,
                                        2
                                )
                        );

        if (activeMode.hasAutomaticEnd()) {
            builder.addAction(
                    R.drawable.ic_add,
                    context.getString(
                            R.string.notification_action_extend_30
                    ),
                    actionPendingIntent(
                            ACTION_EXTEND_30,
                            3
                    )
            );
        }

        manager.notify(
                NOTIFICATION_ID,
                builder.build()
        );
    }

    public void cancel() {
        manager.cancel(NOTIFICATION_ID);
    }

    public boolean canPostNotifications() {
        return Build.VERSION.SDK_INT
                < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private PendingIntent actionPendingIntent(
            String action,
            int requestCode
    ) {
        Intent intent =
                new Intent(
                        context,
                        ModeActionReceiver.class
                );

        intent.setAction(action);

        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT
                < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        context.getString(
                                R.string.notification_channel_active_mode
                        ),
                        NotificationManager.IMPORTANCE_LOW
                );

        channel.setDescription(
                context.getString(
                        R.string.notification_channel_active_mode_description
                )
        );

        channel.setShowBadge(false);

        manager.createNotificationChannel(channel);
    }
}
