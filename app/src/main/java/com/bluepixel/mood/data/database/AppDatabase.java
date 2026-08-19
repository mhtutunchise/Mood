package com.bluepixel.mood.data.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bluepixel.mood.core.AppExecutors;
import com.bluepixel.mood.data.database.dao.HistoryDao;
import com.bluepixel.mood.data.database.dao.ModeDao;
import com.bluepixel.mood.data.database.dao.ScheduleDao;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.database.entity.ModeHistoryEntity;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;

@Database(
        entities = {
                ModeEntity.class,
                ModeHistoryEntity.class,
                ScheduleEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String PREFS_NAME =
            "mood_database_prefs";

    private static final String KEY_DEFAULT_MODES_SEEDED =
            "default_modes_seeded";

    private static volatile AppDatabase instance;

    public abstract ModeDao modeDao();

    public abstract HistoryDao historyDao();

    public abstract ScheduleDao scheduleDao();

    public static AppDatabase getInstance(
            Context context
    ) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    Context appContext =
                            context.getApplicationContext();

                    instance =
                            Room.databaseBuilder(
                                            appContext,
                                            AppDatabase.class,
                                            "mood_database"
                                    )
                                    .build();

                    seedIfNeeded(
                            appContext,
                            instance
                    );
                }
            }
        }

        return instance;
    }

    private static void seedIfNeeded(
            Context context,
            AppDatabase database
    ) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        AppExecutors.io().execute(() -> {
            boolean alreadySeeded =
                    preferences.getBoolean(
                            KEY_DEFAULT_MODES_SEEDED,
                            false
                    );

            if (alreadySeeded) {
                return;
            }

            if (database.modeDao()
                    .countBlocking() == 0) {
                database.modeDao()
                        .insertAll(
                                DefaultModeSeeder.createModes()
                        );
            }

            /*
             * مهم:
             * قبلاً اگر همه مودها حذف می‌شدند، چون count = 0 می‌شد،
             * دفعه بعد دوباره مودهای پیش‌فرض ساخته می‌شدند.
             * از این به بعد Seed فقط یک‌بار انجام می‌شود.
             */
            preferences.edit()
                    .putBoolean(
                            KEY_DEFAULT_MODES_SEEDED,
                            true
                    )
                    .apply();
        });
    }
}
