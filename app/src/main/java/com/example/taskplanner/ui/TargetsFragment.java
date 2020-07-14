package com.example.taskplanner.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.taskplanner.R;
import com.example.taskplanner.adapter.TargetsRecyclerViewAdapter;
import com.example.taskplanner.model.Target;

import java.util.Collections;

import static com.example.taskplanner.ui.MainActivity.Targets;
import static com.example.taskplanner.ui.HomeFragment.calculateNoOfColumns;
import static com.example.taskplanner.ui.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.MainActivity.hideNav;
import static com.example.taskplanner.ui.MainActivity.open_drawer;
import static com.example.taskplanner.ui.MainActivity.showNav;

public class TargetsFragment extends Fragment {


    public static TargetsRecyclerViewAdapter adapter;
    RecyclerView grid_targets_recyclerview;


    public TargetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_targets, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("");
//
//        if ( ((AppCompatActivity)getActivity()).getSupportActionBar() != null)
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow));
        }
        open_drawer = view.findViewById(R.id.open_drawer);
        isDrawerOpen = false;
        open_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDrawerOpen) {
                    hideNav();
                    open_drawer.setImageResource(R.drawable.ic_menu);
                    isDrawerOpen = false;
                } else {
                    close_keyboard();
                    showNav();
                    open_drawer.setImageResource(R.drawable.ic_back);
                    isDrawerOpen = true;
                }
            }
        });

        grid_targets_recyclerview = view.findViewById(R.id.grid_targets_recyclerview);
// sorting targets array list by target deadline
        Collections.sort(Targets, Target.targetsComparator);

        setTargetProgressBar();

        adapter = new TargetsRecyclerViewAdapter(getActivity());
        adapter.setTargets(Targets);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(calculateNoOfColumns(getContext()), StaggeredGridLayoutManager.VERTICAL);

        grid_targets_recyclerview.setAdapter(adapter);
        grid_targets_recyclerview.setLayoutManager(manager);

        grid_targets_recyclerview.addItemDecoration(new HomeFragment.ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), calculateNoOfColumns(getActivity())));

        return view;
    }

    public static void setTargetProgressBar() {
        Log.w("Lol","Targets.size() : "+Targets.size());
        for (int i = 0; i < Targets.size(); i++) {
            int process = 0;
            if (Targets.get(i).getSteps().size() != 0) {
                for (int j = 0; j < Targets.get(i).getSteps().size(); j++)
                    if (Targets.get(i).getSteps().get(i)!=null&&Targets.get(i).getSteps().get(i).isCheck())
                        process++;

                    Log.w("Log","target "+i+" Step Size : "+Targets.get(i).getSteps().size());
                    if(Targets.get(i).getSteps().size()!=0)
                Targets.get(i).setProgress(process * 100 / Targets.get(i).getSteps().size());
            }
        }
        adapter.setTargets(Targets);
        adapter.notifyDataSetChanged();
    }


    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
