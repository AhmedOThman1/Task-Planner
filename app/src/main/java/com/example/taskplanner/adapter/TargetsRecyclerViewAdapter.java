package com.example.taskplanner.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Target;

import java.util.List;

public class TargetsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Target> Targets;
    private Target currenttarget;
    private targetViewHolder ViewHolder;

    public TargetsRecyclerViewAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setTargets(List<Target> targets){
        Targets = targets;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new targetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        currenttarget = Targets.get(position);
        ViewHolder = (targetViewHolder) holder;

        ViewHolder.progress.setText((currenttarget.getProgress()) + "");
        ViewHolder.title.setText(currenttarget.getName());

        String[] startArr = currenttarget.getDay().split(",");
        String day = startArr[2] + "/" + (Integer.parseInt(startArr[1]) + 1) + "/" + startArr[0];
        ViewHolder.time.setText(day);
        ViewHolder.progressBar.setProgress((int) currenttarget.getProgress());

        if (position%4 == 0) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.zeti));
        } else if (position%4 == 1) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.red));
        } else if (position%4 == 2) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.yellow));
            ViewHolder.time.setTextColor(activity.getResources().getColor(R.color.text_time_yellow_card));
        } else if (position%4 == 3) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.blue));
        }


    }

    @Override
    public int getItemCount() {
        return Targets.size();
    }

    public static class targetViewHolder extends RecyclerView.ViewHolder {

        TextView progress, title, time;
        ProgressBar progressBar;
        CardView target_card;

        targetViewHolder(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            progressBar = itemView.findViewById(R.id.progressBar);
            target_card = itemView.findViewById(R.id.active_projects_card);
        }
    }


}
