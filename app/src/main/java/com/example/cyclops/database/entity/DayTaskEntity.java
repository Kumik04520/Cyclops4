package com.example.cyclops.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "day_tasks",
        foreignKeys = @ForeignKey(
                entity = HabitCycleEntity.class,
                parentColumns = "id",
                childColumns = "habit_cycle_id",
                onDelete = ForeignKey.CASCADE // 关键：习惯删除时，自动级联删除对应的任务
        ),
        indices = {@Index("habit_cycle_id")} // 优化查询性能
)

public class DayTaskEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "habit_cycle_id")
    public String habitCycleId;

    public int dayNumber;
    public String taskName;
    public boolean completed;

    // 空构造函数 - Room需要
    public DayTaskEntity() {}

    // 业务构造函数
    @Ignore
    public DayTaskEntity(String habitCycleId, int dayNumber, String taskName) {
        this.habitCycleId = habitCycleId;
        this.dayNumber = dayNumber;
        this.taskName = taskName;
        this.completed = false;
    }
}