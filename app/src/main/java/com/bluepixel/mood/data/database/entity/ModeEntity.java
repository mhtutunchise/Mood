package com.bluepixel.mood.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "modes")
public class ModeEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name = "";

    @NonNull
    private String description = "";

    @NonNull
    private String visualType = "CUSTOM";

    private boolean builtIn;
    private boolean enabled = true;
    private boolean favorite;
    private int sortOrder;

    private boolean changeRingVolume;
    private int ringVolumePercent = 50;

    private boolean changeNotificationVolume;
    private int notificationVolumePercent = 50;

    private boolean changeMediaVolume;
    private int mediaVolumePercent = 50;

    private boolean changeAlarmVolume;
    private int alarmVolumePercent = 100;

    private int ringerMode = -1;
    private int interruptionFilter = -1;

    private boolean changeBrightness;
    private int brightnessPercent = 50;

    @NonNull
    private String endType = "MANUAL";

    private int durationMinutes = 60;
    private int endHour;
    private int endMinute;

    private long createdAt;
    private long updatedAt;

    public ModeEntity() {
    }

    public ModeEntity copyForInsert() {
        ModeEntity copy = new ModeEntity();
        copy.setName(name + " - کپی");
        copy.setDescription(description);
        copy.setVisualType(visualType);
        copy.setBuiltIn(false);
        copy.setEnabled(enabled);
        copy.setFavorite(false);
        copy.setSortOrder(sortOrder + 1);
        copy.setChangeRingVolume(changeRingVolume);
        copy.setRingVolumePercent(ringVolumePercent);
        copy.setChangeNotificationVolume(changeNotificationVolume);
        copy.setNotificationVolumePercent(notificationVolumePercent);
        copy.setChangeMediaVolume(changeMediaVolume);
        copy.setMediaVolumePercent(mediaVolumePercent);
        copy.setChangeAlarmVolume(changeAlarmVolume);
        copy.setAlarmVolumePercent(alarmVolumePercent);
        copy.setRingerMode(ringerMode);
        copy.setInterruptionFilter(interruptionFilter);
        copy.setChangeBrightness(changeBrightness);
        copy.setBrightnessPercent(brightnessPercent);
        copy.setEndType(endType);
        copy.setDurationMinutes(durationMinutes);
        copy.setEndHour(endHour);
        copy.setEndMinute(endMinute);
        long now = System.currentTimeMillis();
        copy.setCreatedAt(now);
        copy.setUpdatedAt(now);
        return copy;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull public String getDescription() { return description; }
    public void setDescription(@NonNull String description) { this.description = description; }

    @NonNull public String getVisualType() { return visualType; }
    public void setVisualType(@NonNull String visualType) { this.visualType = visualType; }

    public boolean isBuiltIn() { return builtIn; }
    public void setBuiltIn(boolean builtIn) { this.builtIn = builtIn; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public boolean isChangeRingVolume() { return changeRingVolume; }
    public void setChangeRingVolume(boolean value) { changeRingVolume = value; }

    public int getRingVolumePercent() { return ringVolumePercent; }
    public void setRingVolumePercent(int value) { ringVolumePercent = value; }

    public boolean isChangeNotificationVolume() { return changeNotificationVolume; }
    public void setChangeNotificationVolume(boolean value) { changeNotificationVolume = value; }

    public int getNotificationVolumePercent() { return notificationVolumePercent; }
    public void setNotificationVolumePercent(int value) { notificationVolumePercent = value; }

    public boolean isChangeMediaVolume() { return changeMediaVolume; }
    public void setChangeMediaVolume(boolean value) { changeMediaVolume = value; }

    public int getMediaVolumePercent() { return mediaVolumePercent; }
    public void setMediaVolumePercent(int value) { mediaVolumePercent = value; }

    public boolean isChangeAlarmVolume() { return changeAlarmVolume; }
    public void setChangeAlarmVolume(boolean value) { changeAlarmVolume = value; }

    public int getAlarmVolumePercent() { return alarmVolumePercent; }
    public void setAlarmVolumePercent(int value) { alarmVolumePercent = value; }

    public int getRingerMode() { return ringerMode; }
    public void setRingerMode(int value) { ringerMode = value; }

    public int getInterruptionFilter() { return interruptionFilter; }
    public void setInterruptionFilter(int value) { interruptionFilter = value; }

    public boolean isChangeBrightness() { return changeBrightness; }
    public void setChangeBrightness(boolean value) { changeBrightness = value; }

    public int getBrightnessPercent() { return brightnessPercent; }
    public void setBrightnessPercent(int value) { brightnessPercent = value; }

    @NonNull public String getEndType() { return endType; }
    public void setEndType(@NonNull String value) { endType = value; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int value) { durationMinutes = value; }

    public int getEndHour() { return endHour; }
    public void setEndHour(int value) { endHour = value; }

    public int getEndMinute() { return endMinute; }
    public void setEndMinute(int value) { endMinute = value; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long value) { createdAt = value; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long value) { updatedAt = value; }
}
