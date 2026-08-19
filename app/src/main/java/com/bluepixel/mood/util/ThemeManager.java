package com.bluepixel.mood.util;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.bluepixel.mood.data.preferences.AppPreferences;

public final class ThemeManager {

    private ThemeManager() {
        // جلوگیری از ساخته‌شدن نمونه از این کلاس
    }

    public static void apply(Context context) {
        AppPreferences preferences =
                AppPreferences.getInstance(
                        context.getApplicationContext()
                );

        AppCompatDelegate.setDefaultNightMode(
                preferences.getNightMode()
        );
    }

    public static void setTheme(
            Context context,
            int nightMode
    ) {
        AppPreferences preferences =
                AppPreferences.getInstance(
                        context.getApplicationContext()
                );

        preferences.setNightMode(nightMode);

        AppCompatDelegate.setDefaultNightMode(
                nightMode
        );
    }
}