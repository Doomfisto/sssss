package com.foodrecognition.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.foodrecognition.app.databinding.ItemHistoryBinding;
import com.foodrecognition.app.model.NutritionRecord;

/**
 * 历史记录列表适配器
 */
public class HistoryAdapter extends ListAdapter<NutritionRecord, HistoryAdapter.HistoryViewHolder> {

    public HistoryAdapter() {
        super(new DiffUtil.ItemCallback<NutritionRecord>() {
            @Override
            public boolean areItemsTheSame(@NonNull NutritionRecord oldItem, @NonNull NutritionRecord newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull NutritionRecord oldItem, @NonNull NutritionRecord newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryBinding binding;

        public HistoryViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NutritionRecord record) {
            binding.tvMealType.setText(record.getMealType());
            binding.tvCalories.setText(String.format("%.0f 卡路里", record.getCalories()));
            binding.tvProtein.setText(String.format("蛋白质: %.1fg", record.getProtein()));
            binding.tvCarbs.setText(String.format("碳水: %.1fg", record.getCarbs()));
            binding.tvFat.setText(String.format("脂肪: %.1fg", record.getFat()));
            binding.tvTime.setText(record.getDate());
        }
    }
} 