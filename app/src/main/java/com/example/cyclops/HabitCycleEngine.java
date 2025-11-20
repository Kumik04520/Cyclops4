package com.example.cyclops;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.DayTask;

public class HabitCycleEngine {

    /**
     * 计算当前应该显示的习惯循环中的第几天
     */
    public static int calculateCurrentDay(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getStartDate() == 0) {
            return 1;
        }

        long startTime = habitCycle.getStartDate();
        long currentTime = System.currentTimeMillis();

        // 计算从开始日期到现在经过的天数
        long diff = currentTime - startTime;
        long daysPassed = diff / (24 * 60 * 60 * 1000);

        // 使用模运算确定当前在循环中的位置
        // 确保天数在 1 到 cycleLength 之间
        int currentDay = (int) (daysPassed % habitCycle.getCycleLength()) + 1;

        return Math.max(1, Math.min(habitCycle.getCycleLength(), currentDay));
    }

    /**
     * 获取当前天的任务（只返回未完成的任务）
     */
    public static DayTask getCurrentDayTask(HabitCycle habitCycle) {
        int currentDay = calculateCurrentDay(habitCycle);
        if (habitCycle.getDayTasks() != null && !habitCycle.getDayTasks().isEmpty()) {
            // 确保天数在有效范围内（0-based index）
            int actualDayIndex = Math.min(currentDay - 1, habitCycle.getDayTasks().size() - 1);
            DayTask task = habitCycle.getDayTasks().get(actualDayIndex);

            // 重要：只返回未完成的任务
            if (task != null && !task.isCompleted()) {
                return task;
            }
        }
        return null;
    }

    public static DayTask getCurrentDayTaskForDisplay(HabitCycle habitCycle) {
        int currentDay = calculateCurrentDay(habitCycle);
        if (habitCycle.getDayTasks() != null && !habitCycle.getDayTasks().isEmpty()) {
            int actualDayIndex = Math.min(currentDay - 1, habitCycle.getDayTasks().size() - 1);
            return habitCycle.getDayTasks().get(actualDayIndex);
        }
        return null;
    }

    /**
     * 检查是否是新的一天（用于重置完成状态）
     */
    public static boolean isNewDay(HabitCycle habitCycle, long lastCompletionTime) {
        if (lastCompletionTime == 0) return true;

        java.util.Calendar lastCal = java.util.Calendar.getInstance();
        lastCal.setTimeInMillis(lastCompletionTime);

        java.util.Calendar currentCal = java.util.Calendar.getInstance();

        return lastCal.get(java.util.Calendar.YEAR) != currentCal.get(java.util.Calendar.YEAR) ||
                lastCal.get(java.util.Calendar.MONTH) != currentCal.get(java.util.Calendar.MONTH) ||
                lastCal.get(java.util.Calendar.DAY_OF_MONTH) != currentCal.get(java.util.Calendar.DAY_OF_MONTH);
    }
}