package com.example.cyclops.database.entity.mapper;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.database.entity.HabitCycleEntity;
import com.example.cyclops.database.entity.DayTaskEntity;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static HabitCycle toHabitCycle(HabitCycleEntity entity) {
        if (entity == null) return null;

        HabitCycle habitCycle = new HabitCycle();
        habitCycle.setId(entity.id);
        habitCycle.setName(entity.name);
        habitCycle.setDescription(entity.description);
        habitCycle.setCycleLength(entity.cycleLength);
        habitCycle.setUserId(entity.userId);

        if (entity.startDate != null) {
            habitCycle.setStartDate(entity.startDate.getTime());
        }

        habitCycle.setPublic(entity.isPublic);
        habitCycle.setCurrentStreak(entity.currentStreak);
        habitCycle.setBestStreak(entity.bestStreak);
        habitCycle.setTotalCompletions(entity.totalCompletions);

        // [新增] 将数据库的时间传给 Model
        if (entity.lastCompletionDate != null) {
            habitCycle.setLastCompletionDate(entity.lastCompletionDate.getTime());
        }

        if (entity.dayTasks != null) {
            List<DayTask> dayTasks = new ArrayList<>();
            for (DayTaskEntity taskEntity : entity.dayTasks) {
                dayTasks.add(toDayTask(taskEntity));
            }
            habitCycle.setDayTasks(dayTasks);
        }

        return habitCycle;
    }

    public static HabitCycleEntity toHabitCycleEntity(HabitCycle habitCycle) {
        if (habitCycle == null) return null;

        HabitCycleEntity entity = new HabitCycleEntity();
        entity.id = habitCycle.getId();
        entity.name = habitCycle.getName();
        entity.description = habitCycle.getDescription();
        entity.cycleLength = habitCycle.getCycleLength();
        entity.userId = habitCycle.getUserId();
        entity.startDate = new java.util.Date(habitCycle.getStartDate());
        entity.isPublic = habitCycle.isPublic();
        entity.currentStreak = habitCycle.getCurrentStreak();
        entity.bestStreak = habitCycle.getBestStreak();
        entity.totalCompletions = habitCycle.getTotalCompletions();
        entity.createdAt = new java.util.Date();
        entity.updatedAt = new java.util.Date();

        // [新增] 将 Model 的时间存入数据库
        if (habitCycle.getLastCompletionDate() > 0) {
            entity.lastCompletionDate = new java.util.Date(habitCycle.getLastCompletionDate());
        }

        if (habitCycle.getDayTasks() != null) {
            List<DayTaskEntity> dayTaskEntities = new ArrayList<>();
            for (DayTask dayTask : habitCycle.getDayTasks()) {
                dayTaskEntities.add(toDayTaskEntity(dayTask, habitCycle.getId()));
            }
            entity.dayTasks = dayTaskEntities;
        }

        return entity;
    }

    public static DayTask toDayTask(DayTaskEntity entity) {
        if (entity == null) return null;
        DayTask dayTask = new DayTask(entity.dayNumber, entity.taskName);
        dayTask.setCompleted(entity.completed);
        return dayTask;
    }

    public static DayTaskEntity toDayTaskEntity(DayTask dayTask, String habitCycleId) {
        if (dayTask == null) return null;
        DayTaskEntity entity = new DayTaskEntity();
        entity.habitCycleId = habitCycleId;
        entity.dayNumber = dayTask.getDayNumber();
        entity.taskName = dayTask.getTaskName();
        entity.completed = dayTask.isCompleted();
        return entity;
    }

    public static List<HabitCycle> toHabitCycleList(List<HabitCycleEntity> entities) {
        List<HabitCycle> habitCycles = new ArrayList<>();
        if (entities != null) {
            for (HabitCycleEntity entity : entities) {
                habitCycles.add(toHabitCycle(entity));
            }
        }
        return habitCycles;
    }
}