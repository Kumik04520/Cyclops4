package com.example.cyclops.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.RoomHabitRepository;
import com.example.cyclops.HabitCycleEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TodayViewModel extends AndroidViewModel {

    private HabitRepository habitRepository;
    private MutableLiveData<List<HabitCycle>> todayHabitsLiveData;
    private MutableLiveData<String> errorMessageLiveData;
    private MutableLiveData<Integer> completedCountLiveData;
    private MutableLiveData<Integer> totalCountLiveData;
    private androidx.lifecycle.Observer<List<HabitCycle>> habitsObserver;
    private Executor executor;

    public TodayViewModel(Application application) {
        super(application);
        this.habitRepository = RoomHabitRepository.getInstance(application);
        this.todayHabitsLiveData = new MutableLiveData<>();
        this.errorMessageLiveData = new MutableLiveData<>();
        this.completedCountLiveData = new MutableLiveData<>();
        this.totalCountLiveData = new MutableLiveData<>();
        this.executor = Executors.newSingleThreadExecutor();
        setupHabitsObserver();
        loadTodayHabits();
    }

    private void setupHabitsObserver() {
        habitsObserver = new androidx.lifecycle.Observer<List<HabitCycle>>() {
            @Override
            public void onChanged(List<HabitCycle> allHabits) {
                if (allHabits != null) {
                    processAndUpdateTodayHabits(allHabits);
                }
            }
        };
    }

    public LiveData<List<HabitCycle>> getTodayHabitsLiveData() {
        return todayHabitsLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<Integer> getCompletedCountLiveData() {
        return completedCountLiveData;
    }

    public LiveData<Integer> getTotalCountLiveData() {
        return totalCountLiveData;
    }

    public void loadTodayHabits() {
        try {
            LiveData<List<HabitCycle>> allHabitsLiveData = habitRepository.getAllHabitCycles();
            // 先移除旧的观察者避免重复
            allHabitsLiveData.removeObserver(habitsObserver);
            allHabitsLiveData.observeForever(habitsObserver);
        } catch (Exception e) {
            android.util.Log.e("TodayViewModel", "加载习惯失败: " + e.getMessage());
            errorMessageLiveData.setValue("加载习惯失败: " + e.getMessage());
        }
    }

    private void processAndUpdateTodayHabits(List<HabitCycle> allHabits) {
        List<HabitCycle> todayHabits = new ArrayList<>();
        int completedCount = 0;
        int totalCount = 0;

        for (HabitCycle habit : allHabits) {
            DayTask todayTask = HabitCycleEngine.getCurrentDayTask(habit);
            if (todayTask != null) {
                totalCount++; // 统计总任务数

                // 创建今日习惯的副本用于显示
                HabitCycle todayHabit = new HabitCycle();
                todayHabit.setId(habit.getId());
                todayHabit.setName(habit.getName());
                todayHabit.setDescription(habit.getDescription());
                todayHabit.setCycleLength(habit.getCycleLength());
                todayHabit.setStartDate(habit.getStartDate());

                // 设置今日任务
                List<DayTask> todayTasks = new ArrayList<>();
                todayTasks.add(todayTask);
                todayHabit.setDayTasks(todayTasks);

                todayHabit.setCurrentStreak(habit.getCurrentStreak());
                todayHabit.setTotalCompletions(habit.getTotalCompletions());
                todayHabits.add(todayHabit);

                if (todayTask.isCompleted()) {
                    completedCount++;
                }
            }
        }

        todayHabitsLiveData.setValue(todayHabits);
        completedCountLiveData.setValue(completedCount);
        totalCountLiveData.setValue(totalCount);

        android.util.Log.d("TodayViewModel", "更新今日习惯: " + todayHabits.size() + "个, 已完成: " + completedCount + "/" + totalCount);
    }

    public void completeTask(String habitId) {
        android.util.Log.d("TodayViewModel", "开始完成任务: " + habitId);

        // 直接更新UI提供即时反馈
        updateUIForCompletion(habitId);

        // 执行数据库操作
        executor.execute(() -> {
            try {
                // 从原始数据源获取习惯信息，而不是从当前显示列表
                LiveData<List<HabitCycle>> allHabitsLiveData = habitRepository.getAllHabitCycles();
                List<HabitCycle> allHabits = allHabitsLiveData.getValue();

                android.util.Log.d("TodayViewModel", "所有习惯列表大小: " + (allHabits != null ? allHabits.size() : 0));

                if (allHabits != null) {
                    boolean found = false;
                    for (HabitCycle habit : allHabits) {
                        if (habit.getId().equals(habitId)) {
                            found = true;
                            int currentDay = HabitCycleEngine.calculateCurrentDay(habit);
                            android.util.Log.d("TodayViewModel", "找到习惯，计算当前天数: " + currentDay);

                            // 完成任务
                            habitRepository.completeDay(habitId, currentDay);
                            android.util.Log.d("TodayViewModel", "已调用repository完成任务");
                            break;
                        }
                    }
                    if (!found) {
                        android.util.Log.e("TodayViewModel", "未在所有习惯列表中找到习惯: " + habitId);
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("TodayViewModel", "完成任务异常: " + e.getMessage(), e);
                // 如果失败，恢复UI状态
                errorMessageLiveData.postValue("完成任务失败: " + e.getMessage());
                // 重新加载数据恢复状态
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        loadTodayHabits();
                    }
                });
            }
        });
    }

    private void updateUIForCompletion(String completedHabitId) {
        List<HabitCycle> currentHabits = todayHabitsLiveData.getValue();
        if (currentHabits != null) {
            List<HabitCycle> updatedHabits = new ArrayList<>();
            for (HabitCycle habit : currentHabits) {
                if (!habit.getId().equals(completedHabitId)) {
                    updatedHabits.add(habit);
                }
            }
            todayHabitsLiveData.setValue(updatedHabits);

            // 更新完成计数 - 总数保持不变
            Integer currentCompleted = completedCountLiveData.getValue();
            if (currentCompleted != null) {
                completedCountLiveData.setValue(currentCompleted + 1);
            }

            android.util.Log.d("TodayViewModel", "UI更新完成: 移除了习惯 " + completedHabitId +
                    ", 剩余显示任务: " + updatedHabits.size() + ", 完成计数: " + (currentCompleted + 1));
        }
    }

    public void skipTask(String habitId) {
        loadTodayHabits();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理资源
        if (habitsObserver != null) {
            try {
                habitRepository.getAllHabitCycles().removeObserver(habitsObserver);
            } catch (Exception e) {
                android.util.Log.e("TodayViewModel", "移除观察者失败: " + e.getMessage());
            }
        }
    }
}