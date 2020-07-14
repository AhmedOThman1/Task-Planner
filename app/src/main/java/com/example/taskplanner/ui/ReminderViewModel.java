package com.example.taskplanner.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskplanner.model.Reminder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReminderViewModel extends ViewModel {

    MutableLiveData<ArrayList<Reminder>> RemindersMutableLiveData = new MutableLiveData<>();
    private ArrayList<Reminder> Reminders ;

    private ArrayList<Reminder> getRemindersFromDatabase(FirebaseUser currentUser){
        Reminders = new ArrayList<>();
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Reminders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Reminders.clear();
                GenericTypeIndicator<ArrayList<Boolean>> t = new GenericTypeIndicator<ArrayList<Boolean>>() {};
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Reminder reminder = new Reminder(
                            data.child("time").getValue(String.class),
                            data.child("AM_PM").getValue(String.class),
                            data.child("date").getValue(String.class),
                            data.child("title").getValue(String.class),
                            data.child("type").getValue(String.class),
                            data.child("week").getValue(Boolean.class),
                            data.child("b").getValue(t)
                    );
                    Reminders.add(reminder);
                }

                RemindersMutableLiveData.setValue(Reminders);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed1 to read value.", error.toException());
            }
        });

        return Reminders;
    }

    public void getReminders(FirebaseUser user) {
        ArrayList<Reminder> reminders = getRemindersFromDatabase(user);
        RemindersMutableLiveData.setValue(reminders);
    }
}
