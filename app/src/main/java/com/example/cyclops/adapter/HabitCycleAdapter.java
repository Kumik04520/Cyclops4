package com.example.cyclops.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.HabitCycleEngine;

import java.util.List;

public class HabitCycleAdapter extends RecyclerView.Adapter<HabitCycleAdapter.HabitViewHolder> {

    private List<HabitCycle> habitCycles;
    private OnHabitClickListener listener;

    public interface OnHabitClickListener {
        void onHabitClick(HabitCycle habitCycle);
        void onCompleteClick(HabitCycle habitCycle);
    }

    public HabitCycleAdapter(List<HabitCycle> habitCycles, OnHabitClickListener listener) {
        this.habitCycles = habitCycles;
        this.listener = listener;
    }

    public void updateData(List<HabitCycle> newHabitCycles) {
        this.habitCycles = newHabitCycles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit_cycle, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        HabitCycle habitCycle = habitCycles.get(position);
        holder.bind(habitCycle, listener);
    }

    @Override
    public int getItemCount() {
        return habitCycles != null ? habitCycles.size() : 0;
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        private TextView habitName;
        private TextView habitDescription;
        private TextView currentDay;
        private ProgressBar progressBar;
        private TextView progressText;
        private Button completeButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.tv_habit_name);
            habitDescription = itemView.findViewById(R.id.tv_habit_description);
            currentDay = itemView.findViewById(R.id.tv_current_day);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progressText = itemView.findViewById(R.id.tv_progress);
            completeButton = itemView.findViewById(R.id.btn_complete);
        }

        public void bind(HabitCycle habitCycle, OnHabitClickListener listener) {
            habitName.setText(habitCycle.getName());
            habitDescription.setText(habitCycle.getDescription());

            int currentDayNumber = HabitCycleEngine.calculateCurrentDay(habitCycle);

            // [修改] 使用 Context 获取资源字符串
            currentDay.setText(itemView.getContext().getString(R.string.day_format, currentDayNumber));

            int progress = (currentDayNumber * 100) / Math.max(1, habitCycle.getCycleLength());
            progressBar.setProgress(progress);

            // [修改] 使用 Context 获取资源字符串
            progressText.setText(itemView.getContext().getString(R.string.progress_format, progress));

            boolean isCompleted = HabitCycleEngine.isCompletedToday(habitCycle);

            if (isCompleted) {
                // [修改] 使用资源ID
                completeButton.setText(R.string.btn_completed_today);
                completeButton.setEnabled(false);
                completeButton.setAlpha(0.5f);
            } else {
                // [修改] 使用资源ID
                completeButton.setText(R.string.btn_check_in);
                completeButton.setEnabled(true);
                completeButton.setAlpha(1.0f);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onHabitClick(habitCycle);
            });

            completeButton.setOnClickListener(v -> {
                if (listener != null) listener.onCompleteClick(habitCycle);
            });
        }
    }
}