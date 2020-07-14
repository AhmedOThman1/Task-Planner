package com.example.taskplanner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    String content , title;
    @Override
    public void onReceive(Context context, Intent intent) {

//        Intent notificationIntent = new Intent(context, Love.class);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(Love.class);
//        stackBuilder.addNextIntent(notificationIntent);

//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        title = intent.getStringExtra("title");
        String s = intent.getStringExtra("Content");
        String[] time = s.split(":");
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
        now.set(Calendar.MINUTE,Integer.parseInt(time[1]));
        now.set(Calendar.SECOND,0);
        content = (now.get(Calendar.HOUR) == 0 ? 12 : now.get(Calendar.HOUR)) + ":" + ( now.get(Calendar.MINUTE)==0?"00":now.get(Calendar.MINUTE)) + " " + (now.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");

        String type = intent.getStringExtra("type");
        boolean week = intent.getBooleanExtra("week",false);

        Log.w("Lol","\n"+title+"\n"+content+"\n"+type+"\n"+week);
        if(week){
            boolean [] check = intent.getBooleanArrayExtra("check");
            Calendar c = Calendar.getInstance();
            assert check != null;

            if(check[0] &&c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
                notification(context);
            else if(check[1] &&c.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)
                notification(context);
            else if(check[2] &&c.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY)
                notification(context);
            else if(check[3] &&c.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY)
                notification(context);
            else if(check[4] &&c.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY)
                notification(context);
            else if(check[5] &&c.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY)
                notification(context);
            else if(check[6] &&c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)
                notification(context);
        }
        assert type != null;
        if(type.equals("until")){
            String ss = intent.getStringExtra("until");
            String[] until = ss.split("/");
            now.set(Calendar.DAY_OF_MONTH,Integer.parseInt(until[0]));
            now.set(Calendar.MONTH,Integer.parseInt(until[1]));
            now.set(Calendar.YEAR,Integer.parseInt(until[2]));

            Calendar temp = Calendar.getInstance();

            if(now.before(temp))notification(context);
            else{
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent i = new Intent();
                i.putExtra("title", title);
                i.putExtra("Content", s);
                i.putExtra("type", "until");
                i.putExtra("until", ss);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                assert alarmManager != null;
                alarmManager.cancel(pendingIntent);
            }

        }  else {
            notification(context);
        }
    }

    void notification( Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "n")
                .setContentTitle(title)
                .setContentText(title + " at " + content)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.drawable.love_react)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getResources().getString(R.string.reminder_notification) +
                                title + context.getResources().getString(R.string.reminder_notification_at)+content));

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }

        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.notify(999, builder.build());
    }
}