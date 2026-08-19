package com.bluepixel.mood.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluepixel.mood.automation.ScheduleManager;
import com.bluepixel.mood.mode.ModeEngine;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ModeEngine.getInstance(context).restoreAfterBoot();
        new ScheduleManager(context).scheduleAllAsync();
    }
}
