package com.example.taskplanner;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TaskPlanner extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
