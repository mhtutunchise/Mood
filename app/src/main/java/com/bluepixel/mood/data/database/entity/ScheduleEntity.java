package com.bluepixel.mood.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedules")
public class ScheduleEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long modeId;

    @NonNull
    private String title = "";

    private int hour;
    private int minute;
    private int daysMask = 127;
    private boolean enabled = true;
    private long createdAt;
    private long updatedAt;

    public ScheduleEntity() {
    }

    public long getId() { return id; }
    public void setId(long value) { id = value; }

    public long getModeId() { return modeId; }
    public void setModeId(long value) { modeId = value; }

    @NonNull public String getTitle() { return title; }
    public void setTitle(@NonNull String value) { title = value; }

    public int getHour() { return hour; }
    public void setHour(int value) { hour = value; }

    public int getMinute() { return minute; }
    public void setMinute(int value) { minute = value; }

    public int getDaysMask() { return daysMask; }
    public void setDaysMask(int value) { daysMask = value; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long value) { createdAt = value; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long value) { updatedAt = value; }
}
