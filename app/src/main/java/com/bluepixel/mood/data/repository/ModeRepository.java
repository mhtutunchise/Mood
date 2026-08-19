package com.bluepixel.mood.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.bluepixel.mood.core.AppExecutors;
import com.bluepixel.mood.data.database.AppDatabase;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.database.entity.ModeHistoryEntity;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;

import java.util.List;

public class ModeRepository {

    public interface ResultCallback<T> {
        void onResult(T result);
    }

    private static volatile ModeRepository instance;
    private final AppDatabase database;

    private ModeRepository(Context context) {
        database = AppDatabase.getInstance(context);
    }

    public static ModeRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (ModeRepository.class) {
                if (instance == null) {
                    instance = new ModeRepository(
                            context.getApplicationContext()
                    );
                }
            }
        }
        return instance;
    }

    public LiveData<List<ModeEntity>> observeModes() {
        return database.modeDao().observeAll();
    }

    public LiveData<List<ModeEntity>> observeFavorites() {
        return database.modeDao().observeFavorites();
    }

    public LiveData<List<ModeHistoryEntity>> observeHistory() {
        return database.historyDao().observeAll();
    }

    public LiveData<List<ScheduleEntity>> observeSchedules() {
        return database.scheduleDao().observeAll();
    }

    public void getMode(
            long id,
            ResultCallback<ModeEntity> callback
    ) {
        AppExecutors.io().execute(() -> {
            ModeEntity result =
                    database.modeDao()
                            .getByIdBlocking(id);

            AppExecutors.main().post(
                    () -> callback.onResult(result)
            );
        });
    }

    public void getSchedule(
            long id,
            ResultCallback<ScheduleEntity> callback
    ) {
        AppExecutors.io().execute(() -> {
            ScheduleEntity result =
                    database.scheduleDao()
                            .getByIdBlocking(id);

            AppExecutors.main().post(
                    () -> callback.onResult(result)
            );
        });
    }

    public void getAllModes(
            ResultCallback<List<ModeEntity>> callback
    ) {
        AppExecutors.io().execute(() -> {
            List<ModeEntity> result =
                    database.modeDao()
                            .getAllBlocking();

            AppExecutors.main().post(
                    () -> callback.onResult(result)
            );
        });
    }

    public void saveMode(
            ModeEntity entity,
            ResultCallback<Long> callback
    ) {
        AppExecutors.io().execute(() -> {
            entity.setUpdatedAt(
                    System.currentTimeMillis()
            );

            long id;

            if (entity.getId() == 0) {
                entity.setCreatedAt(
                        System.currentTimeMillis()
                );

                id =
                        database.modeDao()
                                .insert(entity);
            } else {
                database.modeDao()
                        .update(entity);

                id =
                        entity.getId();
            }

            if (callback != null) {
                long finalId = id;

                AppExecutors.main().post(
                        () -> callback.onResult(finalId)
                );
            }
        });
    }

    public void copyMode(
            ModeEntity entity
    ) {
        saveMode(
                entity.copyForInsert(),
                null
        );
    }

    public void deleteMode(
            long id
    ) {
        deleteMode(
                id,
                null
        );
    }

    public void deleteMode(
            long id,
            ResultCallback<Boolean> callback
    ) {
        AppExecutors.io().execute(() -> {
            final int[] deletedCount = {0};

            /*
             * اول زمان‌بندی‌های وابسته به این مود حذف می‌شوند،
             * بعد خود مود حذف می‌شود.
             */
            database.runInTransaction(() -> {
                database.scheduleDao()
                        .deleteByModeId(id);

                deletedCount[0] =
                        database.modeDao()
                                .deleteById(id);
            });

            if (callback != null) {
                boolean deleted =
                        deletedCount[0] > 0;

                AppExecutors.main().post(
                        () -> callback.onResult(deleted)
                );
            }
        });
    }

    public void insertHistory(
            ModeHistoryEntity entity
    ) {
        AppExecutors.io().execute(
                () -> database.historyDao()
                        .insert(entity)
        );
    }

    public void clearHistory() {
        AppExecutors.io().execute(
                () -> database.historyDao()
                        .clearAll()
        );
    }

    public void saveSchedule(
            ScheduleEntity entity,
            ResultCallback<Long> callback
    ) {
        AppExecutors.io().execute(() -> {
            entity.setUpdatedAt(
                    System.currentTimeMillis()
            );

            long id;

            if (entity.getId() == 0) {
                entity.setCreatedAt(
                        System.currentTimeMillis()
                );

                id =
                        database.scheduleDao()
                                .insert(entity);
            } else {
                database.scheduleDao()
                        .update(entity);

                id =
                        entity.getId();
            }

            if (callback != null) {
                long finalId = id;

                AppExecutors.main().post(
                        () -> callback.onResult(finalId)
                );
            }
        });
    }

    public void deleteSchedule(
            long id
    ) {
        AppExecutors.io().execute(
                () -> database.scheduleDao()
                        .delete(id)
        );
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
