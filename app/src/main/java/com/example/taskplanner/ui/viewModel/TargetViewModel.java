package com.example.taskplanner.ui.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskplanner.model.Activeprojects;
import com.example.taskplanner.model.Target;
import com.example.taskplanner.model.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TargetViewModel extends ViewModel {

    public MutableLiveData<ArrayList<Target>> TargetsMutableLiveData = new MutableLiveData<>();

    private ArrayList<Target> Targets;

    private ArrayList<Target> getTargetFromDatabase(FirebaseUser currentUser) {

        Targets = new ArrayList<>();

        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Targets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Targets.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Target target = new Target(
                            data.child("name").getValue(String.class),
                            data.child("note").getValue(String.class),
                            data.child("day").getValue(String.class)
                    );
                    target.setProgress(data.child("progress").getValue(Integer.class));
                    Targets.add(target);
                    getThisTargetStepsFromDatabase(data.getKey(), currentUser);
                }
                TargetsMutableLiveData.setValue(Targets);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed1 to read value.", error.toException());
            }
        });


        return Targets;
    }

    private void getThisTargetStepsFromDatabase(final String targetNum, final FirebaseUser currentUser) {
        // Read from the database
        DatabaseReference myDbRef = FirebaseDatabase.getInstance().getReference();
        myDbRef.child(currentUser.getUid() + "/Targets/" + targetNum + "/steps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists())
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Target.Step step = new Target.Step(
                                data.child("name").getValue(String.class),
                                data.child("description").getValue(String.class));
                        step.setCheck(data.child("check").getValue(boolean.class));

                        if (Integer.parseInt(targetNum) < Targets.size() && !Targets.get(Integer.parseInt(targetNum)).getSteps().contains(step))
                            Targets.get(Integer.parseInt(targetNum)).getSteps().add(step);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed2 to read value.", error.toException());
            }
        });
    }

    public void getTargets(FirebaseUser user) {
        ArrayList<Target> targets = getTargetFromDatabase(user);
        TargetsMutableLiveData.setValue(targets);
    }

}
