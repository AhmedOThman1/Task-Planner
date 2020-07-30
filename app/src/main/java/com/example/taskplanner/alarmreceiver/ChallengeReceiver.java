package com.example.taskplanner.alarmreceiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Challenge;
import com.example.taskplanner.ui.viewModel.ChallengeViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;

public class ChallengeReceiver extends BroadcastReceiver {

    String UID;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        Log.w("broadcastx","here!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("TaskPlannerChallengeNotification", "TaskPlannerChallengeNotification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }


        UID = intent.getStringExtra("UID");

        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(UID + "/Challenges").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Challenge challenge = new Challenge(
                            data.child("name").getValue(String.class),
                            data.child("startDate").getValue(String.class),
                            data.child("days_to_without").getValue(String.class),
                            data.child("duration").getValue(Integer.class)
                    );
                    challengeCalculation(challenge);
                    Log.w("broadcastx",challenge.getName()+".");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("broadcastx", "Failed1 to read value.", error.toException());
            }
        });


    }

    private void challengeCalculation(Challenge challenge) {
        Calendar start = (Calendar) StringToCalendar(challenge.getStartDate()).clone();
        int difference = (int) (((float) ((Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()) / 1000 / 60 / 60 / 24) / challenge.getDuration()) * 100);
        if (difference == 30)
            notification(challenge.getName(), context.getResources().getString(R.string.challenge_30_notification_content));
        else if (difference == 50)
            notification(challenge.getName(), context.getResources().getString(R.string.challenge_50_notification_content));
        else if (difference == 90)
            notification(challenge.getName(), context.getResources().getString(R.string.challenge_90_notification_content));
        else if (difference == 100)
            notification(challenge.getName(), context.getResources().getString(R.string.challenge_100_notification_content));

        //Toast.makeText(context, "broadcast: "+challenge.getName()+","+difference+".", Toast.LENGTH_SHORT).show();
    }

    private void notification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TaskPlannerChallengeNotification")
                .setContentTitle(title)
                .setContentText(content)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.drawable.love_react)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content));

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }

        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        Random random = new Random(1000);
        compat.notify(random.nextInt(), builder.build());
    }
}