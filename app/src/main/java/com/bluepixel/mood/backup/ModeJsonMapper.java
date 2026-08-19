package com.bluepixel.mood.backup;

import com.bluepixel.mood.data.database.entity.ModeEntity;

import org.json.JSONException;
import org.json.JSONObject;

public final class ModeJsonMapper {

    private ModeJsonMapper() {
    }

    public static JSONObject toJson(ModeEntity mode)
            throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", mode.getName());
        json.put("description", mode.getDescription());
        json.put("visualType", mode.getVisualType());
        json.put("enabled", mode.isEnabled());
        json.put("favorite", mode.isFavorite());
        json.put("sortOrder", mode.getSortOrder());
        json.put("changeRingVolume", mode.isChangeRingVolume());
        json.put("ringVolumePercent", mode.getRingVolumePercent());
        json.put(
                "changeNotificationVolume",
                mode.isChangeNotificationVolume()
        );
        json.put(
                "notificationVolumePercent",
                mode.getNotificationVolumePercent()
        );
        json.put("changeMediaVolume", mode.isChangeMediaVolume());
        json.put("mediaVolumePercent", mode.getMediaVolumePercent());
        json.put("changeAlarmVolume", mode.isChangeAlarmVolume());
        json.put("alarmVolumePercent", mode.getAlarmVolumePercent());
        json.put("ringerMode", mode.getRingerMode());
        json.put(
                "interruptionFilter",
                mode.getInterruptionFilter()
        );
        json.put("changeBrightness", mode.isChangeBrightness());
        json.put("brightnessPercent", mode.getBrightnessPercent());
        json.put("endType", mode.getEndType());
        json.put("durationMinutes", mode.getDurationMinutes());
        json.put("endHour", mode.getEndHour());
        json.put("endMinute", mode.getEndMinute());
        return json;
    }

    public static ModeEntity fromJson(JSONObject json) {
        ModeEntity mode = new ModeEntity();
        mode.setName(json.optString("name", "حالت بازیابی‌شده"));
        mode.setDescription(json.optString("description", ""));
        mode.setVisualType(
                json.optString("visualType", "CUSTOM")
        );
        mode.setBuiltIn(false);
        mode.setEnabled(json.optBoolean("enabled", true));
        mode.setFavorite(json.optBoolean("favorite", false));
        mode.setSortOrder(json.optInt("sortOrder", 100));
        mode.setChangeRingVolume(
                json.optBoolean("changeRingVolume")
        );
        mode.setRingVolumePercent(
                json.optInt("ringVolumePercent", 50)
        );
        mode.setChangeNotificationVolume(
                json.optBoolean("changeNotificationVolume")
        );
        mode.setNotificationVolumePercent(
                json.optInt("notificationVolumePercent", 50)
        );
        mode.setChangeMediaVolume(
                json.optBoolean("changeMediaVolume")
        );
        mode.setMediaVolumePercent(
                json.optInt("mediaVolumePercent", 50)
        );
        mode.setChangeAlarmVolume(
                json.optBoolean("changeAlarmVolume")
        );
        mode.setAlarmVolumePercent(
                json.optInt("alarmVolumePercent", 100)
        );
        mode.setRingerMode(json.optInt("ringerMode", -1));
        mode.setInterruptionFilter(
                json.optInt("interruptionFilter", -1)
        );
        mode.setChangeBrightness(
                json.optBoolean("changeBrightness")
        );
        mode.setBrightnessPercent(
                json.optInt("brightnessPercent", 50)
        );
        mode.setEndType(json.optString("endType", "MANUAL"));
        mode.setDurationMinutes(
                json.optInt("durationMinutes", 60)
        );
        mode.setEndHour(json.optInt("endHour", 0));
        mode.setEndMinute(json.optInt("endMinute", 0));
        long now = System.currentTimeMillis();
        mode.setCreatedAt(now);
        mode.setUpdatedAt(now);
        return mode;
    }
}
