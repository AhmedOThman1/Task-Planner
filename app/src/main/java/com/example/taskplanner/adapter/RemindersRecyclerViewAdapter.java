package com.example.taskplanner.adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;

import static com.example.taskplanner.ui.fragments.CreateNewReminderFragment.uploadReminders;
import static com.example.taskplanner.ui.fragments.RemindersFragment.remindersAdapter;

public class RemindersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Reminder> Reminders;
    private Reminder current_reminder;
    private reminderViewHolder ViewHolder;
    private int rand = 1;

    public RemindersRecyclerViewAdapter(Activity context, List<Reminder> reminders) {
        activity = context;
        Reminders = reminders;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new reminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        current_reminder = Reminders.get(position);
        ViewHolder = (reminderViewHolder) holder;

        Calendar c = Calendar.getInstance();
        String[] t = current_reminder.getTime().split(":");
        String[] d = current_reminder.getDate().split("/");

        c.set(Calendar.YEAR,Integer.parseInt(d[2]));
        c.set(Calendar.MONTH,Integer.parseInt(d[1]));
        c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(d[0]));
        c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(t[0]));
        c.set(Calendar.MINUTE,Integer.parseInt(t[1]));
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);

        final String time = (c.get(Calendar.HOUR_OF_DAY)==0?12:c.get(Calendar.HOUR_OF_DAY))+":"+ (c.get(Calendar.MINUTE)<10?"0"+c.get(Calendar.MINUTE):c.get(Calendar.MINUTE) ) ,
        date = d[2]+"/"+(Integer.parseInt(d[1])+1)+"/"+d[0];
        ViewHolder.clock.setText( time );
        ViewHolder.title.setText(current_reminder.getTitle());
        ViewHolder.AM_PM.setText(current_reminder.getAM_PM());
        ViewHolder.date.setText(date);

        ViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

                Intent i = new Intent();
                i.putExtra("title", Reminders.get(position).getTitle());
                i.putExtra("Content", time);
                i.putExtra("type", Reminders.get(position).getType());
                if(Reminders.get(position).getType().equals("until"))
                i.putExtra("until", Reminders.get(position).getDate());
                if(Reminders.get(position).isWeek())
                {
                    i.putExtra("week", true);
                    i.putExtra("check", Reminders.get(position).getB());
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity,
                        0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                assert alarmManager != null;
                alarmManager.cancel(pendingIntent);
                Reminders.remove(position);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                uploadReminders(currentUser);
                remindersAdapter.notifyDataSetChanged();
            }
        });

        if (rand == 1) {
            ViewHolder.remind_item.setBackground(activity.getResources().getDrawable(R.drawable.background_yellowsq));
//            rand++;
        } else if (rand == 2) {
            ViewHolder.remind_item.setBackground(activity.getResources().getDrawable(R.drawable.background_bluesq));
            rand++;
        } else if (rand == 3) {
            ViewHolder.remind_item.setBackground(activity.getResources().getDrawable(R.drawable.background_zetisq));
            rand =1;
        } else if (rand == 4) {
            ViewHolder.remind_item.setBackgroundColor(activity.getResources().getColor(R.color.blue));
            rand = 1;
        }

    }

    @Override
    public int getItemCount() {
        return Reminders.size();
    }

    public static class reminderViewHolder extends RecyclerView.ViewHolder {

        TextView clock, date, AM_PM, title;
        ImageView delete;
        RelativeLayout remind_item;
        reminderViewHolder(@NonNull View itemView) {
            super(itemView);
            clock = itemView.findViewById(R.id.clock);
            date = itemView.findViewById(R.id.date);
            AM_PM = itemView.findViewById(R.id.time_AM_PM);
            title = itemView.findViewById(R.id.title);
            delete = itemView.findViewById(R.id.delete);
            remind_item = itemView.findViewById(R.id.remind_item);
        }
    }


}
