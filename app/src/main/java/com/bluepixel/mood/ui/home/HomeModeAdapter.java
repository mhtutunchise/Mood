package com.bluepixel.mood.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.databinding.ItemHomeModeBinding;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.bluepixel.mood.ui.common.ModeVisuals;

import java.util.Objects;

public class HomeModeAdapter extends
        ListAdapter<ModeEntity, HomeModeAdapter.ViewHolder> {

    public interface Listener {
        void onClick(ModeEntity mode);
    }

    private final Listener listener;

    public HomeModeAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ModeEntity> DIFF =
            new DiffUtil.ItemCallback<ModeEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ModeEntity oldItem,
                        @NonNull ModeEntity newItem
                ) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ModeEntity oldItem,
                        @NonNull ModeEntity newItem
                ) {
                    return Objects.equals(
                            oldItem.getName(),
                            newItem.getName()
                    )
                            && Objects.equals(
                            oldItem.getDescription(),
                            newItem.getDescription()
                    )
                            && Objects.equals(
                            oldItem.getVisualType(),
                            newItem.getVisualType()
                    )
                            && oldItem.isFavorite()
                            == newItem.isFavorite();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        return new ViewHolder(
                ItemHomeModeBinding.inflate(
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
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemHomeModeBinding binding;

        ViewHolder(ItemHomeModeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ModeEntity mode) {

            binding.textModeName.setText(
                    ModeTextResolver.name(
                            binding.getRoot().getContext(),
                            mode.getVisualType(),
                            mode.getName()
                    )
            );

            binding.textModeDescription.setText(
                    ModeTextResolver.description(
                            binding.getRoot().getContext(),
                            mode.getVisualType(),
                            mode.getDescription()
                    )
            );

            binding.imageMode.setImageResource(
                    ModeVisuals.icon(
                            mode.getVisualType()
                    )
            );

            binding.imageMode.setBackgroundResource(
                    ModeVisuals.container(
                            mode.getVisualType()
                    )
            );

            binding.imageMode.setColorFilter(
                    ContextCompat.getColor(
                            binding.getRoot().getContext(),
                            ModeVisuals.tint(
                                    mode.getVisualType()
                            )
                    )
            );

            binding.getRoot().setOnClickListener(
                    view -> listener.onClick(mode)
            );
        }
    }
}
