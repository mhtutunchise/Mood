package com.bluepixel.mood.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.bluepixel.mood.core.AppExecutors;
import com.bluepixel.mood.data.database.AppDatabase;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ActivationSource;
import com.bluepixel.mood.model.ModeEndReason;

public class MoodWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_RUN =
            "com.bluepixel.mood.widget.RUN";
    public static final String ACTION_STOP =
            "com.bluepixel.mood.widget.STOP";
    public static final String EXTRA_VISUAL_TYPE =
            "visual_type";

    @Override
    public void onUpdate(
            Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds
    ) {
        for (int id : appWidgetIds) {
            appWidgetManager.updateAppWidget(
                    id,
                    WidgetUpdater.createViews(context)
            );
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (ACTION_STOP.equals(intent.getAction())) {
            ModeEngine.getInstance(context)
                    .deactivate(ModeEndReason.USER_STOPPED);
            return;
        }

        if (!ACTION_RUN.equals(intent.getAction())) {
            return;
        }

        String visualType = intent.getStringExtra(
                EXTRA_VISUAL_TYPE
        );

        PendingResult pendingResult = goAsync();

        AppExecutors.io().execute(() -> {
            try {
                ModeEntity mode = AppDatabase
                        .getInstance(context)
                        .modeDao()
                        .getFirstByVisualTypeBlocking(visualType);

                if (mode != null) {
                    ModeEngine.getInstance(context).activate(
                            mode,
                            ActivationSource.WIDGET,
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
                    );
                }
            } finally {
                pendingResult.finish();
            }
        });
    }
}
