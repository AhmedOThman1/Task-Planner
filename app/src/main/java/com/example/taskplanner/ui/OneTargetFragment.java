package com.example.taskplanner.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.taskplanner.R;
import com.example.taskplanner.adapter.OneTargetStepAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import static com.example.taskplanner.ui.MainActivity.Targets;

public class OneTargetFragment extends Fragment {

    RecyclerView one_target_recyclerview;
    int position, color;
    OneTargetStepAdapter oneTargetStepAdapter;
    TextInputLayout target_note;

    public OneTargetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one_target, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        one_target_recyclerview = view.findViewById(R.id.one_target_recyclerview);
        target_note = view.findViewById(R.id.target_note);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

//        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null)
//            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getArguments();
        position = bundle.getInt("project pos", -1);
        color = bundle.getInt("color", -1);
        if (color == 0)
        {
            toolbar.setBackgroundResource(R.color.zeti);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.zeti));
            }
        }
        else if (color == 1)
        {
            toolbar.setBackgroundResource(R.color.red);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.red));
            }
        }
        else if (color == 2)
        {
            toolbar.setBackgroundResource(R.color.yellow);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.yellow));
            }
        }
        else if (color == 3)
        {
            toolbar.setBackgroundResource(R.color.blue);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.blue));
            }
        }


        getActivity().setTitle(Targets.get(position).getName());

        Log.w("Log","pos:::: "+position);
        oneTargetStepAdapter = new OneTargetStepAdapter(getActivity(), Targets.get(position).getSteps());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        one_target_recyclerview.setAdapter(oneTargetStepAdapter);
        one_target_recyclerview.setLayoutManager(manager);

        if(!Targets.get(position).getNote().equals("")){
            target_note.setVisibility(View.VISIBLE);
            Objects.requireNonNull(target_note.getEditText()).setText(Targets.get(position).getNote());
        }

        return view;
    }
}
