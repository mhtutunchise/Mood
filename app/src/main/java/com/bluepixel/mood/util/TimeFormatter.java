package com.bluepixel.mood.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TimeFormatter {

    private TimeFormatter() {
    }

    public static String time(long millis) {
        return time(null, millis);
    }

    public static String time(
            @Nullable Context context,
            long millis
    ) {
        boolean persian =
                isPersian(context);

        Locale locale =
                persian
                        ? new Locale("fa")
                        : Locale.US;

        String result =
                new SimpleDateFormat(
                        "HH:mm",
                        locale
                ).format(new Date(millis));

        return persian
                ? PersianDigits.convert(result)
                : result;
    }

    public static String dateTime(long millis) {
        return dateTime(null, millis);
    }

    public static String dateTime(
            @Nullable Context context,
            long millis
    ) {
        boolean persian =
                isPersian(context);

        Locale locale =
                persian
                        ? new Locale("fa")
                        : Locale.US;

        String pattern =
                persian
                        ? "yyyy/MM/dd  HH:mm"
                        : "yyyy/MM/dd  HH:mm";

        String result =
                new SimpleDateFormat(
                        pattern,
                        locale
                ).format(new Date(millis));

        return persian
                ? PersianDigits.convert(result)
                : result;
    }

    public static String duration(long millis) {
        return duration(null, millis);
    }

    public static String duration(
            @Nullable Context context,
            long millis
    ) {
        boolean persian =
                isPersian(context);

        long minutes =
                Math.max(0, millis / 60_000L);

        long hours =
                minutes / 60;

        long remaining =
                minutes % 60;

        if (persian) {
            if (hours > 0 && remaining > 0) {
                return PersianDigits.from(hours)
                        + " ساعت و "
                        + PersianDigits.from(remaining)
                        + " دقیقه";
            }

            if (hours > 0) {
                return PersianDigits.from(hours)
                        + " ساعت";
            }

            return PersianDigits.from(minutes)
                    + " دقیقه";
        }

        if (hours > 0 && remaining > 0) {
            return hours
                    + " "
                    + plural(hours, "hour", "hours")
                    + " and "
                    + remaining
                    + " "
                    + plural(
                    remaining,
                    "minute",
                    "minutes"
            );
        }

        if (hours > 0) {
            return hours
                    + " "
                    + plural(
                    hours,
                    "hour",
                    "hours"
            );
        }

        return minutes
                + " "
                + plural(
                minutes,
                "minute",
                "minutes"
        );
    }

    public static String number(
            @Nullable Context context,
            long value
    ) {
        return isPersian(context)
                ? PersianDigits.from(value)
                : String.valueOf(value);
    }

    public static boolean isPersian(
            @Nullable Context context
    ) {
        LocaleListCompat appLocales =
                AppCompatDelegate
                        .getApplicationLocales();

        if (!appLocales.isEmpty()
                && appLocales.get(0) != null) {
            return "fa".equals(
                    appLocales.get(0).getLanguage()
            );
        }

        if (context != null) {
            Configuration configuration =
                    context.getResources()
                            .getConfiguration();

            Locale locale;

            if (Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.N) {
                locale =
                        configuration
                                .getLocales()
                                .get(0);
            } else {
                locale =
                        configuration.locale;
            }

            return locale != null
                    && "fa".equals(
                    locale.getLanguage()
            );
        }

        return "fa".equals(
                Locale.getDefault().getLanguage()
        );
    }

    @NonNull
    private static String plural(
            long value,
            @NonNull String singular,
            @NonNull String plural
    ) {
        return value == 1
                ? singular
                : plural;
    }
}
