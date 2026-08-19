package com.bluepixel.mood.ui.quickaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.mode.ModePermission;
import com.bluepixel.mood.mode.ModePermissionChecker;
import com.bluepixel.mood.model.ActivationSource;
import com.bluepixel.mood.model.ModeEndReason;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class QuickActionActivity extends AppCompatActivity {

    public static final String EXTRA_MODE_ID = "mode_id";
    public static final String EXTRA_STOP = "stop";
    public static final String EXTRA_SOURCE = "source";

    private long pendingModeId;
    private String pendingSource;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.getBooleanExtra(EXTRA_STOP, false)) {
            ModeEngine.getInstance(this).deactivate(
                    ModeEndReason.USER_STOPPED
            );
            finish();
            return;
        }

        pendingModeId = intent.getLongExtra(
                EXTRA_MODE_ID,
                0
        );
        pendingSource = intent.getStringExtra(
                EXTRA_SOURCE
        );

        if (pendingModeId == 0) {
            finish();
            return;
        }

        loadAndActivate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pendingModeId != 0 && !isFinishing()) {
            loadAndActivate();
        }
    }

    private void loadAndActivate() {
        long id = pendingModeId;
        pendingModeId = 0;

        ModeRepository.getInstance(this).getMode(
                id,
                mode -> {
                    if (mode == null) {
                        finish();
                        return;
                    }

                    List<ModePermission> missing =
                            new ModePermissionChecker(this)
                                    .getMissingPermissions(mode);

                    if (!missing.isEmpty()) {
                        pendingModeId = id;
                        showPermissionDialog(missing.get(0));
                        return;
                    }

                    ModeEngine.getInstance(this).activate(
                            mode,
                            pendingSource,
                            new ModeEngine.Callback() {
                                @Override
                                public void onSuccess(
                                        com.bluepixel.mood.mode.ActiveMode
                                                activeMode
                                ) {
                                    Toast.makeText(
                                            QuickActionActivity.this,
                                            "حالت «"
                                                    + activeMode.modeName
                                                    + "» فعال شد.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    finish();
                                }

                                @Override
                                public void onError(
                                        String message,
                                        Throwable throwable
                                ) {
                                    Toast.makeText(
                                            QuickActionActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();
                                    finish();
                                }
                            }
                    );
                }
        );
    }

    private void showPermissionDialog(
            ModePermission permission
    ) {
        if (permission == ModePermission.NOTIFICATION_POLICY) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("دسترسی مزاحم نشوید")
                    .setMessage(
                            "برای اجرای کامل این حالت، اجازه "
                                    + "مدیریت مزاحم نشوید را فعال کن."
                    )
                    .setNegativeButton(
                            "انصراف",
                            (dialog, which) -> finish()
                    )
                    .setPositiveButton(
                            "تنظیمات",
                            (dialog, which) ->
                                    startActivity(new Intent(
                                            Settings
                                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                                    ))
                    )
                    .setOnCancelListener(dialog -> finish())
                    .show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("دسترسی تنظیمات سیستم")
                .setMessage(
                        "برای تغییر روشنایی و تنظیمات نمایشگر، "
                                + "این دسترسی را فعال کن."
                )
                .setNegativeButton(
                        "انصراف",
                        (dialog, which) -> finish()
                )
                .setPositiveButton(
                        "تنظیمات",
                        (dialog, which) ->
                                startActivity(new Intent(
                                        Settings
                                                .ACTION_MANAGE_WRITE_SETTINGS,
                                        Uri.parse(
                                                "package:"
                                                        + getPackageName()
                                        )
                                ))
                )
                .setOnCancelListener(dialog -> finish())
                .show();
    }
}
