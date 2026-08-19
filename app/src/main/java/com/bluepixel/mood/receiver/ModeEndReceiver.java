package com.bluepixel.mood.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ModeEndReason;

public class ModeEndReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ModeEngine.getInstance(context)
                .deactivate(ModeEndReason.TIME_FINISHED);
    }
}
