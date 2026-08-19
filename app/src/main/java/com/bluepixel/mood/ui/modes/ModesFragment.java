package com.bluepixel.mood.ui.modes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluepixel.mood.R;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.databinding.FragmentModesBinding;
import com.bluepixel.mood.mode.ActiveMode;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ModeEndReason;
import com.bluepixel.mood.ui.common.ModeActivationCoordinator;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class ModesFragment extends Fragment {

    private FragmentModesBinding binding;
    private ModeRepository repository;
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
        binding =
                FragmentModesBinding.inflate(
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
        super.onViewCreated(
                view,
                savedInstanceState
        );

        repository =
                ModeRepository.getInstance(
                        requireContext()
                );

        engine =
                ModeEngine.getInstance(
                        requireContext()
                );

        activationCoordinator =
                new ModeActivationCoordinator(
                        this,
                        notificationPermissionLauncher,
                        activeMode -> {
                        }
                );

        ModeAdapter adapter =
                new ModeAdapter(
                        new ModeAdapter.Listener() {
                            @Override
                            public void onRun(
                                    ModeEntity mode
                            ) {
                                if (activationCoordinator != null) {
                                    activationCoordinator.activate(mode);
                                }
                            }

                            @Override
                            public void onEdit(
                                    ModeEntity mode
                            ) {
                                openEditor(
                                        mode.getId()
                                );
                            }

                            @Override
                            public void onCopy(
                                    ModeEntity mode
                            ) {
                                repository.copyMode(mode);

                                Snackbar.make(
                                        binding.getRoot(),
                                        getString(
                                                R.string.modes_copy_created
                                        ),
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }

                            @Override
                            public void onDelete(
                                    ModeEntity mode
                            ) {
                                confirmDelete(mode);
                            }

                            @Override
                            public void onFavoriteChanged(
                                    ModeEntity mode
                            ) {
                                repository.saveMode(
                                        mode,
                                        null
                                );
                            }
                        }
                );

        binding.recyclerModes.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        binding.recyclerModes.setAdapter(adapter);

        repository.observeModes().observe(
                getViewLifecycleOwner(),
                adapter::submitList
        );

        binding.fabAddMode.setOnClickListener(
                clicked -> openEditor(0)
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        if (activationCoordinator != null) {
            activationCoordinator.onResume();
        }
    }

    private void openEditor(
            long modeId
    ) {
        Bundle args =
                new Bundle();

        args.putLong(
                "modeId",
                modeId
        );

        NavHostFragment
                .findNavController(this)
                .navigate(
                        R.id.modeEditorFragment,
                        args
                );
    }

    private void confirmDelete(
            ModeEntity mode
    ) {
        String modeName =
                ModeTextResolver.name(
                        requireContext(),
                        mode.getVisualType(),
                        mode.getName()
                );

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(
                        R.string.modes_delete_title
                )
                .setMessage(
                        getString(
                                R.string.modes_delete_message,
                                modeName
                        )
                )
                .setNegativeButton(
                        R.string.modes_cancel,
                        null
                )
                .setPositiveButton(
                        R.string.modes_delete_confirm,
                        (dialog, which) ->
                                deleteMode(mode)
                )
                .show();
    }

    private void deleteMode(
            ModeEntity mode
    ) {
        ActiveMode activeMode =
                engine == null
                        ? null
                        : engine.getActiveMode();

        if (activeMode != null
                && activeMode.modeId == mode.getId()) {
            engine.deactivate(
                    ModeEndReason.USER_STOPPED
            );
        }

        repository.deleteMode(
                mode.getId(),
                deleted -> {
                    if (binding == null) {
                        return;
                    }

                    Snackbar.make(
                            binding.getRoot(),
                            deleted
                                    ? getString(
                                    R.string.modes_deleted
                            )
                                    : getString(
                                    R.string.modes_delete_failed
                            ),
                            Snackbar.LENGTH_SHORT
                    ).show();
                }
        );
    }

    @Override
    public void onDestroyView() {
        activationCoordinator = null;
        binding = null;
        super.onDestroyView();
    }
}
