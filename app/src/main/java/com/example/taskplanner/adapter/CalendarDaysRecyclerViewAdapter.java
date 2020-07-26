package com.example.taskplanner.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;

import java.util.Calendar;
import java.util.List;

import static com.example.taskplanner.ui.fragments.CalendarFragment.last_selected_day;

public class CalendarDaysRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Calendar> Days;
    private Calendar currentday;
    private dayViewHolder ViewHolder;

    public CalendarDaysRecyclerViewAdapter(Activity context, List<Calendar> days) {
        activity = context;
        Days = days;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new dayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        currentday = Days.get(position);
        ViewHolder = (dayViewHolder) holder;

        ViewHolder.dayName.setText(activity.getResources().getStringArray(R.array.days)[currentday.get(Calendar.DAY_OF_WEEK) - 1]);
        String num = "" + currentday.get(Calendar.DAY_OF_MONTH);
        ViewHolder.dayNum.setText(num);

        if (position == 0) {
            ViewHolder.dayName.setTextColor(activity.getResources().getColor(R.color.red));
            ViewHolder.dayNum.setTextColor(activity.getResources().getColor(R.color.red));
        } else if (position == last_selected_day) {

            ViewHolder.dayName.setTextColor(activity.getResources().getColor(R.color.zeti));
            ViewHolder.dayNum.setTextColor(activity.getResources().getColor(R.color.zeti));
        } else {
            ViewHolder.dayName.setTextColor(activity.getResources().getColor(R.color.defualt_textview_color));
            ViewHolder.dayNum.setTextColor(activity.getResources().getColor(R.color.black));
        }


    }

    @Override
    public int getItemCount() {
        return Days.size();
    }

    public static class dayViewHolder extends RecyclerView.ViewHolder {

        TextView dayName, dayNum;

        dayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayName = itemView.findViewById(R.id.day_name);
            dayNum = itemView.findViewById(R.id.day_num);
        }
    }


}
