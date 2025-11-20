package com.example.cyclops;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.DayTask;

import java.util.Calendar;

public class HabitCycleEngine {

    /**
     * 计算当前应该是第几天
     */
    public static int calculateCurrentDay(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getStartDate() == 0) {
            return 1;
        }
        // 修复后的日期计算逻辑：基于日历天数差异，而非毫秒差
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(habitCycle.getStartDate());
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(System.currentTimeMillis());
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        long diff = currentCal.getTimeInMillis() - startCal.getTimeInMillis();
        long daysPassed = diff / (24 * 60 * 60 * 1000);

        int currentDay = (int) (daysPassed % habitCycle.getCycleLength()) + 1;
        return Math.max(1, Math.min(habitCycle.getCycleLength(), currentDay));
    }

    /**
     * [新增] 检查该习惯今天是否已经完成（用于控制按钮状态）
     */
    public static boolean isCompletedToday(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getLastCompletionDate() == 0) {
            return false;
        }

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(habitCycle.getLastCompletionDate());

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(System.currentTimeMillis());

        // 比较 年 和 日 是否相同
        return lastCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                lastCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取当前天的任务对象（用于逻辑处理，如获取ID等）
     */
    public static DayTask getCurrentDayTask(HabitCycle habitCycle) {
        int currentDay = calculateCurrentDay(habitCycle);
        if (habitCycle.getDayTasks() != null && !habitCycle.getDayTasks().isEmpty()) {
            int actualDayIndex = Math.min(currentDay - 1, habitCycle.getDayTasks().size() - 1);
            return habitCycle.getDayTasks().get(actualDayIndex);
        }
        return null;
    }

    /**
     * [恢复缺失的方法] 获取当前应该显示的任务（用于 UI 展示，TodayViewModel 需要此方法）
     */
    public static DayTask getCurrentDayTaskForDisplay(HabitCycle habitCycle) {
        int currentDay = calculateCurrentDay(habitCycle);
        if (habitCycle.getDayTasks() != null && !habitCycle.getDayTasks().isEmpty()) {
            // 确保索引不越界
            int actualDayIndex = Math.min(currentDay - 1, habitCycle.getDayTasks().size() - 1);
            return habitCycle.getDayTasks().get(actualDayIndex);
        }
        return null;
    }
}