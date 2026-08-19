package com.bluepixel.mood.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluepixel.mood.automation.ScheduleManager;
import com.bluepixel.mood.core.AppExecutors;
import com.bluepixel.mood.data.database.AppDatabase;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ActivationSource;

public class ScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long scheduleId = intent == null
                ? 0
                : intent.getLongExtra(
                        ScheduleManager.EXTRA_SCHEDULE_ID,
                        0
                );

        if (scheduleId == 0) return;

        PendingResult pendingResult = goAsync();

        AppExecutors.io().execute(() -> {
            try {
                AppDatabase database =
                        AppDatabase.getInstance(context);

                ScheduleEntity schedule =
                        database.scheduleDao()
                                .getByIdBlocking(scheduleId);

                if (schedule == null || !schedule.isEnabled()) {
                    return;
                }

                ModeEntity mode = database.modeDao()
                        .getByIdBlocking(schedule.getModeId());

                if (mode != null) {
                    AppExecutors.main().post(() ->
                            ModeEngine.getInstance(context).activate(
                                    mode,
                                    ActivationSource.SCHEDULE,
                                    new ModeEngine.Callback() {
                                        @Override
                                        public void onSuccess(
                                                com.bluepixel.mood.mode.ActiveMode activeMode
                                        ) {
                                        }

                                        @Override
                                        public void onError(
                                                String message,
                                                Throwable throwable
                                        ) {
                                        }
                                    }
                            )
                    );
                }

                new ScheduleManager(context).schedule(schedule);
            } finally {
                pendingResult.finish();
            }
        });
    }
}
