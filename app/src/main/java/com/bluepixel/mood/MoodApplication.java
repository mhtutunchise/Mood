package com.bluepixel.mood;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.bluepixel.mood.data.preferences.AppPreferences;

public class MoodApplication extends Application {

    private static final String PREFS_NAME =
            "mood_app_startup";

    private static final String KEY_INITIAL_LANGUAGE_APPLIED =
            "initial_language_applied";

    @Override
    public void onCreate() {
        super.onCreate();

        applyInitialLanguageIfNeeded();

        AppCompatDelegate.setDefaultNightMode(
                AppPreferences.getInstance(this)
                        .getNightMode()
        );
    }

    private void applyInitialLanguageIfNeeded() {
        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        boolean initialLanguageApplied =
                preferences.getBoolean(
                        KEY_INITIAL_LANGUAGE_APPLIED,
                        false
                );

        if (initialLanguageApplied) {
            return;
        }

        if (AppCompatDelegate
                .getApplicationLocales()
                .isEmpty()) {

            AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags("fa")
            );
        }

        preferences.edit()
                .putBoolean(
                        KEY_INITIAL_LANGUAGE_APPLIED,
                        true
                )
                .apply();
    }
}