package com.example.taskplanner.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.adapter.RemindersRecyclerViewAdapter;
import com.example.taskplanner.model.Challenge;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.taskplanner.ui.activities.MainActivity.Challenges;
import static com.example.taskplanner.ui.activities.MainActivity.hideNav;
import static com.example.taskplanner.ui.activities.MainActivity.open_drawer;
import static com.example.taskplanner.ui.activities.MainActivity.showNav;
import static com.example.taskplanner.ui.fragments.ChallengesFragment.uploadChallenges;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.CalendarToString;
import static com.example.taskplanner.ui.fragments.HomeFragment.isDrawerOpen;

public class CreateNewChallengeFragment extends Fragment {

    FloatingActionButton fab;
    TextInputLayout titleInputLayout, dateInputLayout, days_to_without, days;
    TextView selectDeadline, createChallenge, deadline_date;

    FirebaseUser currentUser;
    Calendar calendar_selected_day, deadline_calendar;
    int num_days = 0;
    boolean touch = true;

    public CreateNewChallengeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_new_challenge, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        open_drawer = view.findViewById(R.id.open_drawer);

        fab = view.findViewById(R.id.fab);
        titleInputLayout = view.findViewById(R.id.title_input_layout);
        dateInputLayout = view.findViewById(R.id.date_input_layout);
        days_to_without = view.findViewById(R.id.days_to_without);
        days = view.findViewById(R.id.days);
        selectDeadline = view.findViewById(R.id.select_deadline);
        createChallenge = view.findViewById(R.id.create_challenge);
        deadline_date = view.findViewById(R.id.deadline_date);

        init();

        isDrawerOpen = false;
        open_drawer.setOnClickListener(v -> {
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
        });
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("");
        fab.setOnClickListener(v -> choice_date("start"));
        selectDeadline.setOnClickListener(v -> choice_date("end"));
        deadline_date.setOnClickListener(v -> choice_date("end"));
        titleInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (days_to_without.getEditText().getText().toString().trim().isEmpty())
                    touch = true;
                if (touch) {
                    days_to_without.getEditText().setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        days.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().trim().isEmpty())
                    num_days = Integer.parseInt(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        days_to_without.getEditText().setOnTouchListener((v, event) -> touch = false);
        createChallenge.setOnClickListener(v -> {
            if (titleInputLayout.getEditText().getText().toString().trim().isEmpty()) {
                titleInputLayout.setError(getActivity().getResources().getString(R.string.empty));
                titleInputLayout.getEditText().requestFocus();
                open_keyboard(titleInputLayout.getEditText());
            } else if (days.getEditText().getText().toString().trim().isEmpty()) {
                titleInputLayout.setError("");
                days.setError(getActivity().getResources().getString(R.string.empty));
                days.getEditText().requestFocus();
                open_keyboard(days.getEditText());
            } else if (days.getEditText().getText().toString().trim().equals("0")) {
                titleInputLayout.setError("");
                days.setError(getActivity().getResources().getString(R.string.cant_be_zero));
                days.getEditText().requestFocus();
                open_keyboard(days.getEditText());
            } else if (days_to_without.getEditText().getText().toString().trim().isEmpty()) {
                titleInputLayout.setError("");
                days.setError("");
                days_to_without.setError(getActivity().getResources().getString(R.string.empty));
                days_to_without.getEditText().requestFocus();
                open_keyboard(days_to_without.getEditText());
            } else {
                titleInputLayout.setError("");
                days.setError("");
                days_to_without.setError("");

                String name = titleInputLayout.getEditText().getText().toString().trim(),
                        d = days_to_without.getEditText().getText().toString().trim();
                Challenges.add(new Challenge(name, CalendarToString(calendar_selected_day), d, num_days));
                uploadChallenges(currentUser);
                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.done_toast), Toast.LENGTH_SHORT).show();
                init();
            }
        });

        return view;
    }

    private void init() {
        calendar_selected_day = Calendar.getInstance();
        deadline_calendar = Calendar.getInstance();
        calendar_selected_day.set(Calendar.HOUR_OF_DAY, 20);
        deadline_calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar_selected_day.set(Calendar.MINUTE, 0);
        deadline_calendar.set(Calendar.MINUTE, 0);
        calendar_selected_day.set(Calendar.SECOND, 0);
        deadline_calendar.set(Calendar.SECOND, 0);
        calendar_selected_day.set(Calendar.MILLISECOND, 0);
        deadline_calendar.set(Calendar.MILLISECOND, 0);

        deadline_calendar.add(Calendar.HOUR_OF_DAY, 3);
        dateInputLayout.getEditText().setText(getActivity().getResources().getString(R.string.today));
        dateInputLayout.getEditText().setEnabled(false);
        titleInputLayout.getEditText().setText("");
        days_to_without.getEditText().setText("");
        days.getEditText().setText("0");
        deadline_date.setText("");
    }

    private void choice_date(String start_end) {
        close_keyboard();
        Calendar calendar;
        if (start_end.equals("start"))
            calendar = (Calendar) calendar_selected_day.clone();
        else
            calendar = (Calendar) deadline_calendar.clone();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            if (start_end.equals("start")) {
                calendar_selected_day.set(Calendar.YEAR, year1);
                calendar_selected_day.set(Calendar.MONTH, month1);
                calendar_selected_day.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String thisday = calendar_selected_day.get(Calendar.DAY_OF_MONTH) + "/" + (calendar_selected_day.get(Calendar.MONTH) + 1) + "/" + calendar_selected_day.get(Calendar.YEAR);
                Calendar c = Calendar.getInstance();
                if (calendar_selected_day.get(Calendar.YEAR) == c.get(Calendar.YEAR) &&
                        calendar_selected_day.get(Calendar.MONTH) == c.get(Calendar.MONTH) &&
                        calendar_selected_day.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH))
                    thisday = getActivity().getResources().getString(R.string.today);
                c.add(Calendar.DAY_OF_MONTH, 1);
                if (calendar_selected_day.get(Calendar.YEAR) == c.get(Calendar.YEAR) &&
                        calendar_selected_day.get(Calendar.MONTH) == c.get(Calendar.MONTH) &&
                        calendar_selected_day.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH))
                    thisday = getActivity().getResources().getString(R.string.tomorrow);
                dateInputLayout.getEditText().setText(thisday);
            } else {
                deadline_calendar.set(Calendar.YEAR, year1);
                deadline_calendar.set(Calendar.MONTH, month1);
                deadline_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String thisday = deadline_calendar.get(Calendar.DAY_OF_MONTH) + "/" + (deadline_calendar.get(Calendar.MONTH) + 1) + "/" + deadline_calendar.get(Calendar.YEAR);
                deadline_date.setText(thisday);
                long diff = deadline_calendar.getTimeInMillis() - calendar_selected_day.getTimeInMillis();
                num_days = (int) (diff / 1000 / 60 / 60 / 24);
                Log.w("diff", diff + "/ 1000 / 60 / 60 / 24 = " + (diff / 1000 / 60 / 60 / 24));
                if (num_days < 0) num_days = 0;
                days.getEditText().setText("" + num_days);

            }

        }, year, month, day);
        pickerDialog.show();
    }

    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void open_keyboard(EditText textInputLayout) {
        textInputLayout.requestFocus();     // editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
        imm.showSoftInput(textInputLayout, InputMethodManager.SHOW_IMPLICIT); //    first param -> editText

    }
}
