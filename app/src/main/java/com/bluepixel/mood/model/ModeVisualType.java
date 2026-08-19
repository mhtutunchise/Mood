package com.bluepixel.mood.model;

public final class ModeVisualType {

    public static final String SLEEP = "SLEEP";
    public static final String MEETING = "MEETING";
    public static final String STUDY = "STUDY";
    public static final String OUTDOOR = "OUTDOOR";
    public static final String DRIVE = "DRIVE";
    public static final String GYM = "GYM";
    public static final String CUSTOM = "CUSTOM";

    private ModeVisualType() {
    }

    public static String fromPosition(int position) {
        switch (position) {
            case 0:
                return SLEEP;
            case 1:
                return MEETING;
            case 2:
                return STUDY;
            case 3:
                return OUTDOOR;
            case 4:
                return DRIVE;
            case 5:
                return GYM;
            default:
                return CUSTOM;
        }
    }

    public static int toPosition(String type) {
        if (SLEEP.equals(type)) return 0;
        if (MEETING.equals(type)) return 1;
        if (STUDY.equals(type)) return 2;
        if (OUTDOOR.equals(type)) return 3;
        if (DRIVE.equals(type)) return 4;
        if (GYM.equals(type)) return 5;
        return 6;
    }
}
