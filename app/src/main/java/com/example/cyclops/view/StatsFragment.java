package com.example.cyclops.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cyclops.R;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel; // 1. 改用 HabitViewModel

import java.util.List;

public class StatsFragment extends Fragment {

    private HabitViewModel habitViewModel; // 2. 变量类型更改
    private TextView tvTotalHabits;
    private TextView tvTotalCompletions;
    private TextView tvBestStreak;
    private TextView tvSuccessRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 3. 获取 HabitViewModel 实例
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        tvTotalHabits = view.findViewById(R.id.tv_total_habits);
        tvTotalCompletions = view.findViewById(R.id.tv_total_completions);
        tvBestStreak = view.findViewById(R.id.tv_best_streak);
        tvSuccessRate = view.findViewById(R.id.tv_success_rate);
    }

    private void observeViewModel() {
        // 4. 调用正确的方法 getHabitsLiveData()
        habitViewModel.getHabitsLiveData().observe(getViewLifecycleOwner(), this::calculateAndShowStats);
    }

    // 统计核心逻辑 (保持不变)
    private void calculateAndShowStats(List<HabitCycle> habits) {
        if (habits == null) return;

        int totalHabitsCount = habits.size();
        int grandTotalCompletions = 0; // 所有习惯的任务完成总数
        int maxGlobalStreak = 0;       // 所有习惯中最高的连续记录

        for (HabitCycle habit : habits) {
            // 累加每个习惯的 totalCompletions (小任务完成数)
            grandTotalCompletions += habit.getTotalCompletions();

            // 寻找最大的 bestStreak
            if (habit.getBestStreak() > maxGlobalStreak) {
                maxGlobalStreak = habit.getBestStreak();
            }
        }

        // 计算成功率 (示例算法：为了展示效果，简单处理)
        // 实际项目可根据 (完成数 / 预期天数) 计算
        double successRate = totalHabitsCount > 0 ?
                (grandTotalCompletions > 0 ? 100.0 : 0) : 0;

        // 更新 UI
        tvTotalHabits.setText(String.valueOf(totalHabitsCount));
        tvTotalCompletions.setText(String.valueOf(grandTotalCompletions));
        tvBestStreak.setText(String.valueOf(maxGlobalStreak));
        tvSuccessRate.setText(String.format("%.1f%%", successRate));
    }
}