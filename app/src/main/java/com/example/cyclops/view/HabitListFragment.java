package com.example.cyclops.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.adapter.HabitCycleAdapter;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel;

public class HabitListFragment extends Fragment {

    private HabitViewModel habitViewModel;
    private RecyclerView recyclerView;
    private HabitCycleAdapter adapter;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_list, container, false);
        initViews(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次进入页面时刷新数据
        if (habitViewModel != null) {
            habitViewModel.loadAllHabits();
        }
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_habits);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }

    private void setupRecyclerView() {
        adapter = new HabitCycleAdapter(null, new HabitCycleAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(HabitCycle habitCycle) {
                // 打开习惯详情
                openHabitDetail(habitCycle);
            }

            @Override
            public void onCompleteClick(HabitCycle habitCycle) {
                // 完成当前任务
                int currentDay = habitViewModel.getCurrentDayForHabit(habitCycle);
                habitViewModel.completeDay(habitCycle.getId(), currentDay);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        habitViewModel.getHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                adapter.updateData(habits);
                android.util.Log.d("HabitFragment", "习惯列表更新，数量: " + habits.size());

                // 打印每个习惯的状态用于调试
                for (HabitCycle habit : habits) {
                    android.util.Log.d("HabitFragment", "习惯: " + habit.getName() +
                            ", 连续天数: " + habit.getCurrentStreak() +
                            ", 完成次数: " + habit.getTotalCompletions());
                }
            }
        });

        habitViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void openHabitDetail(HabitCycle habitCycle) {
        // 打开习惯详情页面的逻辑
        Intent intent = new Intent(getContext(), HabitDetailActivity.class);
        intent.putExtra("HABIT_ID", habitCycle.getId());
        startActivity(intent);
    }
}