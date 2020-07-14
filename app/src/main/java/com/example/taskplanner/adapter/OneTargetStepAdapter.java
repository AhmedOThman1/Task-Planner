package com.example.taskplanner.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Target;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class OneTargetStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<Target.Step> Steps;
    private StepViewHolder viewHolder;
    public OneTargetStepAdapter(Activity context , ArrayList<Target.Step> steps) {
        activity = context;
        Steps = steps;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final Target.Step currentstep = Steps.get(position);
        viewHolder = (StepViewHolder) holder;

        viewHolder.pos=position;
        viewHolder.step_title.setText(currentstep.getName());
        viewHolder.step_content.setText(currentstep.getName());
        viewHolder.step_description.setText(currentstep.getDescription());
        viewHolder.add_new_step.setVisibility(View.GONE);
        viewHolder.delete.setVisibility(View.GONE);
        viewHolder.step_title.setEnabled(false);
        viewHolder.step_description.setEnabled(false);
        stepCheck(Steps.get(position).isCheck());

        viewHolder.check.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.w("Lol", "check " + position + " , "+viewHolder.pos+" <- pos");
                if (viewHolder.step_title.getVisibility() == View.VISIBLE && !viewHolder.step_title.getText().toString().trim().isEmpty()) {
                    viewHolder.step_title.setVisibility(View.GONE);
                    viewHolder.step_description.setVisibility(View.GONE);
                    viewHolder.step_content.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.step_title.setVisibility(View.VISIBLE);
                    viewHolder.step_description.setVisibility(View.VISIBLE);
                    viewHolder.step_content.setVisibility(View.GONE);
                }
                return true;
            }
        });

        viewHolder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Lol", "check " + position + " , "+viewHolder.pos+" <- pos");

                Steps.get(position).setCheck(!Steps.get(position).isCheck());
                viewHolder.step_title.setVisibility(View.GONE);
                viewHolder.step_description.setVisibility(View.GONE);
                viewHolder.step_content.setVisibility(View.VISIBLE);
                stepCheck(Steps.get(position).isCheck());
            }
        });

    }

    void stepCheck(boolean check){
        if(check){
            viewHolder.check_circle.setImageResource(R.drawable.background_green_circle);
            viewHolder.check_image.setImageResource(R.drawable.ic_white_check);
        } else{
            viewHolder.check_circle.setImageResource(R.drawable.circle_shape2dp);
            viewHolder.check_image.setImageResource(R.drawable.ic_check);
        }
    }

    @Override
    public int getItemCount() {
        return Steps.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {

        TextView step_content;
        EditText step_title, step_description;
        ImageView delete , check_image , check_circle;
        RelativeLayout check;
        Chip add_new_step;
        int pos;
        StepViewHolder(@NonNull View itemView) {
            super(itemView);
            step_content = itemView.findViewById(R.id.step_content);
            step_title = itemView.findViewById(R.id.step_title);
            step_description = itemView.findViewById(R.id.step_description);
            delete = itemView.findViewById(R.id.delete);
            check = itemView.findViewById(R.id.check);
            add_new_step = itemView.findViewById(R.id.add_new_step);
            check_image = itemView.findViewById(R.id.check_image);
            check_circle = itemView.findViewById(R.id.check_circle);
        }
    }

}
