package com.bluepixel.mood.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluepixel.mood.R;
import com.bluepixel.mood.automation.NextRunCalculator;
import com.bluepixel.mood.data.database.entity.ScheduleEntity;
import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.databinding.FragmentHomeBinding;
import com.bluepixel.mood.mode.ActiveMode;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ModeEndReason;
import com.bluepixel.mood.ui.common.ModeActivationCoordinator;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.bluepixel.mood.ui.common.ModeVisuals;
import com.bluepixel.mood.util.TimeFormatter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ModeEngine engine;
    private ModeActivationCoordinator activationCoordinator;

    private final ActivityResultLauncher<String>
            notificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted && engine != null) {
                            engine.refreshNotification();
                        }
                    }
            );

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(
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

        engine = ModeEngine.getInstance(requireContext());

        ModeRepository repository =
                ModeRepository.getInstance(requireContext());

        activationCoordinator =
                new ModeActivationCoordinator(
                        this,
                        notificationPermissionLauncher,
                        activeMode -> renderActiveMode()
                );

        HomeModeAdapter adapter =
                new HomeModeAdapter(
                        mode -> activationCoordinator
                                .activate(mode)
                );

        binding.recyclerQuickModes.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        binding.recyclerQuickModes.setAdapter(adapter);

        repository.observeFavorites().observe(
                getViewLifecycleOwner(),
                modes -> {
                    if (modes == null || modes.isEmpty()) {
                        repository.observeModes().observe(
                                getViewLifecycleOwner(),
                                adapter::submitList
                        );
                    } else {
                        adapter.submitList(modes);
                    }
                }
        );

        repository.observeSchedules().observe(
                getViewLifecycleOwner(),
                this::renderNextSchedule
        );

        binding.buttonExtend15.setOnClickListener(
                clicked -> extend(15)
        );

        binding.buttonExtend30.setOnClickListener(
                clicked -> extend(30)
        );

        binding.buttonExtend60.setOnClickListener(
                clicked -> extend(60)
        );

        binding.buttonStopMode.setOnClickListener(
                clicked -> {
                    if (engine.deactivate(
                            ModeEndReason.USER_STOPPED
                    )) {
                        renderActiveMode();

                        Snackbar.make(
                                binding.getRoot(),
                                getString(
                                        R.string.home_settings_restored
                                ),
                                Snackbar.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        binding.buttonAllModes.setOnClickListener(
                clicked -> NavHostFragment
                        .findNavController(this)
                        .navigate(R.id.modesFragment)
        );

        binding.buttonCreateMode.setOnClickListener(
                clicked -> NavHostFragment
                        .findNavController(this)
                        .navigate(R.id.modeEditorFragment)
        );

        binding.cardNextSchedule.setOnClickListener(
                clicked -> NavHostFragment
                        .findNavController(this)
                        .navigate(R.id.schedulesFragment)
        );

        renderActiveMode();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (activationCoordinator != null) {
            activationCoordinator.onResume();
        }

        if (binding != null) {
            renderActiveMode();
        }
    }

    private void extend(int minutes) {
        if (engine.extend(minutes)) {
            renderActiveMode();

            Snackbar.make(
                    binding.getRoot(),
                    getString(
                            R.string.home_mode_extended,
                            TimeFormatter.number(
                                    requireContext(),
                                    minutes
                            )
                    ),
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }

    private void renderActiveMode() {
        ActiveMode activeMode =
                engine.getActiveMode();

        boolean active =
                activeMode != null;

        binding.layoutNormalMode.setVisibility(
                active ? View.GONE : View.VISIBLE
        );

        binding.layoutActiveMode.setVisibility(
                active ? View.VISIBLE : View.GONE
        );

        if (!active) {
            return;
        }

        String modeName =
                ModeTextResolver.name(
                        requireContext(),
                        activeMode.visualType,
                        activeMode.modeName
                );

        binding.textActiveModeName.setText(
                getString(
                        R.string.home_active_mode_title,
                        modeName
                )
        );

        binding.textActiveModeTime.setText(
                activeMode.hasAutomaticEnd()
                        ? getString(
                        R.string.home_auto_end_at,
                        TimeFormatter.time(
                                requireContext(),
                                activeMode.expectedEndAt
                        )
                )
                        : getString(
                        R.string.home_manual_end_hint
                )
        );

        binding.layoutExtendButtons.setVisibility(
                activeMode.hasAutomaticEnd()
                        ? View.VISIBLE
                        : View.GONE
        );

        binding.imageActiveMode.setImageResource(
                ModeVisuals.icon(
                        activeMode.visualType
                )
        );

        binding.imageActiveMode.setBackgroundResource(
                ModeVisuals.container(
                        activeMode.visualType
                )
        );

        binding.imageActiveMode.setColorFilter(
                ContextCompat.getColor(
                        requireContext(),
                        ModeVisuals.tint(
                                activeMode.visualType
                        )
                )
        );
    }

    private void renderNextSchedule(
            List<ScheduleEntity> schedules
    ) {
        if (schedules == null
                || schedules.isEmpty()) {
            binding.cardNextSchedule
                    .setVisibility(View.GONE);
            return;
        }

        ScheduleEntity next = null;
        long nextTime = Long.MAX_VALUE;

        for (ScheduleEntity schedule : schedules) {
            if (!schedule.isEnabled()) {
                continue;
            }

            long candidate =
                    NextRunCalculator.nextRun(
                            schedule,
                            System.currentTimeMillis()
                    );

            if (candidate < nextTime) {
                next = schedule;
                nextTime = candidate;
            }
        }

        if (next == null) {
            binding.cardNextSchedule
                    .setVisibility(View.GONE);
            return;
        }

        binding.cardNextSchedule
                .setVisibility(View.VISIBLE);

        binding.textNextScheduleName
                .setText(next.getTitle());

        binding.textNextScheduleTime.setText(
                getString(
                        R.string.home_next_schedule_at,
                        TimeFormatter.time(
                                requireContext(),
                                nextTime
                        )
                )
        );
    }

    @Override
    public void onDestroyView() {
        activationCoordinator = null;
        binding = null;
        super.onDestroyView();
    }
}
