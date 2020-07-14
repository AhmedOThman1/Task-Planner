package com.example.taskplanner.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.taskplanner.R;
import com.example.taskplanner.adapter.RemindersRecyclerViewAdapter;

import static com.example.taskplanner.ui.MainActivity.Reminders;
import static com.example.taskplanner.ui.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.MainActivity.hideNav;
import static com.example.taskplanner.ui.MainActivity.open_drawer;
import static com.example.taskplanner.ui.MainActivity.showNav;

public class RemindersFragment extends Fragment {

    public static RemindersRecyclerViewAdapter remindersAdapter;
    RecyclerView reminders_recyclerview;

    public RemindersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);


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

        reminders_recyclerview = view.findViewById(R.id.reminders_recyclerview);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setBackgroundResource(R.color.blue);
        getActivity().setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blue));
        }

        remindersAdapter = new RemindersRecyclerViewAdapter(getActivity(), Reminders);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        reminders_recyclerview.setAdapter(remindersAdapter);
        reminders_recyclerview.setLayoutManager(manager);

        return view;
    }


    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
