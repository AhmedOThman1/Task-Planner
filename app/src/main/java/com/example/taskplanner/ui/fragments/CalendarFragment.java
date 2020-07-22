package com.example.taskplanner.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.RecyclerViewTouchListener;
import com.example.taskplanner.adapter.CalendarDaysRecyclerViewAdapter;
import com.example.taskplanner.model.Tasks;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.CalendarToString;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.months;
import static com.example.taskplanner.ui.fragments.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.activities.Launcher.SHARED_PREF;
import static com.example.taskplanner.ui.activities.Launcher.USER_NAME;
import static com.example.taskplanner.ui.activities.MainActivity.Projects;
import static com.example.taskplanner.ui.activities.MainActivity.hideNav;
import static com.example.taskplanner.ui.activities.MainActivity.open_drawer;
import static com.example.taskplanner.ui.activities.MainActivity.setCheck;
import static com.example.taskplanner.ui.activities.MainActivity.showNav;

public class CalendarFragment extends Fragment {


    CalendarDaysRecyclerViewAdapter daysadapter;
    CollapsingToolbarLayout coolToolbar;
    TextView addTask, hello, mon_year, oldName = null, oldNum = null;
    RecyclerView calendar_days_recyclerview;
    Calendar calendar;
    ArrayList<ImageView> hours_arr_iv = new ArrayList<>();
    ArrayList<TextView> hours_arr_tv = new ArrayList<>();
    RelativeLayout relativeLayout;
    ArrayList<RelativeLayout> thisDayTasks = new ArrayList<>();
    ArrayList<Calendar> DaysCalendar = new ArrayList<>();
    int relCount = 0, oldPos = 0;
    public static int last_selected_day = 0;
    public static String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow));
        }
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
        coolToolbar = view.findViewById(R.id.coolToolbar);
        calendar_days_recyclerview = view.findViewById(R.id.calRecyclerView);
        addTask = view.findViewById(R.id.add_task);
        hello = view.findViewById(R.id.hello_name);
        mon_year = view.findViewById(R.id.mon_year);
        relativeLayout = view.findViewById(R.id.tasks_time_container);

        SharedPreferences share = getContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String[] name = share.getString(USER_NAME, "").split(" ");
        String fname = "Productive Day, " + name[0];
        hello.setText(fname);

        calendar = Calendar.getInstance();
        mon_year.setText(months[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR));

        int cnt = 0;
        last_selected_day = 0;
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        while (true) {
            Calendar temp = Calendar.getInstance();
            temp.add(Calendar.DATE, cnt);
            DaysCalendar.add(temp);
            cnt++;
            if (temp.after(nextYear))
                break;
        }

        daysadapter = new CalendarDaysRecyclerViewAdapter(getActivity(), DaysCalendar);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);

        calendar_days_recyclerview.setAdapter(daysadapter);
        calendar_days_recyclerview.setLayoutManager(manager);
        calendar_days_recyclerview.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), calendar_days_recyclerview, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                mon_year.setText(months[DaysCalendar.get(position).get(Calendar.MONTH)] + ", " + DaysCalendar.get(position).get(Calendar.YEAR));
                for (int i = 0; i < thisDayTasks.size(); i++) {
                    relativeLayout.removeView(thisDayTasks.get(i));
                }
                thisDayTasks.clear();
                DrawTasks(DaysCalendar.get(position));
