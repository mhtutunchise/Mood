package com.bluepixel.mood.mode;

import org.json.JSONException;
import org.json.JSONObject;

public class AppliedActions {

    public boolean ringVolume;
    public boolean notificationVolume;
    public boolean mediaVolume;
    public boolean alarmVolume;
    public boolean ringerMode;
    public boolean interruptionFilter;
    public boolean brightness;

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("ringVolume", ringVolume);
        json.put("notificationVolume", notificationVolume);
        json.put("mediaVolume", mediaVolume);
        json.put("alarmVolume", alarmVolume);
        json.put("ringerMode", ringerMode);
        json.put("interruptionFilter", interruptionFilter);
        json.put("brightness", brightness);
        return json;
    }

    public static AppliedActions fromJson(JSONObject json) {
        AppliedActions value = new AppliedActions();
        value.ringVolume = json.optBoolean("ringVolume");
        value.notificationVolume =
                json.optBoolean("notificationVolume");
        value.mediaVolume = json.optBoolean("mediaVolume");
        value.alarmVolume = json.optBoolean("alarmVolume");
        value.ringerMode = json.optBoolean("ringerMode");
        value.interruptionFilter =
                json.optBoolean("interruptionFilter");
        value.brightness = json.optBoolean("brightness");
        return value;
    }
}
