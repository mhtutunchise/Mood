package com.bluepixel.mood.data.database;

import android.app.NotificationManager;
import android.media.AudioManager;

import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.model.ModeEndType;
import com.bluepixel.mood.model.ModeVisualType;

import java.util.ArrayList;
import java.util.List;

public final class DefaultModeSeeder {

    private DefaultModeSeeder() {
    }

    public static List<ModeEntity> createModes() {
        List<ModeEntity> result = new ArrayList<>();
        long now = System.currentTimeMillis();

        ModeEntity sleep = base(
                "خواب",
                "نور کم، اعلان آرام و آلارم بلند",
                ModeVisualType.SLEEP,
                0,
                now
        );
        sleep.setFavorite(true);
        sleep.setChangeRingVolume(true);
        sleep.setRingVolumePercent(20);
        sleep.setChangeNotificationVolume(true);
        sleep.setNotificationVolumePercent(0);
        sleep.setChangeMediaVolume(true);
        sleep.setMediaVolumePercent(10);
        sleep.setChangeAlarmVolume(true);
        sleep.setAlarmVolumePercent(100);
        sleep.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
        );
        sleep.setChangeBrightness(true);
        sleep.setBrightnessPercent(15);
        sleep.setEndType(ModeEndType.DURATION);
        sleep.setDurationMinutes(480);
        result.add(sleep);

        ModeEntity meeting = base(
                "جلسه",
                "ویبره و اعلان‌های بی‌صدا",
                ModeVisualType.MEETING,
                1,
                now
        );
        meeting.setFavorite(true);
        meeting.setChangeRingVolume(true);
        meeting.setRingVolumePercent(0);
        meeting.setChangeNotificationVolume(true);
        meeting.setNotificationVolumePercent(0);
        meeting.setChangeMediaVolume(true);
        meeting.setMediaVolumePercent(0);
        meeting.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        meeting.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
        );
        meeting.setEndType(ModeEndType.DURATION);
        meeting.setDurationMinutes(60);
        result.add(meeting);

        ModeEntity study = base(
                "مطالعه",
                "تمرکز بیشتر با اعلان آرام",
                ModeVisualType.STUDY,
                2,
                now
        );
        study.setFavorite(true);
        study.setChangeNotificationVolume(true);
        study.setNotificationVolumePercent(0);
        study.setChangeMediaVolume(true);
        study.setMediaVolumePercent(25);
        study.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
        );
        study.setChangeBrightness(true);
        study.setBrightnessPercent(40);
        study.setEndType(ModeEndType.DURATION);
        study.setDurationMinutes(60);
        result.add(study);

        ModeEntity outdoor = base(
                "بیرون",
                "زنگ بلند، اعلان واضح و نور بیشتر",
                ModeVisualType.OUTDOOR,
                3,
                now
        );
        outdoor.setFavorite(true);
        outdoor.setChangeRingVolume(true);
        outdoor.setRingVolumePercent(100);
        outdoor.setChangeNotificationVolume(true);
        outdoor.setNotificationVolumePercent(90);
        outdoor.setChangeMediaVolume(true);
        outdoor.setMediaVolumePercent(70);
        outdoor.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        outdoor.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_ALL
        );
        outdoor.setChangeBrightness(true);
        outdoor.setBrightnessPercent(90);
        result.add(outdoor);

        ModeEntity drive = base(
                "رانندگی",
                "صدای زنگ و رسانه مناسب مسیر",
                ModeVisualType.DRIVE,
                4,
                now
        );
        drive.setChangeRingVolume(true);
        drive.setRingVolumePercent(100);
        drive.setChangeNotificationVolume(true);
        drive.setNotificationVolumePercent(80);
        drive.setChangeMediaVolume(true);
        drive.setMediaVolumePercent(65);
        drive.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        result.add(drive);

        ModeEntity gym = base(
                "باشگاه",
                "رسانه بلند و اعلان متعادل",
                ModeVisualType.GYM,
                5,
                now
        );
        gym.setChangeRingVolume(true);
        gym.setRingVolumePercent(70);
        gym.setChangeNotificationVolume(true);
        gym.setNotificationVolumePercent(50);
        gym.setChangeMediaVolume(true);
        gym.setMediaVolumePercent(85);
        gym.setEndType(ModeEndType.DURATION);
        gym.setDurationMinutes(90);
        result.add(gym);

        return result;
    }

    private static ModeEntity base(
            String name,
            String description,
            String visualType,
            int sortOrder,
            long now
    ) {
        ModeEntity mode = new ModeEntity();
        mode.setName(name);
        mode.setDescription(description);
        mode.setVisualType(visualType);
        mode.setBuiltIn(true);
        mode.setEnabled(true);
        mode.setFavorite(false);
        mode.setSortOrder(sortOrder);
        mode.setCreatedAt(now);
        mode.setUpdatedAt(now);
        return mode;
    }
}
