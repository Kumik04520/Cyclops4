package com.example.cyclops.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cyclops.database.entity.HabitCycleEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface HabitCycleDao {
    @Insert
    long insert(HabitCycleEntity habitCycle);  // 改为返回long以获取插入的ID

    @Update
    void update(HabitCycleEntity habitCycle);

    @Delete
    void delete(HabitCycleEntity habitCycle);

    @Query("SELECT * FROM habit_cycles WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<HabitCycleEntity>> getAllHabitCycles(String userId);

    @Query("SELECT * FROM habit_cycles WHERE userId = :userId ORDER BY createdAt DESC")
    List<HabitCycleEntity> getAllHabitCyclesSync(String userId);  // 同步版本

    @Query("SELECT * FROM habit_cycles WHERE id = :habitId")
    LiveData<HabitCycleEntity> getHabitCycleById(String habitId);

    @Query("SELECT * FROM habit_cycles WHERE id = :habitId")
    HabitCycleEntity getHabitCycleByIdSync(String habitId);  // 同步版本

    @Query("SELECT * FROM habit_cycles WHERE isPublic = 1 ORDER BY totalCompletions DESC LIMIT 10")
    LiveData<List<HabitCycleEntity>> getPopularHabitCycles();

    @Query("SELECT * FROM habit_cycles WHERE isPublic = 1 ORDER BY totalCompletions DESC LIMIT 10")
    List<HabitCycleEntity> getPopularHabitCyclesSync();  // 同步版本

    @Query("UPDATE habit_cycles SET totalCompletions = totalCompletions + 1, lastCompletionDate = :currentDate, updatedAt = :currentDate WHERE id = :habitId")
    void incrementCompletions(String habitId, Date currentDate);

    @Query("UPDATE habit_cycles SET currentStreak = :streak, updatedAt = :currentDate WHERE id = :habitId")
    void updateStreak(String habitId, int streak, Date currentDate);

    @Query("DELETE FROM habit_cycles WHERE id = :habitId")
    void deleteById(String habitId);  // 添加按ID删除的方法


}