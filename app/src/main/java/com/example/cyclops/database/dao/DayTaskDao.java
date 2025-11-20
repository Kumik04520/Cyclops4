package com.example.cyclops.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cyclops.database.entity.DayTaskEntity;

import java.util.List;

@Dao
public interface DayTaskDao {
    @Insert
    void insert(DayTaskEntity dayTask);

    @Insert
    void insertAll(List<DayTaskEntity> dayTasks);

    @Update
    void update(DayTaskEntity dayTask);

    @Query("SELECT * FROM day_tasks WHERE habit_cycle_id = :habitCycleId ORDER BY dayNumber")
    List<DayTaskEntity> getDayTasksByHabitCycle(String habitCycleId);

    @Query("UPDATE day_tasks SET completed = :completed WHERE habit_cycle_id = :habitCycleId AND dayNumber = :dayNumber")
    void updateDayTaskCompletion(String habitCycleId, int dayNumber, boolean completed);

    @Query("DELETE FROM day_tasks WHERE habit_cycle_id = :habitCycleId")
    void deleteByHabitCycleId(String habitCycleId);

}