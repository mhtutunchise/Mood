package com.bluepixel.mood.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mode_history")
public class ModeHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long modeId;

    @NonNull
    private String modeName = "";

    @NonNull
    private String visualType = "CUSTOM";

    private long startedAt;
    private long endedAt;

    @NonNull
    private String endReason = "USER_STOPPED";

    @NonNull
    private String activationSource = "MANUAL";

    private boolean successful = true;

    public ModeHistoryEntity() {
    }

    public long getId() { return id; }
    public void setId(long value) { id = value; }

    public long getModeId() { return modeId; }
    public void setModeId(long value) { modeId = value; }

    @NonNull public String getModeName() { return modeName; }
    public void setModeName(@NonNull String value) { modeName = value; }

    @NonNull public String getVisualType() { return visualType; }
    public void setVisualType(@NonNull String value) { visualType = value; }

    public long getStartedAt() { return startedAt; }
    public void setStartedAt(long value) { startedAt = value; }

    public long getEndedAt() { return endedAt; }
    public void setEndedAt(long value) { endedAt = value; }

    @NonNull public String getEndReason() { return endReason; }
    public void setEndReason(@NonNull String value) { endReason = value; }

    @NonNull public String getActivationSource() { return activationSource; }
    public void setActivationSource(@NonNull String value) { activationSource = value; }

    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean value) { successful = value; }
}
