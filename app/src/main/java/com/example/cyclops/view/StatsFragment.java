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
import com.example.cyclops.viewmodel.HabitViewModel;

import java.util.List;

public class StatsFragment extends Fragment {

    private HabitViewModel habitViewModel;
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
        habitViewModel.getHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                updateStats(habits);
            }
        });
    }

    private void updateStats(List<HabitCycle> habits) {
        int totalHabits = habits.size();
        int totalCompletions = 0;
        int bestStreak = 0;
        int totalPossibleCompletions = totalHabits * 30;

        for (HabitCycle habit : habits) {
            totalCompletions += habit.getTotalCompletions();
            if (habit.getCurrentStreak() > bestStreak) {
                bestStreak = habit.getCurrentStreak();
            }
        }

        double successRate = totalPossibleCompletions > 0 ?
                (double) totalCompletions / totalPossibleCompletions * 100 : 0;

        tvTotalHabits.setText(String.valueOf(totalHabits));
        tvTotalCompletions.setText(String.valueOf(totalCompletions));
        tvBestStreak.setText(String.valueOf(bestStreak));
        tvSuccessRate.setText(String.format("%.1f%%", successRate));
    }
}