package com.bluepixel.mood.model;

import android.app.NotificationManager;
import android.media.AudioManager;

public final class ModeActions {

    public static final int UNCHANGED = -1;

    private ModeActions() {
    }

    public static int ringerFromPosition(int position) {
        switch (position) {
            case 1:
                return AudioManager.RINGER_MODE_NORMAL;
            case 2:
                return AudioManager.RINGER_MODE_VIBRATE;
            case 3:
                return AudioManager.RINGER_MODE_SILENT;
            default:
                return UNCHANGED;
        }
    }

    public static int ringerToPosition(int value) {
        if (value == AudioManager.RINGER_MODE_NORMAL) return 1;
        if (value == AudioManager.RINGER_MODE_VIBRATE) return 2;
        if (value == AudioManager.RINGER_MODE_SILENT) return 3;
        return 0;
    }

    public static int dndFromPosition(int position) {
        switch (position) {
            case 1:
                return NotificationManager.INTERRUPTION_FILTER_ALL;
            case 2:
                return NotificationManager.INTERRUPTION_FILTER_PRIORITY;
            case 3:
                return NotificationManager.INTERRUPTION_FILTER_ALARMS;
            case 4:
                return NotificationManager.INTERRUPTION_FILTER_NONE;
            default:
                return UNCHANGED;
        }
    }

    public static int dndToPosition(int value) {
        if (value == NotificationManager.INTERRUPTION_FILTER_ALL) return 1;
        if (value == NotificationManager.INTERRUPTION_FILTER_PRIORITY) return 2;
        if (value == NotificationManager.INTERRUPTION_FILTER_ALARMS) return 3;
        if (value == NotificationManager.INTERRUPTION_FILTER_NONE) return 4;
        return 0;
    }
}
