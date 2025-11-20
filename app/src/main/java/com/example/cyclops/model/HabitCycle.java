package com.example.cyclops.model;

import java.util.List;
import java.util.UUID;

public class HabitCycle {
    private String id;
    private String name;
    private String description;
    private int cycleLength;
    private List<DayTask> dayTasks;
    private String userId;
    private long startDate;
    private boolean isPublic;
    private int currentStreak;
    private int bestStreak;
    private int totalCompletions;

    // [新增] 记录最后一次打卡的时间戳
    private long lastCompletionDate;

    public HabitCycle() {
        this.id = UUID.randomUUID().toString();
        this.startDate = System.currentTimeMillis();
        this.currentStreak = 0;
        this.bestStreak = 0;
        this.totalCompletions = 0;
        this.userId = "";
        this.isPublic = false;
        this.lastCompletionDate = 0; // 初始化
    }

    public HabitCycle(String name, String description, int cycleLength, List<DayTask> dayTasks) {
        this();
        this.name = name;
        this.description = description;
        this.cycleLength = cycleLength;
        this.dayTasks = dayTasks;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCycleLength() { return cycleLength; }
    public void setCycleLength(int cycleLength) { this.cycleLength = cycleLength; }

    public List<DayTask> getDayTasks() { return dayTasks; }
    public void setDayTasks(List<DayTask> dayTasks) { this.dayTasks = dayTasks; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getBestStreak() { return bestStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }

    public int getTotalCompletions() { return totalCompletions; }
    public void setTotalCompletions(int totalCompletions) { this.totalCompletions = totalCompletions; }

    // [新增] Getter 和 Setter
    public long getLastCompletionDate() { return lastCompletionDate; }
    public void setLastCompletionDate(long lastCompletionDate) { this.lastCompletionDate = lastCompletionDate; }

    public void updateDayTask(int dayIndex, String taskName) {
        if (dayTasks != null && dayIndex >= 0 && dayIndex < dayTasks.size()) {
            dayTasks.get(dayIndex).setTaskName(taskName);
        }
    }
}