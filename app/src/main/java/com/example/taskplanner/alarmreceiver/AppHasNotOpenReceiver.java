package com.example.taskplanner.alarmreceiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.taskplanner.R;
import com.example.taskplanner.ui.activities.Launcher;

import java.util.Random;

public class AppHasNotOpenReceiver extends BroadcastReceiver {
    String content, title;

    @Override
    public void onReceive(Context context, Intent intent) {

        title = context.getResources().getString(R.string.three_days_notification_title);
        content =  context.getResources().getString(R.string.three_days_notification_content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("TaskPlannerHasNotOpenNotification", "TaskPlannerHasNotOpenNotification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
        // when user click the notification open the app
        Intent notificationIntent = new Intent(context, Launcher.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Launcher.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TaskPlannerHasNotOpenNotification")
                .setContentTitle(title)
                .setContentText(content)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.drawable.love_react)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

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