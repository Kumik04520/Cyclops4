package com.example.cyclops.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cyclops.R; // 确保引入R文件
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

    // [新增] 保存 Application 上下文以获取资源
    private final Application application;

    private static final String CURRENT_USER_ID = "";

    private RoomHabitRepository(Application application) {
        this.application = application; // 赋值
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
                habitCycle.setUserId(CURRENT_USER_ID);
                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.insert(entity);

                if (entity.dayTasks != null && !entity.dayTasks.isEmpty()) {
                    for (com.example.cyclops.database.entity.DayTaskEntity task : entity.dayTasks) {
                        task.habitCycleId = entity.id;
                    }
                    dayTaskDao.insertAll(entity.dayTasks);
                }
            } catch (Exception e) {
                // [修改] 使用 application.getString()
                String msg = application.getString(R.string.error_add_habit, e.getMessage());
                errorLiveData.postValue(msg);
            }
        });
    }

    @Override
    public void updateHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                habitCycle.setUserId(CURRENT_USER_ID);
                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.update(entity);
            } catch (Exception e) {
                // [修改]
                String msg = application.getString(R.string.error_update_habit, e.getMessage());
                errorLiveData.postValue(msg);
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
                // [修改]
                String msg = application.getString(R.string.error_delete_habit, e.getMessage());
                errorLiveData.postValue(msg);
            }
        });
    }

    @Override
    public LiveData<List<HabitCycle>> getAllHabitCycles() {
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

                    // 可以在 Log 中记录成功，但无需通知 UI 错误
                }
            } catch (Exception e) {
                // [修改]
                String msg = application.getString(R.string.error_complete_task, e.getMessage());
                errorLiveData.postValue(msg);
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