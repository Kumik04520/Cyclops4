package com.example.cyclops.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.model.DayTask;

import java.util.List;

public class DayTaskAdapter extends RecyclerView.Adapter<DayTaskAdapter.TaskViewHolder> {

    private List<DayTask> dayTasks;
    private OnTaskChangeListener onTaskChangeListener;

    public interface OnTaskChangeListener {
        void onTaskNameChanged(int position, String newTaskName);
        // 移除完成状态变化的监听
    }

    public DayTaskAdapter(List<DayTask> dayTasks, OnTaskChangeListener listener) {
        this.dayTasks = dayTasks;
        this.onTaskChangeListener = listener;
    }

    public void updateData(List<DayTask> dayTasks) {
        this.dayTasks = dayTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        DayTask task = dayTasks.get(position);
        holder.bind(task, position);
    }

    @Override
    public int getItemCount() {
        return dayTasks != null ? dayTasks.size() : 0;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDayNumber;
        private EditText etTaskDescription;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            etTaskDescription = itemView.findViewById(R.id.et_task_description);
        }

        public void bind(DayTask task, int position) {
            // 设置天数
            tvDayNumber.setText(String.valueOf(task.getDayNumber()));

            // 设置任务名称
            etTaskDescription.setText(task.getTaskName());

            // 任务描述变化监听
            etTaskDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String newTaskName = etTaskDescription.getText().toString();
                        if (onTaskChangeListener != null) {
                            onTaskChangeListener.onTaskNameChanged(position, newTaskName);
                        }
                    }
                }
            });
        }
    }
}