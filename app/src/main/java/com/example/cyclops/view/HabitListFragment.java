package com.example.cyclops.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.HabitCycleEngine;
import com.example.cyclops.adapter.HabitCycleAdapter;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel;

import java.util.ArrayList;

public class HabitListFragment extends Fragment {

    private HabitViewModel habitViewModel;
    private RecyclerView recyclerView;
    private HabitCycleAdapter adapter;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_list, container, false);
        initViews(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_habits);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }

    private void setupRecyclerView() {
        adapter = new HabitCycleAdapter(new ArrayList<>(), new HabitCycleAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(HabitCycle habitCycle) {
                openHabitDetail(habitCycle);
            }

            @Override
            public void onCompleteClick(HabitCycle habitCycle) {
                int currentDay = HabitCycleEngine.calculateCurrentDay(habitCycle);
                habitViewModel.completeDay(habitCycle.getId(), currentDay);

                // [修改] 使用 getString 获取格式化的资源字符串
                String msg = getString(R.string.checking_in_toast, habitCycle.getName());
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        habitViewModel.getHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                adapter.updateData(habits);
                updateEmptyState(habits.isEmpty());
            }
        });

        habitViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void openHabitDetail(HabitCycle habitCycle) {
        Intent intent = new Intent(getContext(), HabitDetailActivity.class);
        intent.putExtra("HABIT_ID", habitCycle.getId());
        startActivity(intent);
    }
}