package com.example.taskplanner.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import static com.example.taskplanner.ui.CreateNewTargetFragment.stepAdapter;
import static com.example.taskplanner.ui.CreateNewTargetFragment.tempFun;

public class StepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    public static ArrayList<Target.Step> Steps;
    boolean temp = true ;
    public StepAdapter(Activity context) {
        activity = context;
        Steps = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        Target.Step currentstep = Steps.get(position);
        final StepViewHolder viewHolder = (StepViewHolder) holder;

        viewHolder.step_description.setMaxHeight((int)(130 * ((float) activity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
        viewHolder.step_title.setText(currentstep.getName());
        viewHolder.step_content.setText(currentstep.getName());
        viewHolder.step_description.setText(currentstep.getDescription());

        if (position == Steps.size() - 1) {
            viewHolder.add_new_step.setVisibility(View.VISIBLE);

            viewHolder.step_title.setVisibility(View.VISIBLE);
            viewHolder.step_description.setVisibility(View.VISIBLE);
            viewHolder.step_title.setEnabled(true);
            viewHolder.step_description.setEnabled(true);
            viewHolder.step_content.setVisibility(View.GONE);

            viewHolder.step_title.requestFocus();
        } else {
            viewHolder.add_new_step.setVisibility(View.GONE);

            viewHolder.step_title.setVisibility(View.GONE);
            viewHolder.step_description.setVisibility(View.GONE);
            viewHolder.step_title.setEnabled(false);
            viewHolder.step_description.setEnabled(false);
            viewHolder.step_content.setVisibility(View.VISIBLE);
            Log.w("Lol", "wtf " + position + " , size : " + Steps.size());
        }


        viewHolder.step_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp && position == Steps.size() - 1) {
                    Steps.get(position).setName(viewHolder.step_title.getText().toString().trim());
                    Log.w("Lol", "name after : " + position);
                }
            }
        });
        viewHolder.step_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp && position == Steps.size() - 1) {
                    Steps.get(position).setDescription(viewHolder.step_description.getText().toString().trim());
                    Log.w("Lol", "des after : " + position);
                }
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Lol", "delete " + position + " <- pos ");
//                if(position==Steps.size()-1)
                    temp = false;
//                else
//                    temp = true;
                Steps.remove(position);
                stepAdapter.notifyDataSetChanged();
                if (Steps.size() == 0)
                {
                    tempFun();
                    temp = true ;
                }
            }
        });
        viewHolder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Lol", "check " + position + " <- pos");
                if (viewHolder.step_title.getVisibility() == View.VISIBLE && !viewHolder.step_title.getText().toString().trim().isEmpty()) {
                    viewHolder.step_title.setVisibility(View.GONE);
                    viewHolder.step_description.setVisibility(View.GONE);
                    viewHolder.step_content.setVisibility(View.VISIBLE);
                    viewHolder.step_content.setText(viewHolder.step_title.getText().toString().trim());
                } else {
                    viewHolder.step_title.setVisibility(View.VISIBLE);
                    viewHolder.step_description.setVisibility(View.VISIBLE);
                    viewHolder.step_content.setVisibility(View.GONE);
                }
            }
        });
        viewHolder.add_new_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.step_title.getText().toString().trim().isEmpty()) {
                    viewHolder.step_title.requestFocus();
                    viewHolder.step_title.setError("Can't be empty");
                    open_keyboard(viewHolder.step_title);
                } else if (viewHolder.step_description.getText().toString().trim().isEmpty()) {

                    if (viewHolder.step_description.getVisibility() == View.GONE) {
                        viewHolder.step_title.setVisibility(View.VISIBLE);
                        viewHolder.step_description.setVisibility(View.VISIBLE);
                        viewHolder.step_content.setVisibility(View.GONE);
                    }

                    viewHolder.step_description.requestFocus();
                    viewHolder.step_description.setError("Can't be empty");
                    open_keyboard(viewHolder.step_description);
                } else {
//                    viewHolder.add_new_step.setVisibility(View.GONE);
                    Steps.get(Steps.size() - 1).setName(viewHolder.step_title.getText().toString().trim());
                    Steps.get(Steps.size() - 1).setDescription(viewHolder.step_description.getText().toString().trim());
                    for (int i = 0; i < Steps.size(); i++) {
                        Log.w("Lol", "--- i : " + i + " , name : " + Steps.get(i).getName() + " , des : " + Steps.get(i).getDescription());
                    }
                    Steps.add(new Target.Step("", ""));
//                    Steps.add(new Target.Step(viewHolder.step_title.getText().toString().trim() , viewHolder.step_description.getText().toString().trim() ));
                    stepAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return Steps.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {

        TextView step_content;
        EditText step_title, step_description;
        ImageView delete;
        RelativeLayout check;
        Chip add_new_step;

        StepViewHolder(@NonNull View itemView) {
            super(itemView);
            step_content = itemView.findViewById(R.id.step_content);
            step_title = itemView.findViewById(R.id.step_title);
            step_description = itemView.findViewById(R.id.step_description);
            delete = itemView.findViewById(R.id.delete);
            check = itemView.findViewById(R.id.check);
            add_new_step = itemView.findViewById(R.id.add_new_step);
        }
    }

    void open_keyboard(EditText textInputLayout) {
        textInputLayout.requestFocus();     // editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
        imm.showSoftInput(textInputLayout, InputMethodManager.SHOW_IMPLICIT); //    first param -> editText
    }
}
