package com.bluepixel.mood.mode;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class ActiveModeStore {

    private static final String PREFS = "active_mode_store";
    private static final String KEY_ACTIVE_MODE = "active_mode";

    private final SharedPreferences preferences;

    public ActiveModeStore(Context context) {
        preferences = context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    public void save(ActiveMode activeMode) {
        try {
            JSONObject json = new JSONObject();
            json.put("modeId", activeMode.modeId);
            json.put("modeName", activeMode.modeName);
            json.put("visualType", activeMode.visualType);
            json.put("activationSource", activeMode.activationSource);
            json.put("startedAt", activeMode.startedAt);
            json.put("expectedEndAt", activeMode.expectedEndAt);
            json.put("snapshot", activeMode.snapshot.toJson());
            json.put("appliedActions", activeMode.appliedActions.toJson());

            preferences.edit()
                    .putString(KEY_ACTIVE_MODE, json.toString())
                    .apply();
        } catch (JSONException exception) {
            throw new IllegalStateException(
                    "Unable to persist active mode.",
                    exception
            );
        }
    }

    public ActiveMode get() {
        String raw = preferences.getString(KEY_ACTIVE_MODE, null);
        if (raw == null || raw.trim().isEmpty()) return null;

        try {
            JSONObject json = new JSONObject(raw);
            ActiveMode value = new ActiveMode();
            value.modeId = json.optLong("modeId");
            value.modeName = json.optString("modeName");
            value.visualType = json.optString("visualType");
            value.activationSource =
                    json.optString("activationSource", "MANUAL");
            value.startedAt = json.optLong("startedAt");
            value.expectedEndAt = json.optLong("expectedEndAt");
            value.snapshot = DeviceStateSnapshot.fromJson(
                    json.getJSONObject("snapshot")
            );
            value.appliedActions = AppliedActions.fromJson(
                    json.getJSONObject("appliedActions")
            );
            return value;
        } catch (JSONException exception) {
            clear();
            return null;
        }
    }

    public void clear() {
        preferences.edit().remove(KEY_ACTIVE_MODE).apply();
    }
}
