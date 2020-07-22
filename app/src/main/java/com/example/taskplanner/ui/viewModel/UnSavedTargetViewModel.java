package com.example.taskplanner.ui.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskplanner.model.Target;
import com.example.taskplanner.model.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UnSavedTargetViewModel extends ViewModel {

    public MutableLiveData<Target> TargetMutableLiveData = new MutableLiveData<>();
    private Target UnSavedTarget;
    private boolean ha = false, ha2 = false;

    private Target getUnSavedTargetFromDatabase(FirebaseUser currentUser) {
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/UnSavedTarget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (ha) {

                    Log.w("yarab",data.getChildrenCount() + ","+
                                    data.child("name").getValue(String.class)+","
                            +data.child("note").getValue(String.class)+","
                            +data.child("day").getValue(String.class) );
                    UnSavedTarget = new Target(
                            data.child("name").getValue(String.class),
                            data.child("note").getValue(String.class),
                            data.child("day").getValue(String.class)
                    );
                    ha2 = true;
                    UnSavedTarget.setProgress(data.child("progress").getValue(Integer.class));
                    getThisTargetStepsFromDatabase(data.getKey(), currentUser);

                    TargetMutableLiveData.setValue(UnSavedTarget);
                    ha = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed1 to read value.", error.toException());
            }
        });

        return UnSavedTarget;
    }

    private void getThisTargetStepsFromDatabase(final String targetNum, final FirebaseUser currentUser) {
        // Read from the database
        DatabaseReference myDbRef = FirebaseDatabase.getInstance().getReference();
        myDbRef.child(currentUser.getUid() + "/UnSavedTarget/steps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (ha2) {
                    if (dataSnapshot.exists())
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Target.Step step = new Target.Step(
                                    data.child("name").getValue(String.class),
                                    data.child("description").getValue(String.class));
                            step.setCheck(data.child("check").getValue(Boolean.class));
                            UnSavedTarget.getSteps().add(step);
                        }
                    TargetMutableLiveData.setValue(UnSavedTarget);
                    ha2 = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed2 to read value.", error.toException());
            }
        });
    }


    public void getUnSavedTarget(FirebaseUser user) {
        ha = true;
        Target target = getUnSavedTargetFromDatabase(user);
        TargetMutableLiveData.setValue(target);
    }
}
