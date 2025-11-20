package com.example.cyclops.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.HabitCycleEngine;

// 移除 static 关键字
public class HabitViewHolder extends RecyclerView.ViewHolder {
    private TextView habitName;
    private TextView habitDescription;
    private TextView currentDay;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView tvStreak;
    private TextView tvCompletions;
    private Button completeButton;

    public HabitViewHolder(@NonNull View itemView) {
        super(itemView);
        habitName = itemView.findViewById(R.id.tv_habit_name);
        habitDescription = itemView.findViewById(R.id.tv_habit_description);
        currentDay = itemView.findViewById(R.id.tv_current_day);
        progressBar = itemView.findViewById(R.id.progress_bar);
        progressText = itemView.findViewById(R.id.tv_progress);
        tvStreak = itemView.findViewById(R.id.tv_streak);
        tvCompletions = itemView.findViewById(R.id.tv_completions);
        completeButton = itemView.findViewById(R.id.btn_complete);
    }

    public void bind(HabitCycle habitCycle, OnHabitClickListener listener) {
        habitName.setText(habitCycle.getName());
        habitDescription.setText(habitCycle.getDescription());

        int currentDayNumber = HabitCycleEngine.calculateCurrentDay(habitCycle);
        currentDay.setText("第 " + currentDayNumber + " 天");

        // 计算进度
        int progress = (currentDayNumber * 100) / habitCycle.getCycleLength();
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");

        // 设置统计信息
        tvStreak.setText(String.valueOf(habitCycle.getCurrentStreak()));
        tvCompletions.setText(String.valueOf(habitCycle.getTotalCompletions()));

        // 设置点击监听器
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitClick(habitCycle);
            }
        });

        completeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCompleteClick(habitCycle);
            }
        });
    }

    public interface OnHabitClickListener {
        void onHabitClick(HabitCycle habitCycle);
        void onCompleteClick(HabitCycle habitCycle);
    }
}