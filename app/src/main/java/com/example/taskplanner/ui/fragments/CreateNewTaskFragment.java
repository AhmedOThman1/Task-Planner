package com.example.taskplanner.ui.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Activeprojects;
import com.example.taskplanner.model.Tasks;
import com.example.taskplanner.ui.viewModel.UnSavedTaskViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import static com.example.taskplanner.ui.activities.MainActivity.Projects;
import static com.example.taskplanner.ui.activities.MainActivity.ToDo_tasks;
import static com.example.taskplanner.ui.fragments.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.activities.MainActivity.hideNav;
import static com.example.taskplanner.ui.activities.MainActivity.open_drawer;
import static com.example.taskplanner.ui.activities.MainActivity.showNav;
import static com.example.taskplanner.ui.activities.TodayActivity.days;

public class CreateNewTaskFragment extends Fragment {

    ChipGroup projects_chips;
    Chip add_project_chip;
    TextInputLayout date, title_input_layout, description;
    CollapsingToolbarLayout coolToolbar;
    AppBarLayout appBarLayout;
    TextView start_time_tv, end_time_tv, start_am_pm, end_am_pm;
    ImageView startLineError, endLineError;
    public static Calendar calendar_selected_day, calendar, start_calender, end_calendar;
    boolean sleep1 = false, sleep2 = false;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public static String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};


    Tasks unSavedTask = new Tasks();

    private static final String TAG = "Create";
    FirebaseUser currentUser;

    public CreateNewTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_new_task, container, false);


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.back);
//
//        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow));
        }

        date = view.findViewById(R.id.date_input_layout);
        coolToolbar = view.findViewById(R.id.coolToolbar);
        start_time_tv = view.findViewById(R.id.start_time);
        end_time_tv = view.findViewById(R.id.end_time);
        start_am_pm = view.findViewById(R.id.am_pm);
        end_am_pm = view.findViewById(R.id.am_pm2);
        projects_chips = view.findViewById(R.id.projects_chips);
        add_project_chip = view.findViewById(R.id.add_project_chip);
        title_input_layout = view.findViewById(R.id.title_input_layout);
        description = view.findViewById(R.id.description);
        appBarLayout = view.findViewById(R.id.AppBarL);
        startLineError = view.findViewById(R.id.start_line_error);
        endLineError = view.findViewById(R.id.end_line_error);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        init();


        open_drawer = view.findViewById(R.id.open_drawer);
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

        description.getEditText().setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });

