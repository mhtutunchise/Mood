package com.bluepixel.mood.mode;

public class ActiveMode {

    public long modeId;
    public String modeName;
    public String visualType;
    public String activationSource;
    public long startedAt;
    public long expectedEndAt;
    public DeviceStateSnapshot snapshot;
    public AppliedActions appliedActions;

    public boolean hasAutomaticEnd() {
        return expectedEndAt > 0;
    }
}
