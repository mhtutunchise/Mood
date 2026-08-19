package com.bluepixel.mood.ui.modes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bluepixel.mood.R;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.databinding.ItemModeBinding;
import com.bluepixel.mood.model.ModeEndType;
import com.bluepixel.mood.ui.common.ModeTextResolver;
import com.bluepixel.mood.ui.common.ModeVisuals;

import java.util.Objects;

public class ModeAdapter extends
        ListAdapter<ModeEntity, ModeAdapter.ViewHolder> {

    public interface Listener {
        void onRun(ModeEntity mode);

        void onEdit(ModeEntity mode);

        void onCopy(ModeEntity mode);

        void onDelete(ModeEntity mode);

        void onFavoriteChanged(ModeEntity mode);
    }

    private final Listener listener;

    public ModeAdapter(
            Listener listener
    ) {
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
                    return oldItem.getId()
                            == newItem.getId();
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
                            && Objects.equals(
                            oldItem.getEndType(),
                            newItem.getEndType()
                    )
                            && oldItem.isBuiltIn()
                            == newItem.isBuiltIn()
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
                ItemModeBinding.inflate(
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

        private final ItemModeBinding binding;

        ViewHolder(
                ItemModeBinding binding
        ) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(
                ModeEntity mode
        ) {
            Context context =
                    binding.getRoot().getContext();

            binding.textModeName.setText(
                    ModeTextResolver.name(
                            context,
                            mode.getVisualType(),
                            mode.getName()
                    )
            );

            binding.textModeDescription.setText(
                    ModeTextResolver.description(
                            context,
                            mode.getVisualType(),
                            mode.getDescription()
                    )
            );

            binding.textModeEnd.setText(
                    endLabel(
                            context,
                            mode.getEndType()
                    )
            );

            binding.textBuiltIn.setVisibility(
                    mode.isBuiltIn()
                            ? View.VISIBLE
                            : View.GONE
            );

            binding.textBuiltIn.setText(
                    context.getString(
                            R.string.modes_badge_built_in
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
                            context,
                            ModeVisuals.tint(
                                    mode.getVisualType()
                            )
                    )
            );

            binding.buttonFavorite.setImageResource(
                    mode.isFavorite()
                            ? R.drawable.ic_star
                            : R.drawable.ic_star_outline
            );

            binding.buttonFavorite.setContentDescription(
                    context.getString(
                            mode.isFavorite()
                                    ? R.string.modes_remove_favorite
                                    : R.string.modes_add_favorite
                    )
            );

            /*
             * حالا حذف برای همه مودها فعال است؛ حتی Built-inها.
             */
            binding.buttonDelete.setVisibility(
                    View.VISIBLE
            );

            binding.buttonRun.setOnClickListener(
                    view -> listener.onRun(mode)
            );

            binding.buttonEdit.setOnClickListener(
                    view -> listener.onEdit(mode)
            );

            binding.buttonCopy.setOnClickListener(
                    view -> listener.onCopy(mode)
            );

            binding.buttonDelete.setOnClickListener(
                    view -> listener.onDelete(mode)
            );

            binding.buttonFavorite.setOnClickListener(view -> {
                int position =
                        getBindingAdapterPosition();

                if (position == RecyclerView.NO_POSITION) {
                    return;
                }

                mode.setFavorite(
                        !mode.isFavorite()
                );

                listener.onFavoriteChanged(mode);

                notifyItemChanged(position);
            });
        }

        private String endLabel(
                Context context,
                String endType
        ) {
            if (ModeEndType.DURATION.equals(endType)) {
                return context.getString(
                        R.string.modes_end_duration
                );
            }

            if (ModeEndType.CLOCK.equals(endType)) {
                return context.getString(
                        R.string.modes_end_clock
                );
            }

            return context.getString(
                    R.string.modes_end_manual
            );
        }
    }
}
