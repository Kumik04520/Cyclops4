package com.example.cyclops.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.example.cyclops.viewmodel.TodayViewModel;

public class TodayFragment extends Fragment {

    private TodayViewModel todayViewModel;
    private RecyclerView recyclerView;
    private HabitCycleAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvCompletedCount;
    private TextView tvTotalCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        initViews(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todayViewModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_today);
        progressBar = view.findViewById(R.id.progress_bar_today);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvTotalCount = view.findViewById(R.id.tv_total_count);
    }

    private void setupRecyclerView() {
        adapter = new HabitCycleAdapter(null, new HabitCycleAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(HabitCycle habitCycle) {
                openHabitDetail(habitCycle);
            }

            @Override
            public void onCompleteClick(HabitCycle habitCycle) {
                // 显示完成动画或反馈
                showCompletionFeedback(habitCycle);

                // 完成任务
                if (todayViewModel != null) {
                    todayViewModel.completeTask(habitCycle.getId());
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void showCompletionFeedback(HabitCycle habitCycle) {
        // 可以在这里添加完成动画
        Toast.makeText(getContext(), "已完成: " + habitCycle.getName(), Toast.LENGTH_SHORT).show();
    }

    private void observeViewModel() {
        // 观察今日习惯列表
        todayViewModel.getTodayHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                adapter.updateData(habits);
                android.util.Log.d("TodayFragment", "习惯列表更新: " + habits.size() + "个任务");
            }
        });

        // 观察完成计数
        todayViewModel.getCompletedCountLiveData().observe(getViewLifecycleOwner(), completedCount -> {
            if (completedCount != null) {
                tvCompletedCount.setText(String.valueOf(completedCount));
                updateProgress();
                android.util.Log.d("TodayFragment", "完成计数更新: " + completedCount);
            }
        });

        // 观察总任务数
        todayViewModel.getTotalCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            if (totalCount != null) {
                tvTotalCount.setText(String.valueOf(totalCount));
                updateProgress();
                android.util.Log.d("TodayFragment", "总任务数更新: " + totalCount);
            }
        });

        // 观察错误信息
        todayViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                android.util.Log.e("TodayFragment", "错误信息: " + errorMessage);
            }
        });
    }

    private void updateProgress() {
        Integer totalCount = todayViewModel.getTotalCountLiveData().getValue();
        Integer completedCount = todayViewModel.getCompletedCountLiveData().getValue();

        if (totalCount != null && completedCount != null) {
            if (totalCount > 0) {
                int progress = (completedCount * 100) / totalCount;
                progressBar.setProgress(progress);
                android.util.Log.d("TodayFragment", "更新进度: " + completedCount + "/" + totalCount + " = " + progress + "%");
            } else {
                progressBar.setProgress(0);
                android.util.Log.d("TodayFragment", "更新进度: 0/0 = 0%");
            }
        } else {
            progressBar.setProgress(0);
            android.util.Log.d("TodayFragment", "更新进度: 数据为空");
        }
    }

    private void openHabitDetail(HabitCycle habitCycle) {
        // 打开习惯详情
        Intent intent = new Intent(getContext(), HabitDetailActivity.class);
        intent.putExtra("HABIT_ID", habitCycle.getId());
        startActivity(intent);
    }
}