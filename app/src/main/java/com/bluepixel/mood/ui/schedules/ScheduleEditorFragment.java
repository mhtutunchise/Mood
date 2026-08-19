package com.bluepixel.mood.ui.schedules;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bluepixel.mood.automation.ScheduleManager;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;
import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.databinding.FragmentScheduleEditorBinding;
import com.bluepixel.mood.util.DayMaskUtils;
import com.bluepixel.mood.util.PersianDigits;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScheduleEditorFragment extends Fragment {

    private FragmentScheduleEditorBinding binding;
    private ModeRepository repository;
    private ScheduleManager scheduleManager;

    private long scheduleId;
    private ScheduleEntity schedule;
    private List<ModeEntity> modes = new ArrayList<>();
    private int selectedHour = 22;
    private int selectedMinute = 0;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentScheduleEditorBinding.inflate(
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

        repository = ModeRepository.getInstance(
                requireContext()
        );
        scheduleManager = new ScheduleManager(
                requireContext()
        );

        scheduleId = getArguments() == null
                ? 0
                : getArguments().getLong(
                        "scheduleId",
                        0
                );

        binding.toolbarScheduleEditor
                .setNavigationOnClickListener(
                        clicked -> NavHostFragment
                                .findNavController(this)
                                .navigateUp()
                );

        binding.buttonScheduleTime.setOnClickListener(
                clicked -> showTimePicker()
        );

        binding.buttonSaveSchedule.setOnClickListener(
                clicked -> save()
        );

        binding.buttonDeleteSchedule.setOnClickListener(
                clicked -> confirmDelete()
        );

        repository.getAllModes(result -> {
            modes = result == null
                    ? new ArrayList<>()
                    : result;
            setupModeSpinner();

            if (scheduleId == 0) {
                schedule = createNewSchedule();
                populate();
            } else {
                repository.getSchedule(
                        scheduleId,
                        loaded -> {
                            if (loaded == null) {
                                Snackbar.make(
                                        binding.getRoot(),
                                        "زمان‌بندی پیدا نشد.",
                                        Snackbar.LENGTH_LONG
                                ).show();
                                NavHostFragment
                                        .findNavController(this)
                                        .navigateUp();
                                return;
                            }
                            schedule = loaded;
                            populate();
                        }
                );
            }
        });
    }

    private ScheduleEntity createNewSchedule() {
        ScheduleEntity value = new ScheduleEntity();
        value.setTitle("");
        value.setHour(selectedHour);
        value.setMinute(selectedMinute);
        value.setDaysMask(127);
        value.setEnabled(true);
        if (!modes.isEmpty()) {
            value.setModeId(modes.get(0).getId());
        }
        return value;
    }

    private void setupModeSpinner() {
        List<String> names = new ArrayList<>();
        for (ModeEntity mode : modes) {
            names.add(mode.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        binding.spinnerScheduleMode.setAdapter(adapter);
    }

    private void populate() {
        binding.toolbarScheduleEditor.setTitle(
                schedule.getId() == 0
                        ? "زمان‌بندی جدید"
                        : "ویرایش زمان‌بندی"
        );

        binding.editScheduleTitle.setText(
                schedule.getTitle()
        );
        selectedHour = schedule.getHour();
        selectedMinute = schedule.getMinute();
        binding.switchScheduleEnabled.setChecked(
                schedule.isEnabled()
        );

        for (int i = 0; i < modes.size(); i++) {
            if (modes.get(i).getId()
                    == schedule.getModeId()) {
                binding.spinnerScheduleMode.setSelection(i);
                break;
            }
        }

        setDayChips(schedule.getDaysMask());
        renderTime();

        binding.buttonDeleteSchedule.setVisibility(
                schedule.getId() == 0
                        ? View.GONE
                        : View.VISIBLE
        );
    }

    private void showTimePicker() {
        new TimePickerDialog(
                requireContext(),
                (picker, hour, minute) -> {
                    selectedHour = hour;
                    selectedMinute = minute;
                    renderTime();
                },
                selectedHour,
                selectedMinute,
                true
        ).show();
    }

    private void renderTime() {
        binding.buttonScheduleTime.setText(
                "ساعت اجرا: "
                        + PersianDigits.convert(
                        String.format(
                                Locale.US,
                                "%02d:%02d",
                                selectedHour,
                                selectedMinute
                        )
                )
        );
    }

    private void save() {
        if (modes.isEmpty()) {
            Snackbar.make(
                    binding.getRoot(),
                    "ابتدا یک حالت بساز.",
                    Snackbar.LENGTH_LONG
            ).show();
            return;
        }

        String title =
                binding.editScheduleTitle.getText() == null
                        ? ""
                        : binding.editScheduleTitle.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(title)) {
            binding.editScheduleTitle.setError(
                    "عنوان را وارد کن."
            );
            return;
        }

        int daysMask = readDaysMask();
        if (daysMask == 0) {
            Snackbar.make(
                    binding.getRoot(),
                    "حداقل یک روز را انتخاب کن.",
                    Snackbar.LENGTH_LONG
            ).show();
            return;
        }

        int selectedPosition =
                binding.spinnerScheduleMode
                        .getSelectedItemPosition();

        schedule.setTitle(title);
        schedule.setModeId(
                modes.get(selectedPosition).getId()
        );
        schedule.setHour(selectedHour);
        schedule.setMinute(selectedMinute);
        schedule.setDaysMask(daysMask);
        schedule.setEnabled(
                binding.switchScheduleEnabled.isChecked()
        );

        repository.saveSchedule(
                schedule,
                id -> {
                    schedule.setId(id);

                    if (schedule.isEnabled()) {
                        scheduleManager.schedule(schedule);
                    } else {
                        scheduleManager.cancel(id);
                    }

                    NavHostFragment
                            .findNavController(this)
                            .navigateUp();
                }
        );
    }

    private int readDaysMask() {
        int mask = 0;
        if (binding.chipSaturday.isChecked()) {
            mask |= DayMaskUtils.SATURDAY;
        }
        if (binding.chipSunday.isChecked()) {
            mask |= DayMaskUtils.SUNDAY;
        }
        if (binding.chipMonday.isChecked()) {
            mask |= DayMaskUtils.MONDAY;
        }
        if (binding.chipTuesday.isChecked()) {
            mask |= DayMaskUtils.TUESDAY;
        }
        if (binding.chipWednesday.isChecked()) {
            mask |= DayMaskUtils.WEDNESDAY;
        }
        if (binding.chipThursday.isChecked()) {
            mask |= DayMaskUtils.THURSDAY;
        }
        if (binding.chipFriday.isChecked()) {
            mask |= DayMaskUtils.FRIDAY;
        }
        return mask;
    }

    private void setDayChips(int mask) {
        binding.chipSaturday.setChecked(
                (mask & DayMaskUtils.SATURDAY) != 0
        );
        binding.chipSunday.setChecked(
                (mask & DayMaskUtils.SUNDAY) != 0
        );
        binding.chipMonday.setChecked(
                (mask & DayMaskUtils.MONDAY) != 0
        );
        binding.chipTuesday.setChecked(
                (mask & DayMaskUtils.TUESDAY) != 0
        );
        binding.chipWednesday.setChecked(
                (mask & DayMaskUtils.WEDNESDAY) != 0
        );
        binding.chipThursday.setChecked(
                (mask & DayMaskUtils.THURSDAY) != 0
        );
        binding.chipFriday.setChecked(
                (mask & DayMaskUtils.FRIDAY) != 0
        );
    }

    private void confirmDelete() {
        if (schedule == null || schedule.getId() == 0) {
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("حذف زمان‌بندی")
                .setMessage(
                        "این زمان‌بندی برای همیشه حذف شود؟"
                )
                .setNegativeButton("انصراف", null)
                .setPositiveButton(
                        "حذف",
                        (dialog, which) -> {
                            scheduleManager.cancel(
                                    schedule.getId()
                            );
                            repository.deleteSchedule(
                                    schedule.getId()
                            );
                            NavHostFragment
                                    .findNavController(this)
                                    .navigateUp();
                        }
                )
                .show();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
