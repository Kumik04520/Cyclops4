package com.example.cyclops;

import com.example.cyclops.repository.RoomHabitRepository;

public class Application extends android.app.Application {

    private static Application instance;
    private RoomHabitRepository habitRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 初始化Repository
        habitRepository = RoomHabitRepository.getInstance(this);


    }

    public static Application getInstance() {
        return instance;
    }

    public RoomHabitRepository getHabitRepository() {
        return habitRepository;
    }
}