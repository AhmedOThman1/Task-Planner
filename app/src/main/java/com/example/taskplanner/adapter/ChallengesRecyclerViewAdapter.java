package com.example.taskplanner.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Challenge;

import java.util.Calendar;
import java.util.List;

import static com.example.taskplanner.ui.activities.MainActivity.Challenges;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;

public class ChallengesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Challenge> Challenges;
    private Challenge currentchallenge;
    private challengeViewHolder ViewHolder;

    public ChallengesRecyclerViewAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setChallenges(List<Challenge> challenges) {
        Challenges = challenges;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new challengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        currentchallenge = Challenges.get(position);
        ViewHolder = (challengeViewHolder) holder;

        ViewHolder.title.setText(currentchallenge.getName());
        Calendar start = (Calendar) StringToCalendar(Challenges.get(position).getStartDate()).clone();
        ViewHolder.time.setText(start.get(Calendar.DAY_OF_MONTH) + "/" + (start.get(Calendar.MONTH) + 1) + "/" + start.get(Calendar.YEAR));
        ViewHolder.progressBar.setMax(Challenges.get(position).getDuration());
        long difference = Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ViewHolder.progressBar.setProgress((int) (difference / 1000 / 60 / 60 / 24), true);
        } else
            ViewHolder.progressBar.setProgress((int) (difference / 1000 / 60 / 60 / 24));

        ViewHolder.progress.setText( (int) ( ( (float)(difference / 1000 / 60 / 60 / 24)  / currentchallenge.getDuration()  ) *100 )+"%");


        if (position % 4 == 0) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.zeti));
        } else if (position % 4 == 1) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.red));
        } else if (position % 4 == 2) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.yellow));
            ViewHolder.time.setTextColor(activity.getResources().getColor(R.color.text_time_yellow_card));
        } else if (position % 4 == 3) {
            ViewHolder.target_card.setCardBackgroundColor(activity.getResources().getColor(R.color.blue));
        }


    }

    @Override
    public int getItemCount() {
        return Challenges.size();
    }

    public static class challengeViewHolder extends RecyclerView.ViewHolder {

        TextView progress, title, time;
        ProgressBar progressBar;
        CardView target_card;

        challengeViewHolder(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            progressBar = itemView.findViewById(R.id.progressBar);
            target_card = itemView.findViewById(R.id.active_projects_card);
        }
    }


}
