package com.example.cyclops.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.adapter.DayTaskAdapter;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel;

import java.util.ArrayList;
import java.util.List;

public class HabitDetailActivity extends AppCompatActivity {

    private HabitViewModel habitViewModel;
    private TextView tvHabitName;
    private TextView tvHabitDescription;
    private TextView tvCycleLength;
    private TextView tvCurrentStreak;
    private TextView tvTotalCompletions;
    private RecyclerView recyclerViewTasks;
    private Button btnDelete;
    private Button btnBack;

    private DayTaskAdapter taskAdapter;
    private String habitId;
    private List<DayTask> currentTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_detail);

        habitId = getIntent().getStringExtra("HABIT_ID");
        if (habitId == null) {
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
    }

    private void initViews() {
        tvHabitName = findViewById(R.id.tv_habit_name);
        tvHabitDescription = findViewById(R.id.tv_habit_description);
        tvCycleLength = findViewById(R.id.tv_cycle_length);
        tvCurrentStreak = findViewById(R.id.tv_current_streak);
        tvTotalCompletions = findViewById(R.id.tv_total_completions);
        recyclerViewTasks = findViewById(R.id.recycler_view_tasks); // 现在从布局中获取
        btnDelete = findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupRecyclerView() {
        currentTasks = new ArrayList<>();
        taskAdapter = new DayTaskAdapter(currentTasks, new DayTaskAdapter.OnTaskChangeListener() {
            @Override
            public void onTaskNameChanged(int position, String newTaskName) { // 修改方法名
                // 更新任务名称
                if (position < currentTasks.size()) {
                    currentTasks.get(position).setTaskName(newTaskName); // 使用 setTaskName
                    saveTasks();
                }
            }


        });

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);
    }

    private void setupViewModel() {
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        habitViewModel.getSelectedHabitLiveData().observe(this, habit -> {
            if (habit != null) {
                updateUI(habit);
            }
        });

        habitViewModel.selectHabitCycle(habitId);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            saveTasks();
            finish();
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void updateUI(HabitCycle habit) {
        tvHabitName.setText(habit.getName());
        tvHabitDescription.setText(habit.getDescription());
        tvCycleLength.setText("循环长度: " + habit.getCycleLength() + "天");
        tvCurrentStreak.setText("当前连续: " + habit.getCurrentStreak() + "天");
        tvTotalCompletions.setText("总完成次数: " + habit.getTotalCompletions());

        if (habit.getDayTasks() != null) {
            currentTasks.clear();
            currentTasks.addAll(habit.getDayTasks());
            taskAdapter.updateData(currentTasks);
        }
    }

    private void saveTasks() {
        if (habitId != null && currentTasks != null) {
            HabitCycle currentHabit = habitViewModel.getSelectedHabitLiveData().getValue();
            if (currentHabit != null) {
                // 更新所有任务的名称
                for (int i = 0; i < currentTasks.size(); i++) {
                    DayTask task = currentTasks.get(i);
                    // 确保天数正确
                    task.setDayNumber(i + 1);
                }
                currentHabit.setDayTasks(currentTasks);
                habitViewModel.updateHabitCycle(currentHabit);
                Toast.makeText(this, "任务已保存", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        HabitCycle habit = habitViewModel.getSelectedHabitLiveData().getValue();
        if (habit == null) return;

        new AlertDialog.Builder(this)
                .setTitle("删除习惯")
                .setMessage("确定要删除习惯 \"" + habit.getName() + "\" 吗？此操作不可恢复。")
                .setPositiveButton("删除", (dialog, which) -> {
                    habitViewModel.deleteHabitCycle(habitId);
                    Toast.makeText(this, "习惯已删除", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTasks();
    }
}