package com.example.cyclops.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cyclops.HabitCycleEngine;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.RoomHabitRepository;

import java.util.List;

public class HabitViewModel extends AndroidViewModel {
    private final MutableLiveData<String> _selectedHabitId = new MutableLiveData<>();
    private HabitRepository habitRepository;
    private LiveData<List<HabitCycle>> habitsLiveData;
    private MutableLiveData<String> errorMessageLiveData;
    private String currentSelectedHabitId;
    private final LiveData<HabitCycle> selectedHabitLiveData;

    public HabitViewModel(Application application) {
        super(application);
        this.habitRepository = RoomHabitRepository.getInstance(application);
        this.habitsLiveData = habitRepository.getAllHabitCycles();
        this.errorMessageLiveData = new MutableLiveData<>();
        this.selectedHabitLiveData = Transformations.switchMap(_selectedHabitId, id -> {
            return habitRepository.getHabitCycleById(id);
        });
    }

    public LiveData<List<HabitCycle>> getHabitsLiveData() {
        return habitsLiveData;
    }

    public LiveData<HabitCycle> getSelectedHabitLiveData() {
        return selectedHabitLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void addHabitCycle(HabitCycle habitCycle) {
        habitRepository.addHabitCycle(habitCycle);
        // No need to call loadAllHabits() - LiveData will update automatically
    }

    public void updateHabitCycle(HabitCycle habitCycle) {
        habitRepository.updateHabitCycle(habitCycle);
        // No need to call loadAllHabits() - LiveData will update automatically
    }

    public void deleteHabitCycle(String habitId) {
        habitRepository.deleteHabitCycle(habitId);
        // No need to call loadAllHabits() - LiveData will update automatically
    }

    public void selectHabitCycle(String habitId) {
            _selectedHabitId.setValue(habitId);
    }

    public void completeDay(String habitId, int dayNumber) {
        habitRepository.completeDay(habitId, dayNumber);
        // LiveData will automatically update when database changes
        // Re-select current habit to update the detail view
        if (habitId.equals(currentSelectedHabitId)) {
            selectHabitCycle(habitId);
        }
    }

    public int getCurrentDayForHabit(HabitCycle habitCycle) {
        return HabitCycleEngine.calculateCurrentDay(habitCycle);
    }
}