package com.bluepixel.mood.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ModeEndReason;
import com.bluepixel.mood.notification.ModeNotificationManager;

public class ModeActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (ModeNotificationManager.ACTION_STOP.equals(
                intent.getAction()
        )) {
            ModeEngine.getInstance(context)
                    .deactivate(ModeEndReason.USER_STOPPED);
        } else if (
                ModeNotificationManager.ACTION_EXTEND_30.equals(
                        intent.getAction()
                )
        ) {
            ModeEngine.getInstance(context).extend(30);
        }
    }
}
