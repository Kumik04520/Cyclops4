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
    private int totalCompletions;

    public HabitCycle() {
        this.id = UUID.randomUUID().toString();
        this.startDate = System.currentTimeMillis(); // 设置开始时间为当前时间
        this.currentStreak = 0;
        this.totalCompletions = 0;
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
    public void setCycleLength(int cycleLength) {
        this.cycleLength = cycleLength;
    }

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

    public int getTotalCompletions() { return totalCompletions; }
    public void setTotalCompletions(int totalCompletions) { this.totalCompletions = totalCompletions; }

    public void updateDayTask(int dayIndex, String taskName) {
        if (dayTasks != null && dayIndex >= 0 && dayIndex < dayTasks.size()) {
            dayTasks.get(dayIndex).setTaskName(taskName);
        }
    }
}