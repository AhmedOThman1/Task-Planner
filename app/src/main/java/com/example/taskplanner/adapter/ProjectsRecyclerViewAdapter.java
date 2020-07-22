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
import com.example.taskplanner.model.Activeprojects;

import java.util.List;

public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Activeprojects> Projects;
    private Activeprojects currentproject;
    private projectViewHolder ViewHolder;
    private int rand = 1;

    public ProjectsRecyclerViewAdapter(Activity context) {
        activity = context;

    }

    public void setProjects( List<Activeprojects> projects){
        Projects = projects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new projectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        currentproject = Projects.get(position);
        ViewHolder = (projectViewHolder) holder;

        ViewHolder.progress.setText((currentproject.getProgress()) + "");
        ViewHolder.title.setText(currentproject.getTitle());
        ViewHolder.time.setText(currentproject.getTime());
        ViewHolder.progressBar.setProgress(currentproject.getProgress());
        ViewHolder.project_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Fragment tasks_fragment = new TasksFragment();
//                Bundle bundle= new Bundle();
//                bundle.putString("project name", Projects.get(position).getTitle());
//                bundle.putString("color", "" + position % 4);
//                tasks_fragment.setArguments(bundle);
//                ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, tasks_fragment).commit();

            }
        });

//        ViewHolder.project_card.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Projects.remove(position);
//                adapter.notifyDataSetChanged();
//                return true;
//            }
//        });
        if (position%4 == 0) {
            ViewHolder.project_card.setCardBackgroundColor(activity.getResources().getColor(R.color.zeti));
        } else if (position%4 == 1) {
            ViewHolder.project_card.setCardBackgroundColor(activity.getResources().getColor(R.color.red));
            rand++;
        } else if (position%4 == 2) {
            ViewHolder.project_card.setCardBackgroundColor(activity.getResources().getColor(R.color.yellow));
            ViewHolder.time.setTextColor(activity.getResources().getColor(R.color.text_time_yellow_card));
        } else if (position%4 == 3) {
            ViewHolder.project_card.setCardBackgroundColor(activity.getResources().getColor(R.color.blue));
        }


    }

    @Override
    public int getItemCount() {
        return Projects.size();
    }

    public static class projectViewHolder extends RecyclerView.ViewHolder {

        TextView progress, title, time;
        ProgressBar progressBar;
        CardView project_card;

        projectViewHolder(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            progressBar = itemView.findViewById(R.id.progressBar);
            project_card = itemView.findViewById(R.id.active_projects_card);
        }
    }


}
