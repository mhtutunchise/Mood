package com.bluepixel.mood.ui.settings;

import android.Manifest;
import android.app.StatusBarManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bluepixel.mood.R;
import com.bluepixel.mood.backup.BackupManager;
import com.bluepixel.mood.data.preferences.AppPreferences;
import com.bluepixel.mood.databinding.FragmentSettingsBinding;
import com.bluepixel.mood.mode.ModePermissionChecker;
import com.bluepixel.mood.service.MoodTileService;
import com.bluepixel.mood.widget.MoodWidgetProvider;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private ModePermissionChecker permissionChecker;
    private BackupManager backupManager;

    /*
     * این دو فلگ جلوی اجرای ناخواسته Listener هنگام مقداردهی اولیه
     * Spinnerها و هنگام بازسازی Activity را می‌گیرند.
     */
    private boolean themeSpinnerReady = false;
    private boolean languageSpinnerReady = false;

    private final ActivityResultLauncher<String>
            notificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (binding != null) {
                            renderPermissions();
                        }
                    }
            );

    private final ActivityResultLauncher<String>
            createBackupLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.CreateDocument(
                            "application/json"
                    ),
                    uri -> {
                        if (uri != null) {
                            exportBackup(uri);
                        }
                    }
            );

    private final ActivityResultLauncher<String[]>
            openBackupLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri != null) {
                            importBackup(uri);
                        }
                    }
            );

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSettingsBinding.inflate(
                inflater,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        permissionChecker =
                new ModePermissionChecker(requireContext());

        backupManager =
                new BackupManager(requireContext());

        setupPermissionCards();
        setupTheme();
        setupLanguage();
        setupTools();
        setupBackup();
        renderPermissions();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding != null) {
            renderPermissions();
        }
    }

    private void setupTheme() {
        themeSpinnerReady = false;

        AppPreferences preferences =
                AppPreferences.getInstance(requireContext());

        int currentMode = preferences.getNightMode();
        int selectedPosition;

        if (currentMode
                == AppCompatDelegate.MODE_NIGHT_NO) {
            selectedPosition = 1;
        } else if (
                currentMode
                        == AppCompatDelegate.MODE_NIGHT_YES
        ) {
            selectedPosition = 2;
        } else {
            selectedPosition = 0;
        }

        /*
         * false یعنی Spinner برای تغییر مقدار اولیه انیمیشن نزند.
         * Listener هنوز فعال نیست، بنابراین تغییر ناخواسته رخ نمی‌دهد.
         */
        binding.spinnerTheme.setSelection(
                selectedPosition,
                false
        );

        binding.spinnerTheme.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View selectedView,
                            int position,
                            long id
                    ) {
                        if (!themeSpinnerReady) {
                            return;
                        }

                        applyTheme(position);
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );

        /*
         * اتصال Listener معمولاً یک callback اولیه ایجاد می‌کند.
         * با post صبر می‌کنیم تا آن callback تمام شود.
         */
        binding.spinnerTheme.post(() -> {
            if (binding != null) {
                themeSpinnerReady = true;
            }
        });
    }

    private void applyTheme(int selectedPosition) {
        int nightMode;

        if (selectedPosition == 1) {
            nightMode =
                    AppCompatDelegate.MODE_NIGHT_NO;
        } else if (selectedPosition == 2) {
            nightMode =
                    AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            nightMode =
                    AppCompatDelegate
                            .MODE_NIGHT_FOLLOW_SYSTEM;
        }

        AppPreferences preferences =
                AppPreferences.getInstance(requireContext());

        if (preferences.getNightMode() == nightMode) {
            return;
        }

        /*
         * ابتدا مقدار را ذخیره می‌کنیم، سپس AppCompat خودش Activity
         * را بازسازی می‌کند. recreate یا navigate دستی نباید انجام شود.
         */
        preferences.setNightMode(nightMode);

        AppCompatDelegate.setDefaultNightMode(
                nightMode
        );
    }

    private void setupLanguage() {
        languageSpinnerReady = false;

        String currentLanguage = getCurrentLanguage();

        int selectedPosition =
                "en".equals(currentLanguage) ? 1 : 0;

        binding.spinnerLanguage.setSelection(
                selectedPosition,
                false
        );

        binding.spinnerLanguage.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View selectedView,
                            int position,
                            long id
                    ) {
                        if (!languageSpinnerReady) {
                            return;
                        }

                        applyLanguage(position);
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );

        binding.spinnerLanguage.post(() -> {
            if (binding != null) {
                languageSpinnerReady = true;
            }
        });
    }

    private void applyLanguage(int selectedPosition) {
        String selectedLanguage =
                selectedPosition == 1 ? "en" : "fa";

        if (selectedLanguage.equals(
                getCurrentLanguage()
        )) {
            return;
        }

        /*
         * setApplicationLocales خودش Activity را بازسازی می‌کند.
         * اینجا نباید recreate، finish، startActivity یا navigate اجرا شود.
         */
        LocaleListCompat locales =
                LocaleListCompat.forLanguageTags(
                        selectedLanguage
                );

        AppCompatDelegate.setApplicationLocales(
                locales
        );
    }

    private String getCurrentLanguage() {
        LocaleListCompat appLocales =
                AppCompatDelegate.getApplicationLocales();

        if (!appLocales.isEmpty()
                && appLocales.get(0) != null) {
            return appLocales.get(0).getLanguage();
        }

        /*
         * وقتی هنوز زبان اختصاصی انتخاب نشده، زبان واقعی Resources
         * را می‌خوانیم؛ نه صرفاً Locale پیش‌فرض JVM.
         */
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.N) {
            Locale locale = requireContext()
                    .getResources()
                    .getConfiguration()
                    .getLocales()
                    .get(0);

            if (locale != null) {
                return locale.getLanguage();
            }
        }

        // minSdk پروژه 26 است، اما fallback برای ایمنی باقی مانده است.
        return Locale.getDefault().getLanguage();
    }

    private void setupPermissionCards() {
        binding.cardDndPermission.setOnClickListener(
                clicked -> startActivity(
                        new Intent(
                                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                        )
                )
        );

        binding.cardWriteSettingsPermission
                .setOnClickListener(clicked ->
                        startActivity(
                                new Intent(
                                        Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                        Uri.parse(
                                                "package:"
                                                        + requireContext()
                                                        .getPackageName()
                                        )
                                )
                        )
                );

        binding.cardNotificationPermission
                .setOnClickListener(clicked -> {
                    if (Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                        );
                    } else {
                        Intent intent = new Intent(
                                Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        );

                        intent.putExtra(
                                Settings.EXTRA_APP_PACKAGE,
                                requireContext().getPackageName()
                        );

                        startActivity(intent);
                    }
                });
    }

    private void setupTools() {
        binding.cardSchedules.setOnClickListener(
                clicked -> NavHostFragment
                        .findNavController(this)
                        .navigate(R.id.schedulesFragment)
        );

        binding.buttonAddTile.setOnClickListener(
                clicked -> requestTile()
        );

        binding.buttonAddWidget.setOnClickListener(
                clicked -> requestWidget()
        );
    }

    private void setupBackup() {
        binding.buttonExport.setOnClickListener(
                clicked -> createBackupLauncher.launch(
                        "mood-backup.json"
                )
        );

        binding.buttonImport.setOnClickListener(
                clicked -> openBackupLauncher.launch(
                        new String[]{
                                "application/json",
                                "text/plain"
                        }
                )
        );
    }

    private void renderPermissions() {
        if (binding == null || permissionChecker == null) {
            return;
        }

        boolean dnd =
                permissionChecker
                        .hasNotificationPolicyAccess();

        boolean writeSettings =
                permissionChecker
                        .hasWriteSettingsAccess();

        boolean notifications =
                Build.VERSION.SDK_INT
                        < Build.VERSION_CODES.TIRAMISU
                        || requireContext().checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED;

        renderPermission(
                binding.textDndStatus,
                binding.dotDndStatus,
                dnd
        );

        renderPermission(
                binding.textWriteSettingsStatus,
                binding.dotWriteSettingsStatus,
                writeSettings
        );

        renderPermission(
                binding.textNotificationStatus,
                binding.dotNotificationStatus,
                notifications
        );
    }

    private void renderPermission(
            android.widget.TextView label,
            View dot,
            boolean granted
    ) {
        label.setText(
                granted
                        ? R.string.permission_granted
                        : R.string.permission_required
        );

        dot.setActivated(granted);
    }

    private void requestTile() {
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.TIRAMISU) {
            StatusBarManager manager =
                    requireContext().getSystemService(
                            StatusBarManager.class
                    );

            if (manager != null) {
                manager.requestAddTileService(
                        new ComponentName(
                                requireContext(),
                                MoodTileService.class
                        ),
                        getString(R.string.tile_label),
                        Icon.createWithResource(
                                requireContext(),
                                R.drawable.ic_tile
                        ),
                        requireContext().getMainExecutor(),
                        result -> {
                            if (binding != null) {
                                Snackbar.make(
                                        binding.getRoot(),
                                        getString(
                                                R.string.tile_request_sent
                                        ),
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
                return;
            }
        }

        showMessage(
                getString(R.string.tile_manual_help)
        );
    }

    private void requestWidget() {
        AppWidgetManager manager =
                AppWidgetManager.getInstance(
                        requireContext()
                );

        ComponentName provider =
                new ComponentName(
                        requireContext(),
                        MoodWidgetProvider.class
                );

        if (manager.isRequestPinAppWidgetSupported()) {
            manager.requestPinAppWidget(
                    provider,
                    null,
                    null
            );
        } else {
            showMessage(
                    getString(R.string.widget_manual_help)
            );
        }
    }

    private void exportBackup(Uri uri) {
        backupManager.exportTo(
                uri,
                new BackupManager.Callback() {
                    @Override
                    public void onSuccess(String message) {
                        showMessage(message);
                    }

                    @Override
                    public void onError(
                            String message,
                            Throwable throwable
                    ) {
                        showMessage(message);
                    }
                }
        );
    }

    private void importBackup(Uri uri) {
        backupManager.importFrom(
                uri,
                new BackupManager.Callback() {
                    @Override
                    public void onSuccess(String message) {
                        showMessage(message);
                    }

                    @Override
                    public void onError(
                            String message,
                            Throwable throwable
                    ) {
                        showMessage(message);
                    }
                }
        );
    }

    private void showMessage(String message) {
        if (binding == null) {
            return;
        }

        Snackbar.make(
                binding.getRoot(),
                message,
                Snackbar.LENGTH_LONG
        ).show();
    }

    @Override
    public void onDestroyView() {
        themeSpinnerReady = false;
        languageSpinnerReady = false;
        binding = null;
        super.onDestroyView();
    }
}
