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

    // 临时用户ID，后续集成认证系统时替换
    private static final String TEMP_USER_ID = "temp_user_001";

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

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    @Override
    public void addHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                // 设置用户ID
                habitCycle.setUserId(TEMP_USER_ID);

                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                long id = habitCycleDao.insert(entity);

                // 插入每日任务
                if (entity.dayTasks != null && !entity.dayTasks.isEmpty()) {
                    dayTaskDao.insertAll(entity.dayTasks);
                }

                android.util.Log.d("RoomHabitRepository", "添加习惯成功: " + habitCycle.getName());
            } catch (Exception e) {
                android.util.Log.e("RoomHabitRepository", "添加习惯失败: " + e.getMessage(), e);
                errorLiveData.postValue("添加习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.update(entity);

                // 更新每日任务 - 先删除旧的，再插入新的
                dayTaskDao.deleteByHabitCycleId(habitCycle.getId());
                if (entity.dayTasks != null && !entity.dayTasks.isEmpty()) {
                    dayTaskDao.insertAll(entity.dayTasks);
                }

                android.util.Log.d("RoomHabitRepository", "更新习惯成功: " + habitCycle.getName());
            } catch (Exception e) {
                android.util.Log.e("RoomHabitRepository", "更新习惯失败: " + e.getMessage(), e);
                errorLiveData.postValue("更新习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public void deleteHabitCycle(String habitId) {
        executor.execute(() -> {
            try {
                // 先删除关联的每日任务
                dayTaskDao.deleteByHabitCycleId(habitId);

                // 然后删除习惯循环
                habitCycleDao.deleteById(habitId);

                android.util.Log.d("RoomHabitRepository", "删除习惯成功: " + habitId);
            } catch (Exception e) {
                android.util.Log.e("RoomHabitRepository", "删除习惯失败: " + e.getMessage(), e);
                errorLiveData.postValue("删除习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public LiveData<List<HabitCycle>> getAllHabitCycles() {
        return Transformations.map(
                habitCycleDao.getAllHabitCycles(TEMP_USER_ID),
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

    @Override
    public void completeDay(String habitId, int dayNumber) {
        executor.execute(() -> {
            try {
                android.util.Log.d("RoomHabitRepository", "开始完成任务: " + habitId + ", 天数: " + dayNumber);

                // 使用同步方法获取习惯信息
                HabitCycleEntity entity = habitCycleDao.getHabitCycleByIdSync(habitId);
                if (entity != null) {
                    android.util.Log.d("RoomHabitRepository", "找到习惯: " + entity.name + ", 当前连续天数: " + entity.currentStreak + ", 总完成次数: " + entity.totalCompletions);

                    // 更新任务完成状态
                    dayTaskDao.updateDayTaskCompletion(habitId, dayNumber, true);
                    android.util.Log.d("RoomHabitRepository", "更新任务完成状态完成");

                    // 增加完成次数
                    habitCycleDao.incrementCompletions(habitId, new java.util.Date());
                    android.util.Log.d("RoomHabitRepository", "增加完成次数完成");

                    // 更新连续天数
                    int newStreak = entity.currentStreak + 1;
                    habitCycleDao.updateStreak(habitId, newStreak, new java.util.Date());
                    android.util.Log.d("RoomHabitRepository", "更新连续天数: " + newStreak);

                    android.util.Log.d("RoomHabitRepository", "完成任务成功: " + entity.name + " 第" + dayNumber + "天, 新连续天数: " + newStreak);

                    // 强制刷新数据
                    loadAllHabitsFromDatabase();
                } else {
                    android.util.Log.e("RoomHabitRepository", "未找到习惯: " + habitId);
                    errorLiveData.postValue("未找到习惯: " + habitId);
                }
            } catch (Exception e) {
                android.util.Log.e("RoomHabitRepository", "完成任务失败: " + e.getMessage(), e);
                errorLiveData.postValue("完成任务失败: " + e.getMessage());
            }
        });
    }

    private void loadAllHabitsFromDatabase() {
        // 强制重新加载所有习惯数据
        LiveData<List<HabitCycleEntity>> habitsLiveData = habitCycleDao.getAllHabitCycles(TEMP_USER_ID);
        habitsLiveData.observeForever(new androidx.lifecycle.Observer<List<HabitCycleEntity>>() {
            @Override
            public void onChanged(List<HabitCycleEntity> habits) {
                // 数据已更新，移除观察者
                habitsLiveData.removeObserver(this);
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