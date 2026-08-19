package com.bluepixel.mood.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.bluepixel.mood.R;
import com.bluepixel.mood.mode.ActiveMode;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ModeVisualType;
import com.bluepixel.mood.util.TimeFormatter;

public final class WidgetUpdater {

    private WidgetUpdater() {
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager =
                AppWidgetManager.getInstance(context);

        ComponentName component = new ComponentName(
                context,
                MoodWidgetProvider.class
        );

        int[] ids = manager.getAppWidgetIds(component);
        for (int id : ids) {
            manager.updateAppWidget(
                    id,
                    createViews(context)
            );
        }
    }

    public static RemoteViews createViews(Context context) {
        RemoteViews views = new RemoteViews(
                context.getPackageName(),
                R.layout.widget_mood
        );

        ActiveMode activeMode =
                ModeEngine.getInstance(context).getActiveMode();

        if (activeMode == null) {
            views.setTextViewText(
                    R.id.textWidgetStatus,
                    "حالت عادی"
            );
            views.setTextViewText(
                    R.id.textWidgetHint,
                    "یک حالت را انتخاب کن"
            );
            views.setViewVisibility(
                    R.id.buttonWidgetStop,
                    View.GONE
            );
        } else {
            views.setTextViewText(
                    R.id.textWidgetStatus,
                    activeMode.modeName
            );
            views.setTextViewText(
                    R.id.textWidgetHint,
                    activeMode.hasAutomaticEnd()
                            ? "پایان "
                            + TimeFormatter.time(
                            activeMode.expectedEndAt
                    )
                            : "فعال تا پایان دستی"
            );
            views.setViewVisibility(
                    R.id.buttonWidgetStop,
                    View.VISIBLE
            );
        }

        setAction(
                context,
                views,
                R.id.buttonWidgetSleep,
                MoodWidgetProvider.ACTION_RUN,
                ModeVisualType.SLEEP,
                101
        );
        setAction(
                context,
                views,
                R.id.buttonWidgetMeeting,
                MoodWidgetProvider.ACTION_RUN,
                ModeVisualType.MEETING,
                102
        );
        setAction(
                context,
                views,
                R.id.buttonWidgetStudy,
                MoodWidgetProvider.ACTION_RUN,
                ModeVisualType.STUDY,
                103
        );
        setAction(
                context,
                views,
                R.id.buttonWidgetOutdoor,
                MoodWidgetProvider.ACTION_RUN,
                ModeVisualType.OUTDOOR,
                104
        );
        setAction(
                context,
                views,
                R.id.buttonWidgetStop,
                MoodWidgetProvider.ACTION_STOP,
                "",
                105
        );

        return views;
    }

    private static void setAction(
            Context context,
            RemoteViews views,
            int viewId,
            String action,
            String visualType,
            int requestCode
    ) {
        Intent intent = new Intent(
                context,
                MoodWidgetProvider.class
        );
        intent.setAction(action);
        intent.putExtra(
                MoodWidgetProvider.EXTRA_VISUAL_TYPE,
                visualType
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        views.setOnClickPendingIntent(viewId, pendingIntent);
    }
}
