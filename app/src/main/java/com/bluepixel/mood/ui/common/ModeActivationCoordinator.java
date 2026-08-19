package com.bluepixel.mood.ui.common;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.mode.ActiveMode;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.mode.ModePermission;
import com.bluepixel.mood.mode.ModePermissionChecker;
import com.bluepixel.mood.model.ActivationSource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ModeActivationCoordinator {

    public interface Listener {
        void onActivated(ActiveMode activeMode);
    }

    private final Fragment fragment;
    private final ActivityResultLauncher<String>
            notificationPermissionLauncher;
    private final ModePermissionChecker permissionChecker;
    private final ModeEngine engine;
    private final Listener listener;

    private ModeEntity pendingMode;

    public ModeActivationCoordinator(
            Fragment fragment,
            ActivityResultLauncher<String>
                    notificationPermissionLauncher,
            Listener listener
    ) {
        this.fragment = fragment;
        this.notificationPermissionLauncher =
                notificationPermissionLauncher;
        this.listener = listener;
        permissionChecker = new ModePermissionChecker(
                fragment.requireContext()
        );
        engine = ModeEngine.getInstance(
                fragment.requireContext()
        );
    }

    public void activate(ModeEntity mode) {
        pendingMode = mode;
        continueActivation();
    }

    public void onResume() {
        if (pendingMode != null) {
            continueActivation();
        }
    }

    private void continueActivation() {
        if (pendingMode == null || fragment.getView() == null) {
            return;
        }

        List<ModePermission> missing =
                permissionChecker
                        .getMissingPermissions(pendingMode);

        if (missing.contains(
                ModePermission.NOTIFICATION_POLICY
        )) {
            showDndDialog();
            return;
        }

        if (missing.contains(ModePermission.WRITE_SETTINGS)) {
            showWriteSettingsDialog();
            return;
        }

        ModeEntity mode = pendingMode;
        pendingMode = null;

        engine.activate(
                mode,
                ActivationSource.MANUAL,
                new ModeEngine.Callback() {
                    @Override
                    public void onSuccess(ActiveMode activeMode) {
                        requestNotificationPermissionIfNeeded();

                        if (listener != null) {
                            listener.onActivated(activeMode);
                        }

                        if (fragment.getView() != null) {
                            Snackbar.make(
                                    fragment.requireView(),
                                    "حالت «"
                                            + activeMode.modeName
                                            + "» فعال شد.",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onError(
                            String message,
                            Throwable throwable
                    ) {
                        if (fragment.getView() != null) {
                            Snackbar.make(
                                    fragment.requireView(),
                                    message,
                                    Snackbar.LENGTH_LONG
                            ).show();
                        }
                    }
                }
        );
    }

    private void showDndDialog() {
        new MaterialAlertDialogBuilder(
                fragment.requireContext()
        )
                .setTitle("دسترسی مزاحم نشوید")
                .setMessage(
                        "برای اجرای کامل این حالت، اجازه مدیریت "
                                + "مزاحم‌نشوید را برای Mood فعال کن."
                )
                .setNegativeButton(
                        "انصراف",
                        (dialog, which) -> pendingMode = null
                )
                .setPositiveButton(
                        "رفتن به تنظیمات",
                        (dialog, which) -> fragment.startActivity(
                                new Intent(
                                        Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                                )
                        )
                )
                .show();
    }

    private void showWriteSettingsDialog() {
        new MaterialAlertDialogBuilder(
                fragment.requireContext()
        )
                .setTitle("دسترسی تغییر روشنایی")
                .setMessage(
                        "برای تنظیم روشنایی، اجازه تغییر تنظیمات "
                                + "سیستم را برای Mood فعال کن."
                )
                .setNegativeButton(
                        "انصراف",
                        (dialog, which) -> pendingMode = null
                )
                .setPositiveButton(
                        "دادن دسترسی",
                        (dialog, which) -> {
                            Intent intent = new Intent(
                                    Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                    Uri.parse(
                                            "package:"
                                                    + fragment
                                                    .requireContext()
                                                    .getPackageName()
                                    )
                            );
                            fragment.startActivity(intent);
                        }
                )
                .show();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.TIRAMISU
                && fragment.requireContext()
                .checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                )
                != android.content.pm.PackageManager
                .PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
            );
        }
    }
}
