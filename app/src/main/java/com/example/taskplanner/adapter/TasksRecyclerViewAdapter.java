package com.example.taskplanner.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Tasks;

import java.util.Calendar;
import java.util.List;

import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<com.example.taskplanner.model.Tasks> Tasks;
    private Tasks currenttask;
    private taskViewHolder ViewHolder;
    private int rand = 1;

    public TasksRecyclerViewAdapter(Activity context) {
        activity = context;
    }

    public void setTasks( List<Tasks> tasks){
        Tasks = tasks;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new taskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        currenttask = Tasks.get(position);
        ViewHolder = (taskViewHolder) holder;

        ViewHolder.task_name.setText(currenttask.getTask_name());
        ViewHolder.doindone.setText(currenttask.getToindone());

        Calendar start_calendar = (Calendar) StringToCalendar( currenttask.getStart_calendar() ).clone();
        String date = activity.getResources().getStringArray(R.array.days)[start_calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + start_calendar.get(Calendar.DAY_OF_MONTH) +
                " " + activity.getResources().getStringArray(R.array.months)[start_calendar.get(Calendar.MONTH)] + " " + start_calendar.get(Calendar.YEAR);
        ViewHolder.date.setText(date);

        Calendar end_calendar = (Calendar) StringToCalendar( currenttask.getEnd_calendar() ).clone();
        String end_date = activity.getResources().getStringArray(R.array.days)[end_calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + end_calendar.get(Calendar.DAY_OF_MONTH) +
                " " + activity.getResources().getStringArray(R.array.months)[end_calendar.get(Calendar.MONTH)] + " " + end_calendar.get(Calendar.YEAR);
        ViewHolder.end_date.setText(end_date);

        if(date.equals(end_date)){
            ViewHolder.end_date.setVisibility(View.GONE);
            ViewHolder.start_time2.setVisibility(View.GONE);
            ViewHolder.end_time2.setVisibility(View.GONE);

            ViewHolder.start_time.setVisibility(View.VISIBLE);
            ViewHolder.end_time.setVisibility(View.VISIBLE);
            ViewHolder.bb.setVisibility(View.VISIBLE);
        }else{

            ViewHolder.end_date.setVisibility(View.VISIBLE);
            ViewHolder.start_time2.setVisibility(View.VISIBLE);
            ViewHolder.end_time2.setVisibility(View.VISIBLE);

            ViewHolder.start_time.setVisibility(View.GONE);
            ViewHolder.end_time.setVisibility(View.GONE);
            ViewHolder.bb.setVisibility(View.GONE);
        }

       String start_time = (start_calendar.get(Calendar.HOUR) == 0 ? "12" : start_calendar.get(Calendar.HOUR)) + ":" + (start_calendar.get(Calendar.MINUTE) < 10 ? "0" + start_calendar.get(Calendar.MINUTE) : start_calendar.get(Calendar.MINUTE)) +( start_calendar.get(Calendar.AM_PM)==Calendar.AM?" AM":" PM") ,
               end_time = (end_calendar.get(Calendar.HOUR) == 0 ? "12" : end_calendar.get(Calendar.HOUR)) + ":" + (end_calendar.get(Calendar.MINUTE) < 10 ? "0" + end_calendar.get(Calendar.MINUTE) : end_calendar.get(Calendar.MINUTE)) +( end_calendar.get(Calendar.AM_PM)==Calendar.AM?" AM":" PM") ;

        ViewHolder.start_time.setText(start_time);
        ViewHolder.end_time.setText(end_time);

        ViewHolder.start_time2.setText(start_time);
        ViewHolder.end_time2.setText(end_time);

        if (position%4 == 0) {
            ViewHolder.task_card.setCardBackgroundColor(activity.getResources().getColor(R.color.zeti));
        } else if (position%4 == 1) {
            ViewHolder.task_card.setCardBackgroundColor(activity.getResources().getColor(R.color.red));
        } else if (position%4 == 2) {
            ViewHolder.task_card.setCardBackgroundColor(activity.getResources().getColor(R.color.yellow));
        } else if (position%4 == 3) {
            ViewHolder.task_card.setCardBackgroundColor(activity.getResources().getColor(R.color.blue));
        }


    }

    @Override
    public int getItemCount() {
        return Tasks.size();
    }

    public static class taskViewHolder extends RecyclerView.ViewHolder {

        TextView task_name, date, doindone, start_time, end_time , end_date , start_time2 , end_time2 , bb;
        CardView task_card;

        taskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_name = itemView.findViewById(R.id.task_name);
            date = itemView.findViewById(R.id.date);
            end_date = itemView.findViewById(R.id.end_date);
            doindone = itemView.findViewById(R.id.toindone);
            start_time = itemView.findViewById(R.id.start_time);
            end_time = itemView.findViewById(R.id.end_time);
            start_time2 = itemView.findViewById(R.id.start_time2);
            end_time2 = itemView.findViewById(R.id.end_time2);
            task_card = itemView.findViewById(R.id.task_card);
            bb = itemView.findViewById(R.id.bb);
        }
    }


}
