package com.example.taskplanner.ui.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskplanner.model.Reminder;
import com.example.taskplanner.model.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UnSavedTaskViewModel extends ViewModel {

    public MutableLiveData<Tasks> TaskMutableLiveData = new MutableLiveData<>();
    private Tasks UnSavedTask;
    private boolean ha = false;

    private Tasks getUnSavedTaskFromDatabase(FirebaseUser currentUser) {
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/UnSavedTask").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (ha) {
                    UnSavedTask = new Tasks(
                            data.child("projectName").getValue(String.class),
                            data.child("task_name").getValue(String.class),
                            data.child("start_calendar").getValue(String.class),
                            data.child("end_calendar").getValue(String.class),
                            data.child("task_description").getValue(String.class),
                            data.child("toindone").getValue(String.class)
                    );

                    TaskMutableLiveData.setValue(UnSavedTask);
                    ha = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed1 to read value.", error.toException());
            }
        });

        return UnSavedTask;
    }

    public void getUnSavedTask(FirebaseUser user) {
        ha = true;
        Tasks task = getUnSavedTaskFromDatabase(user);
        TaskMutableLiveData.setValue(task);
    }
}