//                Toast.makeText(getContext(), "" + months[DaysCalendar.get(position).get(Calendar.MONTH)] + ", " + DaysCalendar.get(position).get(Calendar.YEAR) + "\n" + days[DaysCalendar.get(position).get(Calendar.DAY_OF_WEEK) - 1] + " , " + DaysCalendar.get(position).get(Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
                TextView name = view.findViewById(R.id.day_name),
                        num = view.findViewById(R.id.day_num);

                if (name != null && num != null) {

                    if (oldName == null) oldName = name;
                    if (oldNum == null) oldNum = num;

                    if (oldNum != num && oldPos != 0)
                        oldNum.setTextColor(getResources().getColor(R.color.black));
                    if (oldName != name && oldPos != 0)
                        oldName.setTextColor(getResources().getColor(R.color.defualt_textview_color));

                    if (position != 0) {
                        name.setTextColor(getResources().getColor(R.color.zeti));
                        num.setTextColor(getResources().getColor(R.color.zeti));
                    }

                    last_selected_day = position;
                    oldName = name;
                    oldNum = num;
                    oldPos = position;
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        AppBarLayout appBarLayout = view.findViewById(R.id.AppBarL);

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, i) -> {
            if (Math.abs(i) == appBarLayout1.getTotalScrollRange()) {
                coolToolbar.setTitle("Calendar");
                appBarLayout1.setBackgroundResource(R.drawable.background_toolbar2);
            } else if (Math.abs(i) < (appBarLayout1.getTotalScrollRange()) / 2) {
                coolToolbar.setTitle("");
                appBarLayout1.setBackgroundResource(R.drawable.background_toolbar);
            } else {
                coolToolbar.setTitle("Calendar");
                appBarLayout1.setBackgroundResource(R.drawable.background_toolbar);
            }
        });

        addTask.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new CreateNewTaskFragment()).commit();
            setCheck(2, false, getContext());
        });

        hours_arr_tv.add(view.findViewById(R.id.h12AM));
        hours_arr_tv.add(view.findViewById(R.id.h1AM));
        hours_arr_tv.add(view.findViewById(R.id.h2AM));
        hours_arr_tv.add(view.findViewById(R.id.h3AM));
        hours_arr_tv.add(view.findViewById(R.id.h4AM));
        hours_arr_tv.add(view.findViewById(R.id.h5AM));
        hours_arr_tv.add(view.findViewById(R.id.h6AM));
        hours_arr_tv.add(view.findViewById(R.id.h7AM));
        hours_arr_tv.add(view.findViewById(R.id.h8AM));
        hours_arr_tv.add(view.findViewById(R.id.h9AM));
        hours_arr_tv.add(view.findViewById(R.id.h10AM));
        hours_arr_tv.add(view.findViewById(R.id.h11AM));
        hours_arr_tv.add(view.findViewById(R.id.h12PM));
        hours_arr_tv.add(view.findViewById(R.id.h1PM));
        hours_arr_tv.add(view.findViewById(R.id.h2PM));
        hours_arr_tv.add(view.findViewById(R.id.h3PM));
        hours_arr_tv.add(view.findViewById(R.id.h4PM));
        hours_arr_tv.add(view.findViewById(R.id.h5PM));
        hours_arr_tv.add(view.findViewById(R.id.h6PM));
        hours_arr_tv.add(view.findViewById(R.id.h7PM));
        hours_arr_tv.add(view.findViewById(R.id.h8PM));
        hours_arr_tv.add(view.findViewById(R.id.h9PM));
        hours_arr_tv.add(view.findViewById(R.id.h10PM));
        hours_arr_tv.add(view.findViewById(R.id.h11PM));


        hours_arr_iv.add(view.findViewById(R.id.d12AM));
        hours_arr_iv.add(view.findViewById(R.id.d1AM));
        hours_arr_iv.add(view.findViewById(R.id.d2AM));
        hours_arr_iv.add(view.findViewById(R.id.d3AM));
        hours_arr_iv.add(view.findViewById(R.id.d4AM));
        hours_arr_iv.add(view.findViewById(R.id.d5AM));
        hours_arr_iv.add(view.findViewById(R.id.d6AM));
        hours_arr_iv.add(view.findViewById(R.id.d7AM));
        hours_arr_iv.add(view.findViewById(R.id.d8AM));
        hours_arr_iv.add(view.findViewById(R.id.d9AM));
        hours_arr_iv.add(view.findViewById(R.id.d10AM));
        hours_arr_iv.add(view.findViewById(R.id.d11AM));
        hours_arr_iv.add(view.findViewById(R.id.d12PM));
        hours_arr_iv.add(view.findViewById(R.id.d1PM));
        hours_arr_iv.add(view.findViewById(R.id.d2PM));
        hours_arr_iv.add(view.findViewById(R.id.d3PM));
        hours_arr_iv.add(view.findViewById(R.id.d4PM));
        hours_arr_iv.add(view.findViewById(R.id.d5PM));
        hours_arr_iv.add(view.findViewById(R.id.d6PM));
        hours_arr_iv.add(view.findViewById(R.id.d7PM));
        hours_arr_iv.add(view.findViewById(R.id.d8PM));
        hours_arr_iv.add(view.findViewById(R.id.d9PM));
        hours_arr_iv.add(view.findViewById(R.id.d10PM));
        hours_arr_iv.add(view.findViewById(R.id.d11PM));


        for (int i = 1; i <= 6; i++) {
            hours_arr_tv.get(i).setVisibility(View.GONE);
            hours_arr_iv.get(i).setVisibility(View.GONE);
        }

        DrawTasks(Calendar.getInstance());

        return view;
    }

    void DrawTasks(Calendar now) {
        Calendar yesterday = (Calendar) now.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        boolean thereAreTasksBefore7AM = false;
        for (int i = 0; i < Projects.size(); i++)
            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++) {

                Calendar start_calendar = (Calendar) StringToCalendar(Projects.get(i).getProject_tasks().get(j).getStart_calendar()).clone(),
                        end_calendar = (Calendar) StringToCalendar(Projects.get(i).getProject_tasks().get(j).getEnd_calendar()).clone(), temp;

                Log.w("Log", "CalAct , start_time : " + CalendarToString(start_calendar) + "\tend_time : " + CalendarToString(end_calendar));
                temp = (Calendar) end_calendar.clone();
                temp.set(Calendar.HOUR_OF_DAY, 0);
                temp.set(Calendar.MINUTE, 0);
                temp.set(Calendar.SECOND, 0);
                temp.set(Calendar.MILLISECOND, 0);

                if (start_calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        start_calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        start_calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {

                    long difference = end_calendar.getTimeInMillis() - start_calendar.getTimeInMillis();
                    int minutes = (int) (difference / (1000 * 60));
                    if (start_calendar.get(Calendar.HOUR_OF_DAY) < 7) {
                        Log.d("Lol2", "#" + start_calendar.get(Calendar.HOUR_OF_DAY) + "#");
                        for (int k = start_calendar.get(Calendar.HOUR_OF_DAY); k <= 6; k++) {
                            hours_arr_tv.get(k).setVisibility(View.VISIBLE);
                            hours_arr_iv.get(k).setVisibility(View.VISIBLE);
                            Log.w("Lol2", "calender V" + k + "V");
                        }
                        for (int k = start_calendar.get(Calendar.HOUR_OF_DAY) - 1; k >= 0; k--) {
                            hours_arr_tv.get(k).setVisibility(View.GONE);
                            hours_arr_iv.get(k).setVisibility(View.GONE);
                            Log.w("Lol2", "calendar G" + k + "G");

                        }
                        thereAreTasksBefore7AM = true;
                    }

                    RelativeLayout newRelative = (RelativeLayout) getLayoutInflater().inflate(R.layout.task_card, null);
                    //new RelativeLayout(this);

                    TextView task_title = newRelative.findViewById(R.id.task_title),
                            task_description = newRelative.findViewById(R.id.task_description);

                    task_title.setText(Projects.get(i).getProject_tasks().get(j).getTask_name());
                    task_description.setText(Projects.get(i).getProject_tasks().get(j).getTask_description());

                    if (start_calendar.get(Calendar.AM_PM) == Calendar.PM && end_calendar.get(Calendar.AM_PM) == Calendar.AM) {
//                        Log.d(TAG,(int) (temp.getTimeInMillis() - start_calendar.getTimeInMillis() / (1000 * 60))+"!!!");
//
//                        Log.d(TAG,( (24%start_calendar.get(Calendar.HOUR_OF_DAY))*60 - start_calendar.get(Calendar.MINUTE) )+"!@!");
                        minutes = ((24 % start_calendar.get(Calendar.HOUR_OF_DAY)) * 60 - start_calendar.get(Calendar.MINUTE));
                        if (relCount % 4 == 0)
                            newRelative.setBackgroundResource(R.drawable.background_zetisq12);
                        else if (relCount % 4 == 1)
                            newRelative.setBackgroundResource(R.drawable.background_redsq12);
                        else if (relCount % 4 == 2)
                            newRelative.setBackgroundResource(R.drawable.background_yellowsq12);
                        else if (relCount % 4 == 3)
                            newRelative.setBackgroundResource(R.drawable.background_bluesq12);
                    } else {
//                        Log.d(TAG,minutes+"!");
                        if (relCount % 4 == 0)
                            newRelative.setBackgroundResource(R.drawable.background_zetisq);
                        else if (relCount % 4 == 1)
                            newRelative.setBackgroundResource(R.drawable.background_redsq);
                        else if (relCount % 4 == 2)
                            newRelative.setBackgroundResource(R.drawable.background_yellowsq);
                        else if (relCount % 4 == 3)
                            newRelative.setBackgroundResource(R.drawable.background_bluesq);
                    }

                    if (minutes <= 60) {

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) task_title.getLayoutParams();
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        task_title.setLayoutParams(params);
                        task_description.setVisibility(View.GONE);
                        task_title.setEllipsize(TextUtils.TruncateAt.END);
                        task_title.setMaxLines(1);
                    } else {
                        task_description.setEllipsize(TextUtils.TruncateAt.END);
                        task_description.setMaxLines(1);
                        task_title.setEllipsize(TextUtils.TruncateAt.END);
                        task_title.setMaxLines(1);
                    }

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (minutes * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));

                    relCount++;

                    newRelative.setId(relCount);

                    params.rightMargin = (int) (24 * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    params.leftMargin = (int) (24 * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    params.topMargin = (int) ((-50 + (start_calendar.get(Calendar.MINUTE))) * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                    params.addRule(RelativeLayout.END_OF, R.id.h7AM);
                    int id = hours_arr_tv.get(start_calendar.get(Calendar.HOUR_OF_DAY)).getId();
                    params.addRule(RelativeLayout.BELOW, id);
                    newRelative.setLayoutParams(params);

                    final int finalI = i;
                    final int finalJ = j;
                    newRelative.setOnClickListener(v -> {
                        Tasks task = Projects.get(finalI).getProject_tasks().get(finalJ);
//                        Toast.makeText(getContext(), "" + Projects.get(finalI).getProject_tasks().get(finalJ).getTask_name(), Toast.LENGTH_SHORT).show();
                        builder = new AlertDialog.Builder(getContext());
                        View task_dialog = getLayoutInflater().inflate(R.layout.fragment_one_task, null);
                        TextInputLayout taskName = task_dialog.findViewById(R.id.task_title),
                                projectName = task_dialog.findViewById(R.id.task_project_name),
                                date = task_dialog.findViewById(R.id.task_date),
                                start_time = task_dialog.findViewById(R.id.task_start_time),
                                end_time = task_dialog.findViewById(R.id.task_end_time),
                                description = task_dialog.findViewById(R.id.task_description);

                        taskName.getEditText().setText(task.getTask_name());
                        description.getEditText().setText(task.getTask_description());
                        projectName.getEditText().setText(task.getProjectName());
                        description.getEditText().setText(task.getTask_description());

                        Calendar start = (Calendar) StringToCalendar(task.getStart_calendar()).clone();
                        String today = start.get(Calendar.DAY_OF_MONTH) + "/" + start.get(Calendar.MONTH) + "/" + start.get(Calendar.YEAR);
                        date.getEditText().setText(today);
                        String s = "" + start.get(Calendar.HOUR) + ":" + (start.get(Calendar.MINUTE) < 10 ? "0" + start.get(Calendar.MINUTE) : start.get(Calendar.MINUTE)) + " "
                                + (start.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                        start_time.getEditText().setText(s);

                        Calendar end = (Calendar) StringToCalendar(task.getEnd_calendar()).clone();
                        String e = "" + end.get(Calendar.HOUR) + ":" + (end.get(Calendar.MINUTE) < 10 ? "0" + end.get(Calendar.MINUTE) : end.get(Calendar.MINUTE)) + " "
                                + (end.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                        end_time.getEditText().setText(e);

                        builder.setView(task_dialog);
                        dialog = builder.create();
                        dialog.show();
                        Window window = dialog.getWindow();
                        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                        assert window != null;
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    });

                    relativeLayout.addView(newRelative);
                    thisDayTasks.add(newRelative);

//                    Toast.makeText(this, "h: " + start_calendar.get(Calendar.HOUR_OF_DAY) + "\nm: " + minutes + "\nm%60: " + minutes % 60 + "\nid: " + id + "\n8id", Toast.LENGTH_SHORT).show();
                } else if (end_calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        end_calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        end_calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) &&
                        start_calendar.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH)) {

                    thereAreTasksBefore7AM = true;
                    long difference = end_calendar.getTimeInMillis() - temp.getTimeInMillis();
                    int minutes = (int) (difference / (1000 * 60));

                    for (int k = 0; k <= 6; k++) {
                        hours_arr_tv.get(k).setVisibility(View.VISIBLE);
                        hours_arr_iv.get(k).setVisibility(View.VISIBLE);
                    }

                    RelativeLayout newRelative = (RelativeLayout) getLayoutInflater().inflate(R.layout.task_card, null);
                    //new RelativeLayout(this);

                    TextView task_title = newRelative.findViewById(R.id.task_title),
                            task_description = newRelative.findViewById(R.id.task_description);

                    task_title.setText(Projects.get(i).getProject_tasks().get(j).getTask_name());
                    task_description.setText(Projects.get(i).getProject_tasks().get(j).getTask_description());

                    if (minutes <= 60) {

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) task_title.getLayoutParams();
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        task_title.setLayoutParams(params);
                        task_description.setVisibility(View.GONE);
                        task_title.setEllipsize(TextUtils.TruncateAt.END);
                        task_title.setMaxLines(1);
                    } else {
                        task_description.setEllipsize(TextUtils.TruncateAt.END);
                        task_description.setMaxLines(1);
                        task_title.setEllipsize(TextUtils.TruncateAt.END);
                        task_title.setMaxLines(1);
                    }

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (minutes * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
                    if (relCount % 4 == 0)
                        newRelative.setBackgroundResource(R.drawable.background_zetisq12a);
                    else if (relCount % 4 == 1)
                        newRelative.setBackgroundResource(R.drawable.background_redsq12a);
                    else if (relCount % 4 == 2)
                        newRelative.setBackgroundResource(R.drawable.background_yellowsq12a);
                    else if (relCount % 4 == 3)
                        newRelative.setBackgroundResource(R.drawable.background_bluesq12a);

                    relCount++;

                    newRelative.setId(relCount);

                    params.rightMargin = (int) (24 * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    params.leftMargin = (int) (24 * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    params.topMargin = (int) ((-50) * ((float) CalendarFragment.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                    params.addRule(RelativeLayout.END_OF, R.id.h7AM);
                    int id = hours_arr_tv.get(0).getId();
                    params.addRule(RelativeLayout.BELOW, id);
                    newRelative.setLayoutParams(params);

                    final int finalI = i;
                    final int finalJ = j;
                    newRelative.setOnClickListener(v -> {
                        Tasks task = Projects.get(finalI).getProject_tasks().get(finalJ);
//                        Toast.makeText(getContext(), "" + Projects.get(finalI).getProject_tasks().get(finalJ).getTask_name(), Toast.LENGTH_SHORT).show();
                        builder = new AlertDialog.Builder(getContext());
                        View task_dialog = getLayoutInflater().inflate(R.layout.fragment_one_task, null);
                        TextInputLayout taskName = task_dialog.findViewById(R.id.task_title),
                                projectName = task_dialog.findViewById(R.id.task_project_name),
                                date = task_dialog.findViewById(R.id.task_date),
                                start_time = task_dialog.findViewById(R.id.task_start_time),
                                end_time = task_dialog.findViewById(R.id.task_end_time),
                                description = task_dialog.findViewById(R.id.task_description);
                        taskName.getEditText().setText(task.getTask_name());
                        description.getEditText().setText(task.getTask_description());
                        projectName.getEditText().setText(task.getProjectName());
                        description.getEditText().setText(task.getTask_description());

                        Calendar start = (Calendar) StringToCalendar(task.getStart_calendar()).clone();
                        String today = start.get(Calendar.DAY_OF_MONTH) + "/" + start.get(Calendar.MONTH) + "/" + start.get(Calendar.YEAR);
                        date.getEditText().setText(today);
                        String s = "" + start.get(Calendar.HOUR) + ":" + (start.get(Calendar.MINUTE) < 10 ? "0" + start.get(Calendar.MINUTE) : start.get(Calendar.MINUTE)) + " "
                                + (start.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                        start_time.getEditText().setText(s);

                        Calendar end = (Calendar) StringToCalendar(task.getEnd_calendar()).clone();
                        String e = "" + end.get(Calendar.HOUR) + ":" + (end.get(Calendar.MINUTE) < 10 ? "0" + end.get(Calendar.MINUTE) : end.get(Calendar.MINUTE)) + " "
                                + (end.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                        end_time.getEditText().setText(e);


                        builder.setView(task_dialog);
                        dialog = builder.create();
                        dialog.show();
                        Window window = dialog.getWindow();
                        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                        assert window != null;
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    });

                    relativeLayout.addView(newRelative);
                    thisDayTasks.add(newRelative);

                }

            }

        if (!thereAreTasksBefore7AM)
            for (int k = 0; k <= 6; k++) {
                hours_arr_tv.get(k).setVisibility(View.GONE);
                hours_arr_iv.get(k).setVisibility(View.GONE);
                Log.w("Lol2", "calendar GGG" + k);
            }


    }

    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
