package com.bluepixel.mood.mode;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;

import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.database.entity.ModeHistoryEntity;
import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.model.ActivationSource;
import com.bluepixel.mood.model.ModeActions;
import com.bluepixel.mood.model.ModeEndReason;
import com.bluepixel.mood.notification.ModeNotificationManager;
import com.bluepixel.mood.widget.WidgetUpdater;

public class ModeEngine {

    public interface Callback {
        void onSuccess(ActiveMode activeMode);
        void onError(String message, Throwable throwable);
    }

    private static volatile ModeEngine instance;

    private final Context context;
    private final AudioManager audioManager;
    private final NotificationManager notificationManager;
    private final ActiveModeStore store;
    private final ModeScheduler scheduler;
    private final ModeNotificationManager notification;
    private final ModeRepository repository;

    private ModeEngine(Context context) {
        this.context = context.getApplicationContext();
        audioManager = (AudioManager)
                this.context.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager)
                this.context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );
        store = new ActiveModeStore(this.context);
        scheduler = new ModeScheduler(this.context);
        notification = new ModeNotificationManager(this.context);
        repository = ModeRepository.getInstance(this.context);
    }

    public static ModeEngine getInstance(Context context) {
        if (instance == null) {
            synchronized (ModeEngine.class) {
                if (instance == null) {
                    instance = new ModeEngine(
                            context.getApplicationContext()
                    );
                }
            }
        }
        return instance;
    }

    public synchronized void activate(
            ModeEntity mode,
            String source,
            Callback callback
    ) {
        DeviceStateSnapshot snapshot = null;
        AppliedActions actions = new AppliedActions();

        try {
            if (store.get() != null) {
                deactivateInternal(
                        ModeEndReason.NEW_MODE_STARTED
                );
            }

            snapshot = captureSnapshot();
            apply(mode, actions);

            ActiveMode activeMode = new ActiveMode();
            activeMode.modeId = mode.getId();
            activeMode.modeName = mode.getName();
            activeMode.visualType = mode.getVisualType();
            activeMode.activationSource =
                    source == null
                            ? ActivationSource.MANUAL
                            : source;
            activeMode.startedAt = System.currentTimeMillis();
            activeMode.expectedEndAt =
                    ModeEndTimeResolver.resolve(
                            mode,
                            activeMode.startedAt
                    );
            activeMode.snapshot = snapshot;
            activeMode.appliedActions = actions;

            store.save(activeMode);

            if (activeMode.hasAutomaticEnd()) {
                scheduler.schedule(activeMode.expectedEndAt);
            } else {
                scheduler.cancel();
            }

            notification.show(activeMode);
            WidgetUpdater.updateAll(context);
            callback.onSuccess(activeMode);
        } catch (Throwable throwable) {
            if (snapshot != null) {
                try {
                    restore(snapshot, actions);
                } catch (Throwable ignored) {
                }
            }

            scheduler.cancel();
            notification.cancel();
            store.clear();
            WidgetUpdater.updateAll(context);
            callback.onError(
                    "اجرای حالت با خطا روبه‌رو شد.",
                    throwable
            );
        }
    }

    public synchronized boolean deactivate(String reason) {
        try {
            return deactivateInternal(reason);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public synchronized boolean extend(int minutes) {
        ActiveMode activeMode = store.get();
        if (activeMode == null) return false;

        long base = activeMode.expectedEndAt > 0
                ? activeMode.expectedEndAt
                : System.currentTimeMillis();

        activeMode.expectedEndAt =
                base + Math.max(1, minutes) * 60_000L;

        store.save(activeMode);
        scheduler.schedule(activeMode.expectedEndAt);
        notification.show(activeMode);
        WidgetUpdater.updateAll(context);
        return true;
    }

    public ActiveMode getActiveMode() {
        return store.get();
    }

    public void refreshNotification() {
        ActiveMode activeMode = store.get();
        if (activeMode != null) {
            notification.show(activeMode);
        }
    }

    public void restoreAfterBoot() {
        ActiveMode activeMode = store.get();
        if (activeMode == null) {
            WidgetUpdater.updateAll(context);
            return;
        }

        if (activeMode.hasAutomaticEnd()) {
            if (activeMode.expectedEndAt
                    <= System.currentTimeMillis()) {
                deactivate(ModeEndReason.DEVICE_RESTARTED);
                return;
            }

            scheduler.schedule(activeMode.expectedEndAt);
        }

        notification.show(activeMode);
        WidgetUpdater.updateAll(context);
    }

    private boolean deactivateInternal(String reason) {
        ActiveMode activeMode = store.get();

        if (activeMode == null) {
            scheduler.cancel();
            notification.cancel();
            WidgetUpdater.updateAll(context);
            return false;
        }

        boolean successful = true;

        try {
            restore(
                    activeMode.snapshot,
                    activeMode.appliedActions
            );
        } catch (Throwable throwable) {
            successful = false;
        } finally {
            scheduler.cancel();
            notification.cancel();
            store.clear();
            WidgetUpdater.updateAll(context);
        }

        ModeHistoryEntity history = new ModeHistoryEntity();
        history.setModeId(activeMode.modeId);
        history.setModeName(activeMode.modeName);
        history.setVisualType(activeMode.visualType);
        history.setStartedAt(activeMode.startedAt);
        history.setEndedAt(System.currentTimeMillis());
        history.setEndReason(reason);
        history.setActivationSource(
                activeMode.activationSource
        );
        history.setSuccessful(successful);
        repository.insertHistory(history);

        return true;
    }

    private DeviceStateSnapshot captureSnapshot() {
        DeviceStateSnapshot snapshot =
                new DeviceStateSnapshot();

        snapshot.ringVolume = audioManager.getStreamVolume(
                AudioManager.STREAM_RING
        );
        snapshot.notificationVolume =
                audioManager.getStreamVolume(
                        AudioManager.STREAM_NOTIFICATION
                );
        snapshot.mediaVolume = audioManager.getStreamVolume(
                AudioManager.STREAM_MUSIC
        );
        snapshot.alarmVolume = audioManager.getStreamVolume(
                AudioManager.STREAM_ALARM
        );
        snapshot.ringerMode = audioManager.getRingerMode();
        snapshot.interruptionFilter =
                notificationManager
                        .getCurrentInterruptionFilter();
        snapshot.brightnessMode = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        );
        snapshot.brightnessValue = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                128
        );
        return snapshot;
    }

    private void apply(
            ModeEntity mode,
            AppliedActions actions
    ) {
        if (mode.isChangeRingVolume()) {
            setStreamPercent(
                    AudioManager.STREAM_RING,
                    mode.getRingVolumePercent()
            );
            actions.ringVolume = true;
        }

        if (mode.isChangeNotificationVolume()) {
            setStreamPercent(
                    AudioManager.STREAM_NOTIFICATION,
                    mode.getNotificationVolumePercent()
            );
            actions.notificationVolume = true;
        }

        if (mode.isChangeMediaVolume()) {
            setStreamPercent(
                    AudioManager.STREAM_MUSIC,
                    mode.getMediaVolumePercent()
            );
            actions.mediaVolume = true;
        }

        if (mode.isChangeAlarmVolume()) {
            setStreamPercent(
                    AudioManager.STREAM_ALARM,
                    mode.getAlarmVolumePercent()
            );
            actions.alarmVolume = true;
        }

        if (mode.getInterruptionFilter()
                != ModeActions.UNCHANGED
                && notificationManager
                .isNotificationPolicyAccessGranted()) {
            notificationManager.setInterruptionFilter(
                    mode.getInterruptionFilter()
            );
            actions.interruptionFilter = true;
        }

        if (mode.getRingerMode()
                != ModeActions.UNCHANGED) {
            audioManager.setRingerMode(mode.getRingerMode());
            actions.ringerMode = true;
        }

        if (mode.isChangeBrightness()
                && Settings.System.canWrite(context)) {
            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            );
            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    percentToBrightness(
                            mode.getBrightnessPercent()
                    )
            );
            actions.brightness = true;
        }
    }

    private void restore(
            DeviceStateSnapshot snapshot,
            AppliedActions actions
    ) {
        if (snapshot == null || actions == null) return;

        if (actions.interruptionFilter
                && notificationManager
                .isNotificationPolicyAccessGranted()) {
            notificationManager.setInterruptionFilter(
                    snapshot.interruptionFilter
            );
        }

        if (actions.ringerMode) {
            audioManager.setRingerMode(snapshot.ringerMode);
        }

        if (actions.ringVolume) {
            setStreamIndex(
                    AudioManager.STREAM_RING,
                    snapshot.ringVolume
            );
        }

        if (actions.notificationVolume) {
            setStreamIndex(
                    AudioManager.STREAM_NOTIFICATION,
                    snapshot.notificationVolume
            );
        }

        if (actions.mediaVolume) {
            setStreamIndex(
                    AudioManager.STREAM_MUSIC,
                    snapshot.mediaVolume
            );
        }

        if (actions.alarmVolume) {
            setStreamIndex(
                    AudioManager.STREAM_ALARM,
                    snapshot.alarmVolume
            );
        }

        if (actions.brightness
                && Settings.System.canWrite(context)) {
            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    snapshot.brightnessMode
            );
            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    snapshot.brightnessValue
            );
        }
    }

    private void setStreamPercent(int stream, int percent) {
        int max = audioManager.getStreamMaxVolume(stream);
        int value = Math.round(
                max * clamp(percent, 0, 100) / 100f
        );
        setStreamIndex(stream, value);
    }

    private void setStreamIndex(int stream, int value) {
        int max = audioManager.getStreamMaxVolume(stream);
        audioManager.setStreamVolume(
                stream,
                clamp(value, 0, max),
                0
        );
    }

    private int percentToBrightness(int percent) {
        return Math.max(
                1,
                Math.round(
                        255 * clamp(percent, 1, 100) / 100f
                )
        );
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
