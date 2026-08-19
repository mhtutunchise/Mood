package com.bluepixel.mood.ui.common;

import com.bluepixel.mood.R;
import com.bluepixel.mood.model.ModeVisualType;

public final class ModeVisuals {

    private ModeVisuals() {
    }

    public static int icon(String type) {
        if (ModeVisualType.SLEEP.equals(type)) {
            return R.drawable.ic_mode_sleep;
        }
        if (ModeVisualType.MEETING.equals(type)) {
            return R.drawable.ic_mode_meeting;
        }
        if (ModeVisualType.STUDY.equals(type)) {
            return R.drawable.ic_mode_study;
        }
        if (ModeVisualType.OUTDOOR.equals(type)) {
            return R.drawable.ic_mode_outdoor;
        }
        if (ModeVisualType.DRIVE.equals(type)) {
            return R.drawable.ic_mode_drive;
        }
        if (ModeVisualType.GYM.equals(type)) {
            return R.drawable.ic_mode_gym;
        }
        return R.drawable.ic_mode_custom;
    }

    public static int container(String type) {
        if (ModeVisualType.SLEEP.equals(type)) {
            return R.drawable.bg_sleep_icon;
        }
        if (ModeVisualType.MEETING.equals(type)) {
            return R.drawable.bg_meeting_icon;
        }
        if (ModeVisualType.STUDY.equals(type)) {
            return R.drawable.bg_study_icon;
        }
        if (ModeVisualType.OUTDOOR.equals(type)) {
            return R.drawable.bg_outdoor_icon;
        }
        if (ModeVisualType.DRIVE.equals(type)) {
            return R.drawable.bg_drive_icon;
        }
        if (ModeVisualType.GYM.equals(type)) {
            return R.drawable.bg_gym_icon;
        }
        return R.drawable.bg_custom_icon;
    }

    public static int tint(String type) {
        if (ModeVisualType.SLEEP.equals(type)) {
            return R.color.mode_sleep;
        }
        if (ModeVisualType.MEETING.equals(type)) {
            return R.color.mode_meeting;
        }
        if (ModeVisualType.STUDY.equals(type)) {
            return R.color.mode_study;
        }
        if (ModeVisualType.OUTDOOR.equals(type)) {
            return R.color.mode_outdoor;
        }
        if (ModeVisualType.DRIVE.equals(type)) {
            return R.color.mode_drive;
        }
        if (ModeVisualType.GYM.equals(type)) {
            return R.color.mode_gym;
        }
        return R.color.mode_custom;
    }
}
