package com.bluepixel.mood.mode;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;

import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.model.ModeActions;

import java.util.ArrayList;
import java.util.List;

public class ModePermissionChecker {

    private final Context context;
    private final NotificationManager notificationManager;

    public ModePermissionChecker(Context context) {
        this.context = context.getApplicationContext();
        notificationManager = (NotificationManager)
                this.context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );
    }

    public List<ModePermission> getMissingPermissions(
            ModeEntity mode
    ) {
        List<ModePermission> result = new ArrayList<>();

        if (mode.getInterruptionFilter()
                != ModeActions.UNCHANGED
                && !hasNotificationPolicyAccess()) {
            result.add(ModePermission.NOTIFICATION_POLICY);
        }

        if (mode.isChangeBrightness()
                && !hasWriteSettingsAccess()) {
            result.add(ModePermission.WRITE_SETTINGS);
        }

        return result;
    }

    public boolean hasNotificationPolicyAccess() {
        return notificationManager
                .isNotificationPolicyAccessGranted();
    }

    public boolean hasWriteSettingsAccess() {
        return Settings.System.canWrite(context);
    }
}
