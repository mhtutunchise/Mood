package com.bluepixel.mood.ui.schedules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluepixel.mood.R;
import com.bluepixel.mood.automation.ScheduleManager;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;
import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.databinding.FragmentSchedulesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class SchedulesFragment extends Fragment {

    private FragmentSchedulesBinding binding;
    private ModeRepository repository;
    private ScheduleManager scheduleManager;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSchedulesBinding.inflate(
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

        binding.toolbarSchedules
                .setNavigationOnClickListener(
                        clicked -> NavHostFragment
                                .findNavController(this)
                                .navigateUp()
                );

        ScheduleAdapter adapter = new ScheduleAdapter(
                new ScheduleAdapter.Listener() {
                    @Override
                    public void onEnabledChanged(
                            ScheduleEntity schedule,
                            boolean enabled
                    ) {
                        schedule.setEnabled(enabled);
                        repository.saveSchedule(
                                schedule,
                                id -> {
                                    if (enabled) {
                                        scheduleManager.schedule(
                                                schedule
                                        );
                                    } else {
                                        scheduleManager.cancel(
                                                schedule.getId()
                                        );
                                    }
                                }
                        );
                    }

                    @Override
                    public void onEdit(
                            ScheduleEntity schedule
                    ) {
                        openEditor(schedule.getId());
                    }

                    @Override
                    public void onDelete(
                            ScheduleEntity schedule
                    ) {
                        confirmDelete(schedule);
                    }
                }
        );

        binding.recyclerSchedules.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.recyclerSchedules.setAdapter(adapter);

        repository.observeSchedules().observe(
                getViewLifecycleOwner(),
                schedules -> {
                    adapter.submitList(schedules);
                    renderEmpty(schedules);
                }
        );

        binding.fabAddSchedule.setOnClickListener(
                clicked -> openEditor(0)
        );
    }

    private void openEditor(long scheduleId) {
        Bundle args = new Bundle();
        args.putLong("scheduleId", scheduleId);

        NavHostFragment.findNavController(this)
                .navigate(
                        R.id.scheduleEditorFragment,
                        args
                );
    }

    private void confirmDelete(ScheduleEntity schedule) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("حذف زمان‌بندی")
                .setMessage(
                        "زمان‌بندی «"
                                + schedule.getTitle()
                                + "» حذف شود؟"
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
                        }
                )
                .show();
    }

    private void renderEmpty(
            List<ScheduleEntity> schedules
    ) {
        boolean empty =
                schedules == null || schedules.isEmpty();

        binding.layoutEmptySchedules.setVisibility(
                empty ? View.VISIBLE : View.GONE
        );
        binding.recyclerSchedules.setVisibility(
                empty ? View.GONE : View.VISIBLE
        );
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
