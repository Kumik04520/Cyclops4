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
// 确保引用独立的 ViewHolder（如果之前分离了文件）
// 或者使用内部类，这里假设使用内部类或已合并

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

    // 内部 ViewHolder 类
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
            currentDay.setText("第 " + currentDayNumber + " 天");

            int progress = (currentDayNumber * 100) / Math.max(1, habitCycle.getCycleLength());
            progressBar.setProgress(progress);
            progressText.setText(progress + "%");

            // [核心修改] 检查今天是否已完成，控制按钮状态
            boolean isCompleted = HabitCycleEngine.isCompletedToday(habitCycle);

            if (isCompleted) {
                completeButton.setText("今日已完成");
                completeButton.setEnabled(false); // 禁止点击
                completeButton.setAlpha(0.5f);    // 变灰
            } else {
                completeButton.setText("打卡");
                completeButton.setEnabled(true);  // 允许点击
                completeButton.setAlpha(1.0f);    // 恢复正常
            }

            // 点击整个条目进入详情
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onHabitClick(habitCycle);
            });

            // 点击完成按钮（如果 enabled=true）
            completeButton.setOnClickListener(v -> {
                if (listener != null) listener.onCompleteClick(habitCycle);
            });
        }
    }
}