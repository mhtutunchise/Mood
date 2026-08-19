package com.bluepixel.mood.ui.schedules;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bluepixel.mood.data.database.entity.ScheduleEntity;
import com.bluepixel.mood.databinding.ItemScheduleBinding;
import com.bluepixel.mood.util.DayMaskUtils;
import com.bluepixel.mood.util.PersianDigits;

import java.util.Locale;

public class ScheduleAdapter extends
        ListAdapter<ScheduleEntity, ScheduleAdapter.ViewHolder> {

    public interface Listener {
        void onEnabledChanged(
                ScheduleEntity schedule,
                boolean enabled
        );
        void onEdit(ScheduleEntity schedule);
        void onDelete(ScheduleEntity schedule);
    }

    private final Listener listener;

    public ScheduleAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ScheduleEntity> DIFF =
            new DiffUtil.ItemCallback<ScheduleEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ScheduleEntity oldItem,
                        @NonNull ScheduleEntity newItem
                ) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ScheduleEntity oldItem,
                        @NonNull ScheduleEntity newItem
                ) {
                    return oldItem.getUpdatedAt()
                            == newItem.getUpdatedAt()
                            && oldItem.isEnabled()
                            == newItem.isEnabled();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        return new ViewHolder(
                ItemScheduleBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
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
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemScheduleBinding binding;

        ViewHolder(ItemScheduleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ScheduleEntity schedule) {
            binding.textScheduleTitle.setText(
                    schedule.getTitle()
            );
            binding.textScheduleTime.setText(
                    "ساعت "
                            + PersianDigits.convert(
                            String.format(
                                    Locale.US,
                                    "%02d:%02d",
                                    schedule.getHour(),
                                    schedule.getMinute()
                            )
                    )
            );
            binding.textScheduleDays.setText(
                    DayMaskUtils.label(
                            schedule.getDaysMask()
                    )
            );

            binding.switchScheduleEnabled
                    .setOnCheckedChangeListener(null);
            binding.switchScheduleEnabled.setChecked(
                    schedule.isEnabled()
            );
            binding.switchScheduleEnabled
                    .setOnCheckedChangeListener(
                            (button, checked) ->
                                    listener.onEnabledChanged(
                                            schedule,
                                            checked
                                    )
                    );

            binding.buttonEditSchedule.setOnClickListener(
                    view -> listener.onEdit(schedule)
            );
            binding.buttonDeleteSchedule.setOnClickListener(
                    view -> listener.onDelete(schedule)
            );
        }
    }
}
