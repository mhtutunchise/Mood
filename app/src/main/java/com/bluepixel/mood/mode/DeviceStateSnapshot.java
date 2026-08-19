package com.bluepixel.mood.mode;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceStateSnapshot {

    public int ringVolume;
    public int notificationVolume;
    public int mediaVolume;
    public int alarmVolume;
    public int ringerMode;
    public int interruptionFilter;
    public int brightnessMode;
    public int brightnessValue;

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("ringVolume", ringVolume);
        json.put("notificationVolume", notificationVolume);
        json.put("mediaVolume", mediaVolume);
        json.put("alarmVolume", alarmVolume);
        json.put("ringerMode", ringerMode);
        json.put("interruptionFilter", interruptionFilter);
        json.put("brightnessMode", brightnessMode);
        json.put("brightnessValue", brightnessValue);
        return json;
    }

    public static DeviceStateSnapshot fromJson(JSONObject json) {
        DeviceStateSnapshot value = new DeviceStateSnapshot();
        value.ringVolume = json.optInt("ringVolume", 0);
        value.notificationVolume = json.optInt("notificationVolume", 0);
        value.mediaVolume = json.optInt("mediaVolume", 0);
        value.alarmVolume = json.optInt("alarmVolume", 0);
        value.ringerMode = json.optInt("ringerMode", 2);
        value.interruptionFilter = json.optInt("interruptionFilter", 1);
        value.brightnessMode = json.optInt("brightnessMode", 1);
        value.brightnessValue = json.optInt("brightnessValue", 128);
        return value;
    }
}
