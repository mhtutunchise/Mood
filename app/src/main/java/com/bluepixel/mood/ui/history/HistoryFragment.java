package com.bluepixel.mood.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluepixel.mood.R;
import com.bluepixel.mood.data.database.entity.ModeHistoryEntity;
import com.bluepixel.mood.data.repository.ModeRepository;
import com.bluepixel.mood.databinding.FragmentHistoryBinding;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.bluepixel.mood.util.TimeFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding =
                FragmentHistoryBinding.inflate(
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

        ModeRepository repository =
                ModeRepository.getInstance(
                        requireContext()
                );

        HistoryAdapter adapter =
                new HistoryAdapter();

        binding.recyclerHistory.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        binding.recyclerHistory.setAdapter(adapter);

        repository.observeHistory().observe(
                getViewLifecycleOwner(),
                items -> {
                    adapter.submitList(items);
                    renderState(items);
                }
        );

        binding.buttonClearHistory.setOnClickListener(clicked ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(
                                R.string.history_clear_dialog_title
                        )
                        .setMessage(
                                R.string.history_clear_dialog_message
                        )
                        .setNegativeButton(
                                R.string.history_cancel,
                                null
                        )
                        .setPositiveButton(
                                R.string.history_clear_dialog_confirm,
                                (dialog, which) ->
                                        repository.clearHistory()
                        )
                        .show()
        );
    }

    private void renderState(
            List<ModeHistoryEntity> items
    ) {
        boolean empty =
                items == null
                        || items.isEmpty();

        binding.layoutEmptyHistory.setVisibility(
                empty
                        ? View.VISIBLE
                        : View.GONE
        );

        binding.recyclerHistory.setVisibility(
                empty
                        ? View.GONE
                        : View.VISIBLE
        );

        binding.buttonClearHistory.setEnabled(
                !empty
        );

        if (empty) {
            binding.textTotalRuns.setText(
                    TimeFormatter.number(
                            requireContext(),
                            0
                    )
            );

            binding.textTotalDuration.setText(
                    TimeFormatter.duration(
                            requireContext(),
                            0
                    )
            );

            binding.textMostUsed.setText("-");

            return;
        }

        long totalDuration = 0L;

        Map<String, Integer> counts =
                new HashMap<>();

        for (ModeHistoryEntity item : items) {
            totalDuration += Math.max(
                    0,
                    item.getEndedAt()
                            - item.getStartedAt()
            );

            /*
             * مهم:
             * نامی که داخل History ذخیره شده ممکن است فارسی باشد.
             * برای آمار «Most used» باید نام قابل نمایش با زبان فعلی ساخته شود.
             */
            String displayName =
                    ModeTextResolver.name(
                            requireContext(),
                            item.getVisualType(),
                            item.getModeName()
                    );

            counts.put(
                    displayName,
                    counts.getOrDefault(
                            displayName,
                            0
                    ) + 1
            );
        }

        String mostUsed = "-";
        int highest = 0;

        for (Map.Entry<String, Integer> entry
                : counts.entrySet()) {
            if (entry.getValue() > highest) {
                highest = entry.getValue();
                mostUsed = entry.getKey();
            }
        }

        binding.textTotalRuns.setText(
                TimeFormatter.number(
                        requireContext(),
                        items.size()
                )
        );

        binding.textTotalDuration.setText(
                TimeFormatter.duration(
                        requireContext(),
                        totalDuration
                )
        );

        binding.textMostUsed.setText(
                mostUsed
        );
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
