package com.bluepixel.mood.ui.editor;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bluepixel.mood.R;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.databinding.FragmentModeEditorBinding;
import com.bluepixel.mood.model.ModeActions;
import com.bluepixel.mood.model.ModeEndType;
import com.bluepixel.mood.model.ModeVisualType;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.bluepixel.mood.util.PersianDigits;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class ModeEditorFragment extends Fragment {

    private FragmentModeEditorBinding binding;
    private ModeRepository repository;
    private ModeEntity mode;
    private long modeId;
    private int selectedEndHour;
    private int selectedEndMinute;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentModeEditorBinding.inflate(
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

        repository =
                ModeRepository.getInstance(requireContext());

        modeId =
                getArguments() == null
                        ? 0
                        : getArguments().getLong(
                        "modeId",
                        0
                );

        setupToolbar();
        setupSliders();
        setupSwitches();
        setupEndType();
        setupActions();

        if (modeId == 0) {
            mode = createNewMode();
            populate(mode);
        } else {
            repository.getMode(modeId, result -> {
                if (result == null) {
                    Snackbar.make(
                            binding.getRoot(),
                            getString(
                                    R.string.mode_editor_mode_not_found
                            ),
                            Snackbar.LENGTH_LONG
                    ).show();

                    NavHostFragment
                            .findNavController(this)
                            .navigateUp();

                    return;
                }

                mode = result;
                populate(mode);
            });
        }
    }

    private void setupToolbar() {
        binding.toolbarModeEditor
                .setNavigationOnClickListener(
                        clicked -> NavHostFragment
                                .findNavController(this)
                                .navigateUp()
                );
    }

    private void setupSliders() {
        bindSlider(
                binding.sliderRing,
                binding.textRingValue
        );

        bindSlider(
                binding.sliderNotification,
                binding.textNotificationValue
        );

        bindSlider(
                binding.sliderMedia,
                binding.textMediaValue
        );

        bindSlider(
                binding.sliderAlarm,
                binding.textAlarmValue
        );

        bindSlider(
                binding.sliderBrightness,
                binding.textBrightnessValue
        );
    }

    private void bindSlider(
            Slider slider,
            android.widget.TextView label
    ) {
        renderSliderValue(
                label,
                Math.round(slider.getValue())
        );

        slider.addOnChangeListener(
                (control, value, fromUser) ->
                        renderSliderValue(
                                label,
                                Math.round(value)
                        )
        );
    }

    private void renderSliderValue(
            android.widget.TextView label,
            long value
    ) {
        label.setText(
                getString(
                        R.string.mode_editor_percent_format,
                        localizedNumber(value)
                )
        );
    }

    private void setupSwitches() {
        binding.switchRing.setOnCheckedChangeListener(
                (button, checked) ->
                        binding.sliderRing.setEnabled(checked)
        );

        binding.switchNotification.setOnCheckedChangeListener(
                (button, checked) ->
                        binding.sliderNotification
                                .setEnabled(checked)
        );

        binding.switchMedia.setOnCheckedChangeListener(
                (button, checked) ->
                        binding.sliderMedia.setEnabled(checked)
        );

        binding.switchAlarm.setOnCheckedChangeListener(
                (button, checked) ->
                        binding.sliderAlarm.setEnabled(checked)
        );

        binding.switchBrightness.setOnCheckedChangeListener(
                (button, checked) ->
                        binding.sliderBrightness
                                .setEnabled(checked)
        );
    }

    private void setupEndType() {
        binding.radioEndType.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    binding.layoutDuration.setVisibility(
                            checkedId
                                    == binding.radioDuration.getId()
                                    ? View.VISIBLE
                                    : View.GONE
                    );

                    binding.buttonEndTime.setVisibility(
                            checkedId
                                    == binding.radioClock.getId()
                                    ? View.VISIBLE
                                    : View.GONE
                    );
                }
        );

        binding.buttonEndTime.setOnClickListener(clicked ->
                new TimePickerDialog(
                        requireContext(),
                        (picker, hour, minute) -> {
                            selectedEndHour = hour;
                            selectedEndMinute = minute;
                            renderEndTime();
                        },
                        selectedEndHour,
                        selectedEndMinute,
                        true
                ).show()
        );
    }

    private void setupActions() {
        binding.buttonSaveMode.setOnClickListener(
                clicked -> save()
        );

        binding.buttonDeleteMode.setOnClickListener(clicked ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(
                                R.string.mode_editor_delete_title
                        )
                        .setMessage(
                                R.string.mode_editor_delete_message
                        )
                        .setNegativeButton(
                                R.string.mode_editor_cancel,
                                null
                        )
                        .setPositiveButton(
                                R.string.mode_editor_delete_confirm,
                                (dialog, which) -> {
                                    repository.deleteMode(
                                            mode.getId()
                                    );

                                    NavHostFragment
                                            .findNavController(this)
                                            .navigateUp();
                                }
                        )
                        .show()
        );
    }

    private ModeEntity createNewMode() {
        ModeEntity value = new ModeEntity();
        value.setName("");
        value.setDescription("");
        value.setVisualType(ModeVisualType.CUSTOM);
        value.setBuiltIn(false);
        value.setEnabled(true);
        value.setFavorite(false);
        value.setEndType(ModeEndType.MANUAL);
        value.setDurationMinutes(60);
        value.setRingerMode(ModeActions.UNCHANGED);
        value.setInterruptionFilter(
                ModeActions.UNCHANGED
        );
        return value;
    }

    private void populate(ModeEntity value) {
        binding.toolbarModeEditor.setTitle(
                value.getId() == 0
                        ? getString(
                        R.string.mode_editor_toolbar_new
                )
                        : getString(
                        R.string.mode_editor_toolbar_edit_format,
                        ModeTextResolver.name(
                                requireContext(),
                                value.getVisualType(),
                                value.getName()
                        )
                )
        );

        binding.editModeName.setText(
                value.getName()
        );

        binding.editModeDescription.setText(
                value.getDescription()
        );

        binding.spinnerVisualType.setSelection(
                ModeVisualType.toPosition(
                        value.getVisualType()
                )
        );

        binding.switchFavorite.setChecked(
                value.isFavorite()
        );

        binding.switchRing.setChecked(
                value.isChangeRingVolume()
        );

        binding.sliderRing.setValue(
                value.getRingVolumePercent()
        );

        binding.switchNotification.setChecked(
                value.isChangeNotificationVolume()
        );

        binding.sliderNotification.setValue(
                value.getNotificationVolumePercent()
        );

        binding.switchMedia.setChecked(
                value.isChangeMediaVolume()
        );

        binding.sliderMedia.setValue(
                value.getMediaVolumePercent()
        );

        binding.switchAlarm.setChecked(
                value.isChangeAlarmVolume()
        );

        binding.sliderAlarm.setValue(
                value.getAlarmVolumePercent()
        );

        binding.spinnerRingerMode.setSelection(
                ModeActions.ringerToPosition(
                        value.getRingerMode()
                )
        );

        binding.spinnerDndMode.setSelection(
                ModeActions.dndToPosition(
                        value.getInterruptionFilter()
                )
        );

        binding.switchBrightness.setChecked(
                value.isChangeBrightness()
        );

        binding.sliderBrightness.setValue(
                Math.max(
                        1,
                        value.getBrightnessPercent()
                )
        );

        selectedEndHour =
                value.getEndHour();

        selectedEndMinute =
                value.getEndMinute();

        if (ModeEndType.DURATION.equals(
                value.getEndType()
        )) {
            binding.radioDuration.setChecked(true);
        } else if (ModeEndType.CLOCK.equals(
                value.getEndType()
        )) {
            binding.radioClock.setChecked(true);
        } else {
            binding.radioManual.setChecked(true);
        }

        binding.editDuration.setText(
                String.valueOf(
                        value.getDurationMinutes()
                )
        );

        renderEndTime();

        binding.buttonDeleteMode.setVisibility(
                value.getId() != 0
                        && !value.isBuiltIn()
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void save() {
        String name =
                binding.editModeName.getText() == null
                        ? ""
                        : binding.editModeName.getText()
                          .toString()
                          .trim();

        if (TextUtils.isEmpty(name)) {
            binding.editModeName.setError(
                    getString(
                            R.string.mode_editor_name_required
                    )
            );

            binding.editModeName.requestFocus();

            return;
        }

        mode.setName(name);

        mode.setDescription(
                binding.editModeDescription.getText() == null
                        ? ""
                        : binding.editModeDescription.getText()
                          .toString()
                          .trim()
        );

        mode.setVisualType(
                ModeVisualType.fromPosition(
                        binding.spinnerVisualType
                                .getSelectedItemPosition()
                )
        );

        mode.setFavorite(
                binding.switchFavorite.isChecked()
        );

        mode.setChangeRingVolume(
                binding.switchRing.isChecked()
        );

        mode.setRingVolumePercent(
                Math.round(
                        binding.sliderRing.getValue()
                )
        );

        mode.setChangeNotificationVolume(
                binding.switchNotification.isChecked()
        );

        mode.setNotificationVolumePercent(
                Math.round(
                        binding.sliderNotification.getValue()
                )
        );

        mode.setChangeMediaVolume(
                binding.switchMedia.isChecked()
        );

        mode.setMediaVolumePercent(
                Math.round(
                        binding.sliderMedia.getValue()
                )
        );

        mode.setChangeAlarmVolume(
                binding.switchAlarm.isChecked()
        );

        mode.setAlarmVolumePercent(
                Math.round(
                        binding.sliderAlarm.getValue()
                )
        );

        mode.setRingerMode(
                ModeActions.ringerFromPosition(
                        binding.spinnerRingerMode
                                .getSelectedItemPosition()
                )
        );

        mode.setInterruptionFilter(
                ModeActions.dndFromPosition(
                        binding.spinnerDndMode
                                .getSelectedItemPosition()
                )
        );

        mode.setChangeBrightness(
                binding.switchBrightness.isChecked()
        );

        mode.setBrightnessPercent(
                Math.round(
                        binding.sliderBrightness.getValue()
                )
        );

        if (binding.radioDuration.isChecked()) {
            String durationText =
                    binding.editDuration.getText() == null
                            ? ""
                            : binding.editDuration.getText()
                              .toString()
                              .trim();

            int duration;

            try {
                duration =
                        Integer.parseInt(durationText);
            } catch (NumberFormatException exception) {
                duration = 0;
            }

            if (duration <= 0) {
                binding.editDuration.setError(
                        getString(
                                R.string.mode_editor_duration_required
                        )
                );

                return;
            }

            mode.setEndType(
                    ModeEndType.DURATION
            );

            mode.setDurationMinutes(
                    duration
            );

        } else if (binding.radioClock.isChecked()) {
            mode.setEndType(
                    ModeEndType.CLOCK
            );

            mode.setEndHour(
                    selectedEndHour
            );

            mode.setEndMinute(
                    selectedEndMinute
            );

        } else {
            mode.setEndType(
                    ModeEndType.MANUAL
            );
        }

        repository.saveMode(mode, id -> {
            Snackbar.make(
                    binding.getRoot(),
                    getString(
                            R.string.mode_editor_saved
                    ),
                    Snackbar.LENGTH_SHORT
            ).show();

            NavHostFragment
                    .findNavController(this)
                    .navigateUp();
        });
    }

    private void renderEndTime() {
        binding.buttonEndTime.setText(
                getString(
                        R.string.mode_editor_end_at_time,
                        localizedTime(
                                selectedEndHour,
                                selectedEndMinute
                        )
                )
        );
    }

    private String localizedTime(
            int hour,
            int minute
    ) {
        String time =
                String.format(
                        Locale.US,
                        "%02d:%02d",
                        hour,
                        minute
                );

        return isPersian()
                ? PersianDigits.convert(time)
                : time;
    }

    private String localizedNumber(
            long value
    ) {
        return isPersian()
                ? PersianDigits.from(value)
                : String.valueOf(value);
    }

    private boolean isPersian() {
        Locale locale =
                requireContext()
                        .getResources()
                        .getConfiguration()
                        .getLocales()
                        .get(0);

        return locale != null
                && "fa".equals(
                locale.getLanguage()
        );
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
