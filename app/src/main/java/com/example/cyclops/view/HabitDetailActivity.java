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
        setupViewModel();
        setupClickListeners();
    }

    private void initViews() {
        tvHabitName = findViewById(R.id.tv_habit_name);
        tvHabitDescription = findViewById(R.id.tv_habit_description);
        tvCycleLength = findViewById(R.id.tv_cycle_length);
        tvCurrentStreak = findViewById(R.id.tv_current_streak);
        tvTotalCompletions = findViewById(R.id.tv_total_completions);
        recyclerViewTasks = findViewById(R.id.recycler_view_tasks);
        btnDelete = findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViewModel() {
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        // 触发加载数据
        habitViewModel.selectHabitCycle(habitId);

        habitViewModel.getSelectedHabitLiveData().observe(this, habit -> {
            if (habit != null) {
                updateUI(habit);
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void updateUI(HabitCycle habit) {
        tvHabitName.setText(habit.getName());
        tvHabitDescription.setText(habit.getDescription());
        tvCycleLength.setText(String.valueOf(habit.getCycleLength()));
        tvCurrentStreak.setText(String.valueOf(habit.getCurrentStreak()));
        tvTotalCompletions.setText(String.valueOf(habit.getTotalCompletions()));

        // 初始化或更新任务列表
        if (currentTasks == null) {
            currentTasks = new ArrayList<>();
            if (habit.getDayTasks() != null) {
                currentTasks.addAll(habit.getDayTasks());
            }
        }

        if (taskAdapter == null) {
            taskAdapter = new DayTaskAdapter(currentTasks, (position, newTaskName) -> {
                if (position >= 0 && position < currentTasks.size()) {
                    currentTasks.get(position).setTaskName(newTaskName);
                }
            });
            recyclerViewTasks.setAdapter(taskAdapter);
        } else {
            // 如果需要完全刷新列表
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

                // [修改] 使用资源字符串
                Toast.makeText(this, R.string.toast_tasks_saved, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        HabitCycle habit = habitViewModel.getSelectedHabitLiveData().getValue();
        if (habit == null) return;

        new AlertDialog.Builder(this)
                // [修改] 使用资源字符串
                .setTitle(R.string.title_delete_habit)
                .setMessage(getString(R.string.msg_delete_habit, habit.getName()))
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    habitViewModel.deleteHabitCycle(habitId);
                    // [修改] 使用资源字符串
                    Toast.makeText(this, R.string.toast_habit_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 页面暂停/退出时自动保存任务修改
        saveTasks();
    }
}