package com.bluepixel.mood.backup;

import android.content.Context;
import android.net.Uri;

import com.bluepixel.mood.core.AppExecutors;
import com.bluepixel.mood.data.database.AppDatabase;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BackupManager {

    public interface Callback {
        void onSuccess(String message);
        void onError(String message, Throwable throwable);
    }

    private final Context context;
    private final AppDatabase database;

    public BackupManager(Context context) {
        this.context = context.getApplicationContext();
        database = AppDatabase.getInstance(this.context);
    }

    public void exportTo(Uri uri, Callback callback) {
        AppExecutors.io().execute(() -> {
            try {
                JSONObject root = new JSONObject();
                root.put("formatVersion", 1);
                root.put("package", context.getPackageName());
                root.put("createdAt", System.currentTimeMillis());

                JSONArray modesJson = new JSONArray();
                List<ModeEntity> modes =
                        database.modeDao().getAllBlocking();
                for (ModeEntity mode : modes) {
                    if (!mode.isBuiltIn()) {
                        modesJson.put(ModeJsonMapper.toJson(mode));
                    }
                }
                root.put("customModes", modesJson);

                JSONArray schedulesJson = new JSONArray();
                List<ScheduleEntity> schedules =
                        database.scheduleDao().getAllBlocking();
                for (ScheduleEntity schedule : schedules) {
                    JSONObject item = new JSONObject();
                    item.put("modeId", schedule.getModeId());
                    item.put("title", schedule.getTitle());
                    item.put("hour", schedule.getHour());
                    item.put("minute", schedule.getMinute());
                    item.put("daysMask", schedule.getDaysMask());
                    item.put("enabled", schedule.isEnabled());
                    schedulesJson.put(item);
                }
                root.put("schedules", schedulesJson);

                try (OutputStream output =
                             context.getContentResolver()
                                     .openOutputStream(uri)) {
                    if (output == null) {
                        throw new IllegalStateException(
                                "Output stream is unavailable."
                        );
                    }
                    output.write(
                            root.toString(2).getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );
                }

                AppExecutors.main().post(
                        () -> callback.onSuccess(
                                "فایل پشتیبان ذخیره شد."
                        )
                );
            } catch (Throwable throwable) {
                AppExecutors.main().post(
                        () -> callback.onError(
                                "ساخت فایل پشتیبان ناموفق بود.",
                                throwable
                        )
                );
            }
        });
    }

    public void importFrom(Uri uri, Callback callback) {
        AppExecutors.io().execute(() -> {
            try {
                StringBuilder text = new StringBuilder();

                try (InputStream input =
                             context.getContentResolver()
                                     .openInputStream(uri)) {
                    if (input == null) {
                        throw new IllegalStateException(
                                "Input stream is unavailable."
                        );
                    }

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    input,
                                    StandardCharsets.UTF_8
                            )
                    );

                    String line;
                    while ((line = reader.readLine()) != null) {
                        text.append(line);
                    }
                }

                JSONObject root = new JSONObject(text.toString());
                if (root.optInt("formatVersion", 0) != 1) {
                    throw new IllegalArgumentException(
                            "Unsupported backup version."
                    );
                }

                JSONArray modesJson =
                        root.optJSONArray("customModes");

                database.modeDao().deleteAllCustom();

                if (modesJson != null) {
                    for (int i = 0; i < modesJson.length(); i++) {
                        database.modeDao().insert(
                                ModeJsonMapper.fromJson(
                                        modesJson.getJSONObject(i)
                                )
                        );
                    }
                }

                // Schedules are kept unless a future backup format
                // contains stable mode UUIDs. This avoids linking a
                // schedule to a wrong auto-generated database ID.

                AppExecutors.main().post(
                        () -> callback.onSuccess(
                                "حالت‌های شخصی بازیابی شدند."
                        )
                );
            } catch (Throwable throwable) {
                AppExecutors.main().post(
                        () -> callback.onError(
                                "بازیابی فایل پشتیبان ناموفق بود.",
                                throwable
                        )
                );
            }
        });
    }
}
