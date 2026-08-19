package com.bluepixel.mood.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bluepixel.mood.R;
import com.bluepixel.mood.data.database.entity.ModeHistoryEntity;
import com.bluepixel.mood.databinding.ItemHistoryBinding;
import com.bluepixel.mood.model.ActivationSource;
import com.bluepixel.mood.model.ModeEndReason;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.bluepixel.mood.ui.common.ModeVisuals;
import com.bluepixel.mood.util.TimeFormatter;

public class HistoryAdapter extends
        ListAdapter<ModeHistoryEntity, HistoryAdapter.ViewHolder> {

    public HistoryAdapter() {
        super(DIFF);
    }

    private static final DiffUtil.ItemCallback<ModeHistoryEntity> DIFF =
            new DiffUtil.ItemCallback<ModeHistoryEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ModeHistoryEntity oldItem,
                        @NonNull ModeHistoryEntity newItem
                ) {
                    return oldItem.getId()
                            == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ModeHistoryEntity oldItem,
                        @NonNull ModeHistoryEntity newItem
                ) {
                    return oldItem.getEndedAt()
                            == newItem.getEndedAt()
                            && oldItem.isSuccessful()
                            == newItem.isSuccessful();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        return new ViewHolder(
                ItemHistoryBinding.inflate(
                        LayoutInflater.from(
                                parent.getContext()
                        ),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        holder.bind(
                getItem(position)
        );
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemHistoryBinding binding;

        ViewHolder(
                ItemHistoryBinding binding
        ) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(
                ModeHistoryEntity item
        ) {
            Context context =
                    binding.getRoot().getContext();

            binding.textHistoryName.setText(
                    ModeTextResolver.name(
                            context,
                            item.getVisualType(),
                            item.getModeName()
                    )
            );

            binding.textHistoryTime.setText(
                    context.getString(
                            R.string.history_item_time_format,
                            TimeFormatter.dateTime(
                                    context,
                                    item.getStartedAt()
                            ),
                            TimeFormatter.duration(
                                    context,
                                    Math.max(
                                            0,
                                            item.getEndedAt()
                                                    - item.getStartedAt()
                                    )
                            )
                    )
            );

            binding.textHistoryReason.setText(
                    reasonLabel(
                            context,
                            item.getEndReason()
                    )
            );

            binding.textHistorySource.setText(
                    sourceLabel(
                            context,
                            item.getActivationSource()
                    )
            );

            binding.imageHistory.setImageResource(
                    ModeVisuals.icon(
                            item.getVisualType()
                    )
            );

            binding.imageHistory.setBackgroundResource(
                    ModeVisuals.container(
                            item.getVisualType()
                    )
            );

            binding.imageHistory.setColorFilter(
                    ContextCompat.getColor(
                            context,
                            ModeVisuals.tint(
                                    item.getVisualType()
                            )
                    )
            );
        }

        private String reasonLabel(
                Context context,
                String reason
        ) {
            if (ModeEndReason.TIME_FINISHED.equals(reason)) {
                return context.getString(
                        R.string.resolver_history_reason_time_finished
                );
            }

            if (ModeEndReason.NEW_MODE_STARTED.equals(reason)) {
                return context.getString(
                        R.string.resolver_history_reason_new_mode_started
                );
            }

            if (ModeEndReason.DEVICE_RESTARTED.equals(reason)) {
                return context.getString(
                        R.string.resolver_history_reason_device_restarted
                );
            }

            if (ModeEndReason.ERROR.equals(reason)) {
                return context.getString(
                        R.string.resolver_history_reason_error
                );
            }

            return context.getString(
                    R.string.resolver_history_reason_manual
            );
        }

        private String sourceLabel(
                Context context,
                String source
        ) {
            if (ActivationSource.SCHEDULE.equals(source)) {
                return context.getString(
                        R.string.resolver_history_source_schedule
                );
            }

            if (ActivationSource.TILE.equals(source)) {
                return context.getString(
                        R.string.resolver_history_source_tile
                );
            }

            if (ActivationSource.WIDGET.equals(source)) {
                return context.getString(
                        R.string.resolver_history_source_widget
                );
            }

            return context.getString(
                    R.string.resolver_history_source_manual
            );
        }
    }
}