/// to show date picker and select date & put it in date TextView
        view.findViewById(R.id.fab).setOnClickListener(v -> choice_date());
        date.setOnClickListener(v -> choice_date());
        Objects.requireNonNull(date.getEditText()).setOnClickListener(v -> choice_date());

        ///show time picker & show it
        view.findViewById(R.id.start_time_layout).setOnClickListener(v -> choice_start_time());

        view.findViewById(R.id.end_time_layout).setOnClickListener(v -> choice_end_time());

        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (Math.abs(i) == appBarLayout.getTotalScrollRange()) {
                coolToolbar.setTitle("Create new task");
                appBarLayout.setBackgroundResource(R.drawable.background_toolbar2);
            } else if (Math.abs(i) < (appBarLayout.getTotalScrollRange()) / 2) {
                coolToolbar.setTitle("");
                appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
            } else {
                coolToolbar.setTitle("Create new task");
                appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
            }
        });

        // add exist chips
        add_exist_chips();

        //add new chip
        add_project_chip.setOnClickListener(v -> add_project_chip_dialog());

        Objects.requireNonNull(title_input_layout.getEditText()).setOnEditorActionListener((v, actionId, event) -> {
            if (!title_input_layout.getEditText().getText().toString().isEmpty())
                title_input_layout.setError(null);

            return false;
        });

        view.findViewById(R.id.create_task).setOnClickListener(v -> {

            close_keyboard();

            calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 9);

            Calendar calender_half_hour = (Calendar) start_calender.clone();
            calender_half_hour.add(Calendar.MINUTE, 29);

            Chip chip = projects_chips.findViewById(projects_chips.getCheckedChipId());
            Tasks checkTask = check_if_there_task_at_this_time(start_calender, end_calendar);

            if (start_calender.get(Calendar.AM_PM) == Calendar.PM && end_calendar.get(Calendar.AM_PM) == Calendar.AM) {
                end_calendar.set(Calendar.DATE, start_calender.get(Calendar.DATE));
                end_calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            if (title_input_layout.getEditText().getText().toString().trim().isEmpty()) {
                title_input_layout.setError("Can't be empty");
                title_input_layout.requestFocus();
                open_keyboard(title_input_layout.getEditText());
                appBarLayout.setExpanded(true);
            } else if (start_calender.before(calendar)) {
//
//                    calendar_selected_day.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
//                            calendar_selected_day.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
//                            calendar_selected_day.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
//                            (start_calender.get(Calendar.HOUR_OF_DAY) < calendar.get(Calendar.HOUR_OF_DAY) ||
//                                    (start_calender.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY) &&
//                                            start_calender.get(Calendar.MINUTE) < calendar.get(Calendar.MINUTE)))
                // start else if
                title_input_layout.setError(null);

                startLineError.setImageResource(R.drawable.line_red);
                Toast.makeText(getContext(), "Start time should be at least at " + calendar.get(Calendar.HOUR) + ":" + (calendar.get(Calendar.MINUTE) + 1 < 10 ? "0" + (calendar.get(Calendar.MINUTE) + 1) : (calendar.get(Calendar.MINUTE) + 1)) + (calendar.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM"), Toast.LENGTH_LONG).show();
//                   TO DONE open dialog to choice start time
                choice_start_time();
            } else if (end_calendar.before(calender_half_hour)) {
//                    (end_calendar.get(Calendar.HOUR_OF_DAY) < calender_half_hour.get(Calendar.HOUR_OF_DAY) ||
//                            (end_calendar.get(Calendar.HOUR_OF_DAY) == calender_half_hour.get(Calendar.HOUR_OF_DAY) && end_calendar.get(Calendar.MINUTE) < calender_half_hour.get(Calendar.MINUTE)))) {

                //&& end_calendar.get(Calendar.AM_PM)==calender_half_hour.get(Calendar.AM_PM)
                Log.d(TAG, "#" + end_calendar.get(Calendar.DAY_OF_MONTH));
                Log.d(TAG, "##" + calender_half_hour.get(Calendar.DAY_OF_MONTH));

                title_input_layout.setError(null);
                startLineError.setImageResource(R.drawable.line);

                endLineError.setImageResource(R.drawable.line_red);
                Toast.makeText(getContext(), "End time should be at least at " + calender_half_hour.get(Calendar.HOUR) + ":" + (calender_half_hour.get(Calendar.MINUTE) + 1 < 10 ? "0" + (calender_half_hour.get(Calendar.MINUTE) + 1) : (calender_half_hour.get(Calendar.MINUTE) + 1)) + (calender_half_hour.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM"), Toast.LENGTH_LONG).show();
//                   TO DONE open dialog to choice end time
                choice_end_time();
            } else if (Objects.requireNonNull(description.getEditText()).getText().toString().trim().isEmpty()) {

                title_input_layout.setError(null);
                startLineError.setImageResource(R.drawable.line);
                endLineError.setImageResource(R.drawable.line);

                description.setError("Can't be empty");
                description.requestFocus();
                open_keyboard(description.getEditText());
            } else if (chip == null) {

                title_input_layout.setError(null);
                startLineError.setImageResource(R.drawable.line);
                endLineError.setImageResource(R.drawable.line);
                description.setError(null);

                Toast.makeText(getContext(), "should select Category !", Toast.LENGTH_LONG).show();
            } else if (checkTask != null) {

                title_input_layout.setError(null);
                startLineError.setImageResource(R.drawable.line);
                endLineError.setImageResource(R.drawable.line);
                description.setError(null);

                Toast.makeText(getContext(), "this task time is interrupt with " + checkTask.getTask_name() + " task time", Toast.LENGTH_SHORT).show();
            } else if (!sleep1 && start_calender.get(Calendar.HOUR_OF_DAY) < 7) {
                // you should be sleep
                shouldSleep("start");
            } else if (!sleep2 && end_calendar.get(Calendar.HOUR_OF_DAY) < 7) {
                // you should be sleep
                shouldSleep("end");
            } else {

                title_input_layout.setError(null);
                startLineError.setImageResource(R.drawable.line);
                endLineError.setImageResource(R.drawable.line);
                description.setError(null);

                String taskName = title_input_layout.getEditText().getText().toString().trim(),
                        taskDescription = description.getEditText().getText().toString().trim(),
                        projectName = chip.getText().toString().trim();

                Log.w("Lol2", "todo size before addTask " + ToDo_tasks.size());
                add_task(projectName, taskName, start_calender, end_calendar, taskDescription);
                Toast.makeText(getContext(), "Done \uD83D\uDE0C♥️", Toast.LENGTH_SHORT).show();
                Log.w("Lol2", "todo size before upload " + ToDo_tasks.size());
                uploadProject(currentUser);
                Log.w("Lol2", "todo size after upload " + ToDo_tasks.size());
                unSavedTask.setEnd_calendar(null);
                unSavedTask.setStart_calendar(null);
                unSavedTask.setProjectName(null);
                unSavedTask.setTask_description(null);
                unSavedTask.setTask_name(null);
                DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
                DbRef.child(currentUser.getUid() + "/UnSavedTask").removeValue();
                init();
                Log.w("Lol2", "todo size after init " + ToDo_tasks.size());
                Log.w("Lol2", "-----------------createNew , End of create !----------------");
            }
        });

        // for unsaved task
        title_input_layout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (unSavedTask != null) {
                    unSavedTask.setTask_name(s.toString().trim());
                    uploadUnSavedTask();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        description.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (unSavedTask != null) {
                    unSavedTask.setTask_description(s.toString().trim());
                    uploadUnSavedTask();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        projects_chips.setOnCheckedChangeListener((chipGroup, i) -> {
            if (unSavedTask != null && i >=0 && i<Projects.size()) {
                unSavedTask.setProjectName(Projects.get(i).getTitle());
                uploadUnSavedTask();
            }
        });

        return view;
    }

    private void uploadUnSavedTask() {
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/UnSavedTask/").setValue(unSavedTask);
    }

    private void retrieveUnSavedTask() {

        UnSavedTaskViewModel unSavedTaskViewModel = new ViewModelProvider(this).get(UnSavedTaskViewModel.class);
        unSavedTaskViewModel.getUnSavedTask(currentUser);
        unSavedTaskViewModel.TaskMutableLiveData.observe(getActivity(), task -> {
            unSavedTask = task;
            if (unSavedTask != null) {
                if (unSavedTask.getTask_name() != null)
                    title_input_layout.getEditText().setText(unSavedTask.getTask_name());
                if (unSavedTask.getTask_description() != null)
                    description.getEditText().setText(unSavedTask.getTask_description());
                if (unSavedTask.getProjectName() != null)
                    selectChip(unSavedTask.getProjectName());
                if (unSavedTask.getStart_calendar() != null && !((Calendar) StringToCalendar(unSavedTask.getStart_calendar()).clone()).before(Calendar.getInstance())) {
                    calendar_selected_day = (Calendar) StringToCalendar(unSavedTask.getStart_calendar()).clone();
                    String thisday = days[calendar_selected_day.get(Calendar.DAY_OF_WEEK) - 1] + ", " + calendar_selected_day.get(Calendar.DAY_OF_MONTH) + " " + months[calendar_selected_day.get(Calendar.MONTH)];
                    Objects.requireNonNull(date.getEditText()).setText(thisday);

                    start_calender = (Calendar) StringToCalendar(unSavedTask.getStart_calendar()).clone();
                    start_time_tv.setText((start_calender.get(Calendar.HOUR) == 0 ? "12" : start_calender.get(Calendar.HOUR)) + ":" + (start_calender.get(Calendar.MINUTE) < 10 ? "0" + start_calender.get(Calendar.MINUTE) : start_calender.get(Calendar.MINUTE)));
                    start_am_pm.setText(start_calender.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                }
                if (unSavedTask.getEnd_calendar() != null && !((Calendar) StringToCalendar(unSavedTask.getStart_calendar()).clone()).before(Calendar.getInstance())) {
                    end_calendar = (Calendar) StringToCalendar(unSavedTask.getEnd_calendar()).clone();
                    end_time_tv.setText((end_calendar.get(Calendar.HOUR) == 0 ? "12" : end_calendar.get(Calendar.HOUR)) + ":" + (end_calendar.get(Calendar.MINUTE) < 10 ? "0" + end_calendar.get(Calendar.MINUTE) : end_calendar.get(Calendar.MINUTE)));
                    end_am_pm.setText(end_calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                }
            }
        });


    }

    private void selectChip(String project_name) {
        Collections.sort(Projects, Activeprojects.projectsComparator);
        for (int i = 0; i < Projects.size(); i++)
            if (Projects.get(i).getTitle().equals(project_name)) {
                projects_chips.check(i);
                return;
            }
    }

    static void uploadProject(FirebaseUser currentUser) {
        Log.w("Lol2", "CreateNewTask , uploadProjects , projects.size :" + Projects.size() + "\n");
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Projects/").setValue(Projects);
        Log.w("Lol2", "-----------------create , End of upload projects-------------------");
    }


//    public static void uploadTask(ArrayList<Tasks> tasks, String name, FirebaseUser currentUser) {
//
//        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
//
//        for (int i = 0; i < tasks.size(); i++) {
//            Tasks task = tasks.get(i);
//            DbRef.child(currentUser.getUid() + "/" + name + "/" + i + "/projectName").setValue(task.getProjectName());
//            DbRef.child(currentUser.getUid() + "/" + name + "/" + i + "/task_name").setValue(task.getTask_name());
//            DbRef.child(currentUser.getUid() + "/" + name + "/" + i + "/task_description").setValue(task.getTask_description());
//            DbRef.child(currentUser.getUid() + "/" + name + "/" + i + "/toindone").setValue(task.getToindone());
//            Calendar start = (Calendar) StringToCalendar(task.getStart_calendar()).clone();
//            String start_time = start.get(Calendar.YEAR) + "," + start.get(Calendar.MONTH) + "," + start.get(Calendar.DAY_OF_MONTH) + "," + start.get(Calendar.HOUR_OF_DAY) + "," +
//                    start.get(Calendar.MINUTE) + "," + start.get(Calendar.AM_PM);
//            Calendar end = (Calendar) StringToCalendar(task.getEnd_calendar()).clone();
//            String end_time = end.get(Calendar.YEAR) + "," + end.get(Calendar.MONTH) + "," + end.get(Calendar.DAY_OF_MONTH) + "," + end.get(Calendar.HOUR_OF_DAY) + "," +
//                    end.get(Calendar.MINUTE) + "," + end.get(Calendar.AM_PM);
//            DbRef.child(currentUser.getUid() + "/" + name + "/" + i + "/start_calendar").setValue(start_time);
//            DbRef.child(currentUser.getUid() + "/" + name + "/" + i + "/end_calendar").setValue(end_time);
//
////            Log.w("Lol2","CreateNewTask , uploadTask , tasks.size : "+tasks.size()+"    #/n#    "+tasks.get(i).getTask_name()+"\n"+
////            start_time + "     ||    "+end_time);
//            Log.w("Lol2", "---" + tasks.get(i).getTask_name() + ", begin at :" + start_time + ",and end at :" + end_time + ".   j = " + i);
//
//        }
//        Log.w("Lol2", "CreateNewTask , upload" + name + "Tasks , tasks.size :" + tasks.size() + "\n");
//        Log.w("Lol2", "----------------create , end of upload task----------------");
//    }

    private void init() {
        /// calculate date today
        calendar_selected_day = Calendar.getInstance();
        String today = days[calendar_selected_day.get(Calendar.DAY_OF_WEEK) - 1] +
                ", " + calendar_selected_day.get(Calendar.DAY_OF_MONTH) + " " + months[calendar_selected_day.get(Calendar.MONTH)];
        Objects.requireNonNull(date.getEditText()).setText(today);
        date.getEditText().setEnabled(false);

        // calculate the next hour and the next hour+2 and put them in the text view
        calendar_selected_day.add(Calendar.HOUR, 1);
        calendar_selected_day.set(Calendar.MINUTE, 0);
        String next_hour = (calendar_selected_day.get(Calendar.HOUR) == 0 ? "12" : calendar_selected_day.get(Calendar.HOUR)) + ":00";
        start_time_tv.setText(next_hour);
        if (calendar_selected_day.get(Calendar.AM_PM) == Calendar.AM)
            start_am_pm.setText("AM");
        else
            start_am_pm.setText("PM");

        start_calender = (Calendar) calendar_selected_day.clone();

        calendar_selected_day.add(Calendar.HOUR, 2);
        calendar_selected_day.set(Calendar.MINUTE, 0);
        next_hour = (calendar_selected_day.get(Calendar.HOUR) == 0 ? "12" : calendar_selected_day.get(Calendar.HOUR)) + ":00";
        end_time_tv.setText(next_hour);
        if (calendar_selected_day.get(Calendar.AM_PM) == Calendar.AM)
            end_am_pm.setText("AM");
        else
            end_am_pm.setText("PM");

        end_calendar = (Calendar) calendar_selected_day.clone();

        calendar_selected_day = Calendar.getInstance();
        Objects.requireNonNull(title_input_layout.getEditText()).setText("");
        Objects.requireNonNull(description.getEditText()).setText("");
        projects_chips.clearCheck();

        retrieveUnSavedTask();
    }

    private void choice_start_time() {
        choice_time("start");
    }

    private void choice_end_time() {
        choice_time("end");
    }

    private void choice_time(final String time) {
        close_keyboard();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        calendar_selected_day.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
        calendar_selected_day.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendar_selected_day.set(Calendar.AM_PM, calendar.get(Calendar.AM_PM));
        start_calender.set(Calendar.YEAR, calendar_selected_day.get(Calendar.YEAR));
        start_calender.set(Calendar.MONTH, calendar_selected_day.get(Calendar.MONTH));
        start_calender.set(Calendar.DAY_OF_MONTH, calendar_selected_day.get(Calendar.DAY_OF_MONTH));

        int Chour = time.equals("start") ? start_calender.get(Calendar.HOUR) : end_calendar.get(Calendar.HOUR);
        int Cminute = time.equals("start") ? start_calender.get(Calendar.MINUTE) : end_calendar.get(Calendar.MINUTE);
        if ((time.equals("start") && start_calender.get(Calendar.AM_PM) == Calendar.PM) || (time.equals("end") && end_calendar.get(Calendar.AM_PM) == Calendar.PM))
            Chour += 12;
        TimePickerDialog pickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            calendar_selected_day.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar_selected_day.set(Calendar.MINUTE, minute);


            if (hourOfDay < 7) {
                // you should be sleep
                shouldSleep(time);
            }
            if (time.equals("start")) {
                start_calender = (Calendar) calendar_selected_day.clone();
                if (unSavedTask != null) {
                    unSavedTask.setStart_calendar(CalendarToString(start_calender));
                    uploadUnSavedTask();
                }
                // TO DONE take start time from here

                start_time_tv.setText((start_calender.get(Calendar.HOUR) == 0 ? "12" : start_calender.get(Calendar.HOUR)) + ":" + (start_calender.get(Calendar.MINUTE) < 10 ? "0" + start_calender.get(Calendar.MINUTE) : start_calender.get(Calendar.MINUTE)));
                if (hourOfDay < 12) {
                    start_am_pm.setText("AM");
                    start_calender.set(Calendar.AM_PM, Calendar.AM);
                } else {
                    start_am_pm.setText("PM");
                    start_calender.set(Calendar.AM_PM, Calendar.PM);
                }


            } else if (time.equals("end")) {
                end_calendar = (Calendar) calendar_selected_day.clone();
                if (unSavedTask != null) {
                    unSavedTask.setEnd_calendar(CalendarToString(end_calendar));
                    uploadUnSavedTask();
                }
                // TO DONe take start time from here

                end_time_tv.setText((end_calendar.get(Calendar.HOUR) == 0 ? "12" : end_calendar.get(Calendar.HOUR)) + ":" + (end_calendar.get(Calendar.MINUTE) < 10 ? "0" + end_calendar.get(Calendar.MINUTE) : end_calendar.get(Calendar.MINUTE)));
                if (hourOfDay < 12) {
                    end_am_pm.setText("AM");
                    end_calendar.set(Calendar.AM_PM, Calendar.AM);
                } else {
                    end_am_pm.setText("PM");
                    end_calendar.set(Calendar.AM_PM, Calendar.PM);
                }

                Log.d(TAG, "<" + start_calender.get(Calendar.DAY_OF_MONTH));
                Log.d(TAG, "/>" + end_calendar.get(Calendar.DAY_OF_MONTH));


            }

        }, Chour, Cminute, false);

        //getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        pickerDialog.show();
    }

    private void shouldSleep(final String time) {
        builder = new AlertDialog.Builder(getContext());

        View sleep_dialog = getLayoutInflater().inflate(R.layout.sleep_dialog, null);

        TextView ok = sleep_dialog.findViewById(R.id.ok_dialog),
                cancel = sleep_dialog.findViewById(R.id.cancel_dialog),
                sl = sleep_dialog.findViewById(R.id.sltxt);

        cancel.setText(R.string.ignore_sleep);
        sl.setText(R.string.ok_sleep);


        cancel.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ok️\uD83D\uDE25", Toast.LENGTH_LONG).show();
            if (time.equals("start"))
                sleep1 = true;
            else sleep2 = true;
            dialog.dismiss();
        });

        ok.setOnClickListener(v -> {

            Toast.makeText(getContext(), "Love you ,babe\uD83D\uDE0D\uD83D\uDE48\uD83D\uDE48♥️", Toast.LENGTH_LONG).show();
            if (time.equals("start")) choice_start_time();
            else choice_end_time();

            dialog.dismiss();
        });

        builder.setView(sleep_dialog).setCancelable(false);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void choice_date() {
        close_keyboard();
        int year = calendar_selected_day.get(Calendar.YEAR);
        int month = calendar_selected_day.get(Calendar.MONTH);
        int day = calendar_selected_day.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar_selected_day.set(Calendar.YEAR, year);
                calendar_selected_day.set(Calendar.MONTH, month);
                calendar_selected_day.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // TO DONE get the date from here
                start_calender.set(Calendar.YEAR, year);
                start_calender.set(Calendar.MONTH, month);
                start_calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                end_calendar.set(Calendar.YEAR, year);
                end_calendar.set(Calendar.MONTH, month);
                end_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (unSavedTask != null) {
                    unSavedTask.setStart_calendar(CalendarToString(start_calender));
                    uploadUnSavedTask();
                }

                String thisday = days[calendar_selected_day.get(Calendar.DAY_OF_WEEK) - 1] + ", " + calendar_selected_day.get(Calendar.DAY_OF_MONTH) + " " + months[calendar_selected_day.get(Calendar.MONTH)];
                Objects.requireNonNull(date.getEditText()).setText(thisday);

            }
        }, year, month, day);
        pickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        pickerDialog.show();
    }

    private void add_project_chip_dialog() {
        builder = new AlertDialog.Builder(getContext());

        View new_chip_dialog = getLayoutInflater().inflate(R.layout.add_new_chip, null);

        final TextInputLayout title = new_chip_dialog.findViewById(R.id.new_chip_name);

        TextView ok = new_chip_dialog.findViewById(R.id.ok_dialog),
                cancel = new_chip_dialog.findViewById(R.id.cancel_dialog);

        title.requestFocus();
        open_keyboard(Objects.requireNonNull(title.getEditText()));

        cancel.setOnClickListener(v -> dialog.dismiss());

        ok.setOnClickListener(v -> {

            String name = title.getEditText().getText().toString().trim();
            if (check_project_is_exist(name) != null)
                title.setError("this Project already exist");
            else if (name.isEmpty())
                title.setError("can't be empty");
            else {

                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.choice_chip, projects_chips, false);
                chip.setText(name);
                chip.setId((projects_chips.getChildCount() == 0 ? 0 : projects_chips.getChildCount() - 1));
                chip.setCheckable(true);
                chip.setSelected(true);
                projects_chips.addView(chip, projects_chips.getChildCount() == 0 ? 0 : projects_chips.getChildCount() - 1);
                projects_chips.clearCheck();
                projects_chips.check((projects_chips.getChildCount() == 0 ? 0 : projects_chips.getChildCount() - 1));
                Activeprojects project = new Activeprojects(0, name, "0 hours progress");
                if (!Projects.contains(project))
                    Projects.add(project);
                Log.w("Lol2", "project added , projects.size : " + Projects.size() + "project1 has " + Projects.get(0).getProject_tasks().size() + " tasks!!!");
//                    adapter.notifyDataSetChanged();
                uploadProject(currentUser);
                Log.w("Lol2", "create , add projects , end of fun");
                dialog.dismiss();
            }

        });

        builder.setView(new_chip_dialog).setCancelable(false);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void add_exist_chips() {
        Collections.sort(Projects, Activeprojects.projectsComparator);
        for (int i = 0; i < Projects.size(); i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.choice_chip, projects_chips, false);
            chip.setText(Projects.get(i).getTitle());
            chip.setId(i);
            chip.setCheckable(true);
            projects_chips.addView(chip, projects_chips.getChildCount() - 1);
        }
        projects_chips.setSingleSelection(true);

    }

    public static Activeprojects check_project_is_exist(String projectName) {
        for (int i = 0; i < Projects.size(); i++) {
            if (Projects.get(i).getTitle().trim().toLowerCase().equals(projectName.trim().toLowerCase())) {
                return Projects.get(i);
            }
        }
        Log.w("NULL", "null" + Projects.size());
        return null;
    }

    private Tasks check_if_there_task_at_this_time(Calendar start, Calendar end) {

        Log.w("Lol2", "-" + start.get(Calendar.YEAR) + "/" + start.get(Calendar.MONTH) + "/" + start.get(Calendar.DAY_OF_MONTH) + "  " + start.get(Calendar.HOUR_OF_DAY) + ":" + start.get(Calendar.MINUTE) + " " + start.get(Calendar.AM_PM) + "\n-" +
                end.get(Calendar.YEAR) + "/" + end.get(Calendar.MONTH) + "/" + end.get(Calendar.DAY_OF_MONTH) + "  " + end.get(Calendar.HOUR_OF_DAY) + ":" + end.get(Calendar.MINUTE) + " " + end.get(Calendar.AM_PM));

        for (int i = 0; i < Projects.size(); i++)
            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++) {
                Calendar exist_task_start_calendar = (Calendar) StringToCalendar(Projects.get(i).getProject_tasks().get(j).getStart_calendar()).clone(),
                        exist_task_end_calendar = (Calendar) StringToCalendar(Projects.get(i).getProject_tasks().get(j).getEnd_calendar()).clone();

                exist_task_start_calendar.add(Calendar.MINUTE, 1);
                exist_task_end_calendar.add(Calendar.MINUTE, -1);

                if ((start.after(exist_task_start_calendar) && start.before(exist_task_end_calendar)) || (end.after(exist_task_start_calendar) && end.before(exist_task_end_calendar)) || (exist_task_start_calendar.after(start) && exist_task_end_calendar.before(end))) {

                    exist_task_start_calendar.add(Calendar.MINUTE, -1);
                    exist_task_end_calendar.add(Calendar.MINUTE, 1);

                    return Projects.get(i).getProject_tasks().get(j);
                }

                exist_task_start_calendar.add(Calendar.MINUTE, -1);
                exist_task_end_calendar.add(Calendar.MINUTE, 1);


                Log.w("Lol2", "----" + exist_task_start_calendar.get(Calendar.YEAR) + "/" + exist_task_start_calendar.get(Calendar.MONTH) + "/" + exist_task_start_calendar.get(Calendar.DAY_OF_MONTH) + "  " + exist_task_start_calendar.get(Calendar.HOUR_OF_DAY) + ":" + exist_task_start_calendar.get(Calendar.MINUTE) + " " + exist_task_start_calendar.get(Calendar.AM_PM) + "\n----" +
                        exist_task_end_calendar.get(Calendar.YEAR) + "/" + exist_task_end_calendar.get(Calendar.MONTH) + "/" + exist_task_end_calendar.get(Calendar.DAY_OF_MONTH) + "  " + exist_task_end_calendar.get(Calendar.HOUR_OF_DAY) + ":" + exist_task_end_calendar.get(Calendar.MINUTE) + " " + exist_task_end_calendar.get(Calendar.AM_PM));

            }

        return null;
    }

    public static String CalendarToString(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," +
                calendar.get(Calendar.DAY_OF_MONTH) + "," + calendar.get(Calendar.HOUR) + "," +
                calendar.get(Calendar.MINUTE) + "," + calendar.get(Calendar.AM_PM);
    }

    public static Calendar StringToCalendar(String start) {

        String[] startArr = start.split(",");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(startArr[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(startArr[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startArr[2]));
        calendar.set(Calendar.HOUR, Integer.parseInt(startArr[3]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(startArr[4]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM, Integer.parseInt(startArr[5]));


        Log.w("Log", "Hour : " + Integer.parseInt(startArr[3]) + ", Minute : " + Integer.parseInt(startArr[4]));
        Log.w("Log", "Hour : " + calendar.get(Calendar.HOUR_OF_DAY) + ", Minute : " + calendar.get(Calendar.MINUTE) + "!!");
        Log.w("Log", "StringToCal , time : \n" + start + "\n" + CalendarToString(calendar));
        return calendar;
    }

    private void add_task(String projectName, String task_name, Calendar
            start_calendar, Calendar end_calendar, String task_description) {

        Tasks task = new Tasks(projectName, task_name, CalendarToString(start_calendar), CalendarToString(end_calendar), task_description);

//        Log.w("Lol2", "add " + task_name + " task, in " + projectName + "project, that has " + Objects.requireNonNull(check_project_is_exist(projectName)) + " tasks \n start time : " + CalendarToString(start_calendar) + "\nend time : " + CalendarToString(end_calendar));
        if (!Objects.requireNonNull(check_project_is_exist(projectName)).getProject_tasks().contains(task))
            Objects.requireNonNull(check_project_is_exist(projectName)).getProject_tasks().add(task);

        if (!ToDo_tasks.contains(task)) {
            ToDo_tasks.add(task);
            Log.w("Lol2", "task add " + ToDo_tasks.size());
        }
        Log.w("Lol2", "end of adding " + task_name + " task, in " + projectName + "project, that has " + Objects.requireNonNull(check_project_is_exist(projectName)).getProject_tasks().size() + " tasks now.");

//        getToInDone(this);
    }

    void open_keyboard(EditText textInputLayout) {
        textInputLayout.requestFocus();     // editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
        assert imm != null;
        imm.showSoftInput(textInputLayout, InputMethodManager.SHOW_IMPLICIT); //    first param -> editText

    }

    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}