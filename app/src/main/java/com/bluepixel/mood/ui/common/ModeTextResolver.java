package com.bluepixel.mood.ui.common;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluepixel.mood.R;
import com.bluepixel.mood.data.database.entity.ModeEntity;

import java.util.Locale;

public final class ModeTextResolver {

    private ModeTextResolver() {
    }

    public static String name(
            @NonNull Context context,
            @NonNull ModeEntity mode
    ) {
        return name(
                context,
                mode.getVisualType(),
                mode.getName()
        );
    }

    public static String description(
            @NonNull Context context,
            @NonNull ModeEntity mode
    ) {
        return description(
                context,
                mode.getVisualType(),
                mode.getDescription()
        );
    }

    public static String name(
            @NonNull Context context,
            @Nullable String visualType,
            @Nullable String fallbackName
    ) {
        String type =
                normalize(visualType);

        switch (type) {
            case "sleep":
                return context.getString(
                        R.string.resolver_mode_sleep_name
                );

            case "meeting":
                return context.getString(
                        R.string.resolver_mode_meeting_name
                );

            case "study":
                return context.getString(
                        R.string.resolver_mode_study_name
                );

            case "outdoor":
                return context.getString(
                        R.string.resolver_mode_outdoor_name
                );

            case "drive":
            case "driving":
                return context.getString(
                        R.string.resolver_mode_drive_name
                );

            case "gym":
            case "workout":
                return context.getString(
                        R.string.resolver_mode_gym_name
                );

            default:
                return safeFallback(
                        fallbackName,
                        context.getString(
                                R.string.resolver_mode_custom_name
                        )
                );
        }
    }

    public static String description(
            @NonNull Context context,
            @Nullable String visualType,
            @Nullable String fallbackDescription
    ) {
        String type =
                normalize(visualType);

        switch (type) {
            case "sleep":
                return context.getString(
                        R.string.resolver_mode_sleep_description
                );

            case "meeting":
                return context.getString(
                        R.string.resolver_mode_meeting_description
                );

            case "study":
                return context.getString(
                        R.string.resolver_mode_study_description
                );

            case "outdoor":
                return context.getString(
                        R.string.resolver_mode_outdoor_description
                );

            case "drive":
            case "driving":
                return context.getString(
                        R.string.resolver_mode_drive_description
                );

            case "gym":
            case "workout":
                return context.getString(
                        R.string.resolver_mode_gym_description
                );

            default:
                return safeFallback(
                        fallbackDescription,
                        context.getString(
                                R.string.resolver_mode_custom_description
                        )
                );
        }
    }

    private static String normalize(
            @Nullable String value
    ) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.US);
    }

    private static String safeFallback(
            @Nullable String value,
            @NonNull String defaultValue
    ) {
        if (value == null
                || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }
}
