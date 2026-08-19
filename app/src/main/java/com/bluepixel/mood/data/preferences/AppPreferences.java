package com.bluepixel.mood.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class AppPreferences {

    private static final String PREFS = "mood_preferences";
    private static final String KEY_ONBOARDING = "onboarding_completed";
    private static final String KEY_THEME = "theme_mode";

    private static volatile AppPreferences instance;
    private final SharedPreferences preferences;

    private AppPreferences(Context context) {
        preferences = context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    public static AppPreferences getInstance(Context context) {
        if (instance == null) {
            synchronized (AppPreferences.class) {
                if (instance == null) {
                    instance = new AppPreferences(
                            context.getApplicationContext()
                    );
                }
            }
        }
        return instance;
    }

    public boolean isOnboardingCompleted() {
        return preferences.getBoolean(KEY_ONBOARDING, false);
    }

    public void setOnboardingCompleted(boolean value) {
        preferences.edit()
                .putBoolean(KEY_ONBOARDING, value)
                .apply();
    }

    public int getNightMode() {
        return preferences.getInt(
                KEY_THEME,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        );
    }

    public void setNightMode(int value) {
        preferences.edit().putInt(KEY_THEME, value).apply();
    }
}
