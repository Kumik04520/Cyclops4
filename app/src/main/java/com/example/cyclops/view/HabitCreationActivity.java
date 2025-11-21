package com.example.cyclops.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cyclops.R;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel;

import java.util.ArrayList;
import java.util.List;

public class HabitCreationActivity extends AppCompatActivity {

    private EditText etHabitName;
    private EditText etHabitDescription;
    private Spinner spinnerCycleLength;
    private LinearLayout layoutDayTasks;
    private Button btnCreateHabit;
    private Button btnBack; // [新增] 声明返回按钮

    private HabitViewModel habitViewModel;
    private int currentCycleLength = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_creation);

        initViews();
        setupSpinner();
        setupClickListeners();

        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
    }

    private void initViews() {
        etHabitName = findViewById(R.id.et_habit_name);
        etHabitDescription = findViewById(R.id.et_habit_description);
        spinnerCycleLength = findViewById(R.id.spinner_cycle_length);
        layoutDayTasks = findViewById(R.id.layout_day_tasks);
        btnCreateHabit = findViewById(R.id.btn_create_habit);
        btnBack = findViewById(R.id.btn_back); // [新增] 初始化返回按钮
    }

    private void setupSpinner() {
        Integer[] items = new Integer[]{3, 4, 5, 6, 7};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerCycleLength.setAdapter(adapter);

        spinnerCycleLength.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCycleLength = (Integer) parent.getItemAtPosition(position);
                updateDayTasksLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateDayTasksLayout() {
        layoutDayTasks.removeAllViews();
        for (int i = 1; i <= currentCycleLength; i++) {
            // 这里使用的是之前让你创建的 item_day_task_creation.xml
            View dayTaskView = getLayoutInflater().inflate(R.layout.item_day_task_creation, layoutDayTasks, false);

            TextView tvDayLabel = dayTaskView.findViewById(R.id.tv_day_label);
            EditText etTaskName = dayTaskView.findViewById(R.id.et_task_name);

            // 使用资源字符串
            tvDayLabel.setText(getString(R.string.day_format, i));
            etTaskName.setHint(getString(R.string.hint_day_task, i));
            etTaskName.setTag(i);

            layoutDayTasks.addView(dayTaskView);
        }
    }

    private void setupClickListeners() {
        btnCreateHabit.setOnClickListener(v -> createHabitCycle());

        // [新增] 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());
    }

    private void createHabitCycle() {
        String habitName = etHabitName.getText().toString().trim();
        String habitDescription = etHabitDescription.getText().toString().trim();

        if (TextUtils.isEmpty(habitName)) {
            Toast.makeText(this, R.string.toast_enter_name, Toast.LENGTH_SHORT).show();
            return;
        }

        List<DayTask> dayTasks = new ArrayList<>();
        for (int i = 0; i < layoutDayTasks.getChildCount(); i++) {
            View dayTaskView = layoutDayTasks.getChildAt(i);
            EditText etTaskName = dayTaskView.findViewById(R.id.et_task_name);
            String taskName = etTaskName.getText().toString().trim();

            if (TextUtils.isEmpty(taskName)) {
                taskName = getString(R.string.default_task_name, (i + 1));
            }

            dayTasks.add(new DayTask(i + 1, taskName));
        }

        HabitCycle habitCycle = new HabitCycle();
        habitCycle.setName(habitName);
        habitCycle.setDescription(habitDescription);
        habitCycle.setCycleLength(currentCycleLength);
        habitCycle.setDayTasks(dayTasks);
        habitCycle.setUserId(""); // 确保非空

        habitViewModel.addHabitCycle(habitCycle);

        Toast.makeText(this, R.string.toast_habit_created, Toast.LENGTH_SHORT).show();
        finish();
    }
}