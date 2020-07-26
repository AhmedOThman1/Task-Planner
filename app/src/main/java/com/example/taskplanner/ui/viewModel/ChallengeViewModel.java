package com.example.taskplanner.ui.viewModel;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.taskplanner.model.Challenge;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChallengeViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Challenge>> ChallengesMutableLiveData = new MutableLiveData<>();
    private ArrayList<Challenge> Challenges;
    private boolean first = false;

    private ArrayList<Challenge> getChallengesFromDatabase(FirebaseUser user) {
        Challenges = new ArrayList<>();
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(user.getUid() + "/Challenges").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (first) {
                    first = false;
                    Challenges.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Challenge challenge = new Challenge(
                                data.child("name").getValue(String.class),
                                data.child("startDate").getValue(String.class),
                                data.child("days_to_without").getValue(String.class),
                                data.child("duration").getValue(Integer.class)
                        );
                        Challenges.add(challenge);
                    }
                    ChallengesMutableLiveData.setValue(Challenges);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed1 to read value.", error.toException());
            }
        });


        return Challenges;
    }

    public void getChallenges(FirebaseUser user) {
        first = true;
        ArrayList<Challenge> challenges = getChallengesFromDatabase(user);
        ChallengesMutableLiveData.setValue(challenges);
    }
}
