package com.example.cyclops.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.cyclops.HabitCycleEngine;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.RoomHabitRepository;
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
    private LiveData<List<HabitCycle>> allHabitsLiveData;
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
        habitsObserver = allHabits -> {
            if (allHabits != null) {
                processAndUpdateTodayHabits(allHabits);
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
            // Remove old observer if exists
            if (allHabitsLiveData != null && habitsObserver != null) {
                allHabitsLiveData.removeObserver(habitsObserver);
            }

            allHabitsLiveData = habitRepository.getAllHabitCycles();
            allHabitsLiveData.observeForever(habitsObserver);
        } catch (Exception e) {
            errorMessageLiveData.setValue("Failed to load habits: " + e.getMessage());
        }
    }

    private void processAndUpdateTodayHabits(List<HabitCycle> allHabits) {
        List<HabitCycle> todayHabits = new ArrayList<>();
        int completedCount = 0;
        int totalCount = 0;

        android.util.Log.d("TodayViewModel", "处理习惯列表，总数: " + (allHabits != null ? allHabits.size() : 0));

        for (HabitCycle habit : allHabits) {
            DayTask todayTask = HabitCycleEngine.getCurrentDayTask(habit);
            DayTask displayTask = HabitCycleEngine.getCurrentDayTaskForDisplay(habit);

            android.util.Log.d("TodayViewModel", "习惯: " + habit.getName() +
                ", 当前任务: " + (displayTask != null ? displayTask.getTaskName() : "null") +
                ", 完成状态: " + (displayTask != null ? displayTask.isCompleted() : "N/A"));

            // Only count habits that have tasks for today
            if (displayTask != null) {
                totalCount++;
                if (displayTask.isCompleted()) {
                    completedCount++;
                } else if (todayTask != null) {
                    // Only add uncompleted tasks to the list
                    todayHabits.add(habit);
                }
            }
        }

        android.util.Log.d("TodayViewModel", "待完成任务: " + todayHabits.size() + ", 已完成: " + completedCount + ", 总数: " + totalCount);

        todayHabitsLiveData.setValue(todayHabits);
        completedCountLiveData.setValue(completedCount);
        totalCountLiveData.setValue(totalCount);
    }

    public void completeTask(String habitId) {
        executor.execute(() -> {
            try {
                android.util.Log.d("TodayViewModel", "开始完成任务: " + habitId);

                // Use synchronous method to get habit from repository
                HabitCycle habitToComplete = habitRepository.getHabitCycleByIdSync(habitId);

                if (habitToComplete != null) {
                    android.util.Log.d("TodayViewModel", "找到习惯: " + habitToComplete.getName());
                    int currentDay = HabitCycleEngine.calculateCurrentDay(habitToComplete);
                    android.util.Log.d("TodayViewModel", "当前天数: " + currentDay);
                    habitRepository.completeDay(habitId, currentDay);
                    android.util.Log.d("TodayViewModel", "完成任务成功");
                } else {
                    android.util.Log.e("TodayViewModel", "Habit not found: " + habitId);
                    errorMessageLiveData.postValue("Habit not found: " + habitId);
                }
            } catch (Exception e) {
                android.util.Log.e("TodayViewModel", "Failed to complete task: " + e.getMessage(), e);
                errorMessageLiveData.postValue("Failed to complete task: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (allHabitsLiveData != null && habitsObserver != null) {
            try {
                allHabitsLiveData.removeObserver(habitsObserver);
            } catch (Exception e) {
                android.util.Log.e("TodayViewModel", "Error removing observer: " + e.getMessage());
            }
        }
    }
}