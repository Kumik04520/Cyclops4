package com.example.cyclops.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cyclops.database.AppDatabase;
import com.example.cyclops.database.dao.DayTaskDao;
import com.example.cyclops.database.dao.HabitCycleDao;
import com.example.cyclops.database.entity.HabitCycleEntity;
import com.example.cyclops.database.entity.mapper.Mapper;
import com.example.cyclops.model.HabitCycle;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RoomHabitRepository implements HabitRepository {

    private static RoomHabitRepository instance;
    private final HabitCycleDao habitCycleDao;
    private final DayTaskDao dayTaskDao;
    private final Executor executor;
    private final MutableLiveData<String> errorLiveData;

    // 【关键修复】定义统一的用户 ID 常量 (空字符串)，确保插入和查询一致
    private static final String CURRENT_USER_ID = "";

    private RoomHabitRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.habitCycleDao = database.habitCycleDao();
        this.dayTaskDao = database.dayTaskDao();
        this.executor = Executors.newSingleThreadExecutor();
        this.errorLiveData = new MutableLiveData<>();
    }

    public static synchronized RoomHabitRepository getInstance(Application application) {
        if (instance == null) {
            instance = new RoomHabitRepository(application);
        }
        return instance;
    }

    @Override
    public void addHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                // 【关键修复 1】强制设置 userId，避免 null
                habitCycle.setUserId(CURRENT_USER_ID);

                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.insert(entity);

                // 确保子任务关联正确
                if (entity.dayTasks != null && !entity.dayTasks.isEmpty()) {
                    // 【关键修复 2】确保每个 DayTaskEntity 都有正确的 Habit ID
                    for (com.example.cyclops.database.entity.DayTaskEntity task : entity.dayTasks) {
                        task.habitCycleId = entity.id;
                    }
                    dayTaskDao.insertAll(entity.dayTasks);
                }
            } catch (Exception e) {
                errorLiveData.postValue("添加习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                habitCycle.setUserId(CURRENT_USER_ID); // 确保更新时也不丢失 UserID
                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.update(entity);

                // 如果任务有变动，这里简单处理可能需要更复杂的逻辑
                // 但通常 update 只更新 habit 信息，任务详情在 Detail 页单独处理
            } catch (Exception e) {
                errorLiveData.postValue("更新习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public void deleteHabitCycle(String habitId) {
        executor.execute(() -> {
            try {
                HabitCycleEntity entity = new HabitCycleEntity();
                entity.id = habitId;
                habitCycleDao.delete(entity);
            } catch (Exception e) {
                errorLiveData.postValue("删除习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public LiveData<List<HabitCycle>> getAllHabitCycles() {
        // 【关键修复 3】查询时使用相同的 ID 常量
        return Transformations.map(
                habitCycleDao.getAllHabitCycles(CURRENT_USER_ID),
                Mapper::toHabitCycleList
        );
    }

    @Override
    public LiveData<HabitCycle> getHabitCycleById(String habitId) {
        return Transformations.map(
                habitCycleDao.getHabitCycleById(habitId),
                Mapper::toHabitCycle
        );
    }

    // 同步方法实现
    @Override
    public HabitCycle getHabitCycleByIdSync(String habitId) {
        HabitCycleEntity entity = habitCycleDao.getHabitCycleByIdSync(habitId);
        return Mapper.toHabitCycle(entity);
    }

    @Override
    public void completeDay(String habitId, int dayNumber) {
        executor.execute(() -> {
            try {
                HabitCycleEntity entity = habitCycleDao.getHabitCycleByIdSync(habitId);

                if (entity != null) {
                    entity.currentStreak = entity.currentStreak + 1;

                    if (entity.currentStreak > entity.bestStreak) {
                        entity.bestStreak = entity.currentStreak;
                    }

                    entity.totalCompletions = entity.totalCompletions + 1;
                    entity.lastCompletionDate = new java.util.Date();
                    entity.updatedAt = new java.util.Date();

                    habitCycleDao.update(entity);
                    dayTaskDao.updateDayTaskCompletion(habitId, dayNumber, true);

                    android.util.Log.d("Repository", "打卡成功: " + entity.name);
                }
            } catch (Exception e) {
                errorLiveData.postValue("完成任务失败: " + e.getMessage());
            }
        });
    }

    @Override
    public LiveData<List<HabitCycle>> getPopularHabitCycles() {
        return Transformations.map(
                habitCycleDao.getPopularHabitCycles(),
                Mapper::toHabitCycleList
        );
    }
}