package com.example.cyclops.repository;

import androidx.lifecycle.LiveData;

import com.example.cyclops.model.HabitCycle;

import java.util.List;

public interface HabitRepository {
    void addHabitCycle(HabitCycle habitCycle);
    void updateHabitCycle(HabitCycle habitCycle);
    void deleteHabitCycle(String habitId);
    LiveData<List<HabitCycle>> getAllHabitCycles();
    LiveData<HabitCycle> getHabitCycleById(String habitId);
    void completeDay(String habitId, int dayNumber);
    LiveData<List<HabitCycle>> getPopularHabitCycles();
    HabitCycle getHabitCycleByIdSync(String habitId);
}