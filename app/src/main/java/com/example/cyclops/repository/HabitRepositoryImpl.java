package com.example.cyclops.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cyclops.model.DayTask;
import com.example.cyclops.model.HabitCycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitRepositoryImpl implements HabitRepository {
    private static HabitRepositoryImpl instance;
    private Map<String, HabitCycle> habitsMap;
    private MutableLiveData<List<HabitCycle>> habitsLiveData;
    private MutableLiveData<List<HabitCycle>> popularHabitsLiveData;

    private HabitRepositoryImpl() {
        habitsMap = new HashMap<>();
        habitsLiveData = new MutableLiveData<>();
        popularHabitsLiveData = new MutableLiveData<>();

        // 添加一些示例数据用于测试
        initializeSampleData();
        updateLiveData();
    }

    public static synchronized HabitRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new HabitRepositoryImpl();
        }
        return instance;
    }

    private void initializeSampleData() {
        // 创建示例健身习惯循环
        HabitCycle fitnessCycle = new HabitCycle();
        fitnessCycle.setName("健身计划");
        fitnessCycle.setDescription("3天循环健身计划");
        fitnessCycle.setCycleLength(3);
        fitnessCycle.updateDayTask(0, "胸部训练");
        fitnessCycle.updateDayTask(1, "背部训练");
        fitnessCycle.updateDayTask(2, "腿部训练");

        habitsMap.put(fitnessCycle.getId(), fitnessCycle);

        // 创建示例学习习惯循环
        HabitCycle studyCycle = new HabitCycle();
        studyCycle.setName("语言学习");
        studyCycle.setDescription("每日语言学习计划");
        studyCycle.setCycleLength(4);
        studyCycle.updateDayTask(0, "词汇学习");
        studyCycle.updateDayTask(1, "语法练习");
        studyCycle.updateDayTask(2, "听力训练");
        studyCycle.updateDayTask(3, "口语练习");

        habitsMap.put(studyCycle.getId(), studyCycle);
    }

    private void updateLiveData() {
        // 更新所有习惯的LiveData
        habitsLiveData.setValue(new ArrayList<>(habitsMap.values()));

        // 更新热门习惯的LiveData（按完成次数排序）
        List<HabitCycle> popularHabits = new ArrayList<>(habitsMap.values());
        popularHabits.sort((h1, h2) -> Integer.compare(h2.getTotalCompletions(), h1.getTotalCompletions()));

        // 只取前10个
        if (popularHabits.size() > 10) {
            popularHabits = popularHabits.subList(0, 10);
        }
        popularHabitsLiveData.setValue(popularHabits);
    }

    @Override
    public void addHabitCycle(HabitCycle habitCycle) {
        habitsMap.put(habitCycle.getId(), habitCycle);
        updateLiveData();
    }

    @Override
    public void updateHabitCycle(HabitCycle habitCycle) {
        habitsMap.put(habitCycle.getId(), habitCycle);
        updateLiveData();
    }

    @Override
    public void deleteHabitCycle(String habitId) {
        habitsMap.remove(habitId);
        updateLiveData();
    }

    @Override
    public LiveData<List<HabitCycle>> getAllHabitCycles() {
        return habitsLiveData;
    }

    @Override
    public LiveData<HabitCycle> getHabitCycleById(String habitId) {
        MutableLiveData<HabitCycle> habitLiveData = new MutableLiveData<>();
        HabitCycle habit = habitsMap.get(habitId);
        habitLiveData.setValue(habit);
        return habitLiveData;
    }

    @Override
    public void completeDay(String habitId, int dayNumber) {
        HabitCycle habit = habitsMap.get(habitId);
        if (habit != null && habit.getDayTasks() != null) {
            for (DayTask task : habit.getDayTasks()) {
                if (task.getDayNumber() == dayNumber) {
                    task.setCompleted(true);
                    habit.setTotalCompletions(habit.getTotalCompletions() + 1);
                    break;
                }
            }
            updateLiveData();
        }
    }

    @Override
    public LiveData<List<HabitCycle>> getPopularHabitCycles() {
        return popularHabitsLiveData;
    }

    @Override
    public HabitCycle getHabitCycleByIdSync(String habitId) {
        // 因为数据就在内存 Map 中，直接获取即可，不需要异步操作
        return habitsMap.get(habitId);
    }
}

