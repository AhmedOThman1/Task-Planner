package com.example.taskplanner.ui.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskplanner.R;
import com.example.taskplanner.RecyclerViewTouchListener;
import com.example.taskplanner.adapter.TasksRecyclerViewAdapter;
import com.example.taskplanner.model.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.check_project_is_exist;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.uploadProject;
import static com.example.taskplanner.ui.fragments.HomeFragment.context;
import static com.example.taskplanner.ui.fragments.HomeFragment.setProjectsProcess;
import static com.example.taskplanner.ui.fragments.HomeFragment.setProjectsTime;
import static com.example.taskplanner.ui.activities.Launcher.DONE_TASKS;
import static com.example.taskplanner.ui.activities.MainActivity.Done_tasks;
import static com.example.taskplanner.ui.activities.Launcher.IN_TASKS;
import static com.example.taskplanner.ui.activities.MainActivity.InProgress_tasks;
import static com.example.taskplanner.ui.activities.MainActivity.Projects;
import static com.example.taskplanner.ui.activities.Launcher.SHARED_PREF;
import static com.example.taskplanner.ui.activities.Launcher.TODO_TASKS;
import static com.example.taskplanner.ui.activities.MainActivity.ToDo_tasks;

public class TasksFragment extends Fragment {


    public static ArrayList<Tasks> project_tasks = new ArrayList<>();

    LinearLayout tasks_snack;
    TextView snack1, snack2, snack3_delete;
    View clickToCancel;
    public static TasksRecyclerViewAdapter Tasksadapter;
    RecyclerView project_tasks_recyclerview;
    boolean btemp = false;
    String project_name, color;
    FirebaseUser currentUser;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        project_tasks_recyclerview = view.findViewById(R.id.tasks_recyclerview);
        tasks_snack = view.findViewById(R.id.task_snack);
        snack1 = view.findViewById(R.id.snack1);
        snack2 = view.findViewById(R.id.snack2);
        snack3_delete = view.findViewById(R.id.snack3_delete);
        clickToCancel = view.findViewById(R.id.clickToCancel);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Log.d("Lol2", "!!! " + ToDo_tasks.size());
        //getToInDone(this);
//        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getToInDone(getContext());

        Bundle bundle = getArguments();
        assert bundle != null;
        project_name = bundle.getString("project name");
        color = bundle.getString("color");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (project_name == null) {

            Toast.makeText(getContext(), "null \uD83D\uDE25", Toast.LENGTH_SHORT).show();

        } else if (project_name.equals("todo")) {
            toolbar.setBackgroundResource(R.color.red);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.red));
            }
            getActivity().setTitle("To DO");
            Log.d("Lol2", "@ " + ToDo_tasks.size() + " , " + project_tasks.size());
            project_tasks = ToDo_tasks;
            Log.d("Lol2", "@@ " + ToDo_tasks.size() + " , " + project_tasks.size());
            btemp = true;
        } else if (project_name.equals("in progress")) {
            toolbar.setBackgroundResource(R.color.yellow);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.yellow));
            }
            getActivity().setTitle("In Progress");
            project_tasks = InProgress_tasks;
            btemp = true;
        } else if (project_name.equals("done")) {
            toolbar.setBackgroundResource(R.color.blue);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.blue));
            }
            getActivity().setTitle("Done");
            project_tasks = Done_tasks;
            btemp = true;
        } else {
            getActivity().setTitle(project_name);

            Toast.makeText(getContext(), "" + color, Toast.LENGTH_SHORT).show();

            switch (color) {
                case "0":
                    toolbar.setBackgroundResource(R.color.zeti);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.zeti));
                    }
                    break;
                case "1":
                    toolbar.setBackgroundResource(R.color.red);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.red));
                    }
                    break;
                case "2":
                    toolbar.setBackgroundResource(R.color.yellow);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.yellow));
                    }
                    break;
                case "3":
                    toolbar.setBackgroundResource(R.color.blue);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.blue));
                    }
                    break;
            }

            project_tasks = check_project_is_exist(project_name).getProject_tasks();

        }

        Log.w("Lol2", "TasksActivity\t\t\ttasks num : " + project_tasks.size());


        // sorting projects array-list by project title
        Collections.sort(project_tasks, Tasks.tasksComparator);

        Tasksadapter = new TasksRecyclerViewAdapter(getActivity());
        Tasksadapter.setTasks(project_tasks);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        project_tasks_recyclerview.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), project_tasks_recyclerview, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Tasks task = project_tasks.get(position);

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

            }

            @Override
            public void onLongClick(View view, final int position) {

                Log.w("yarab", "project_tasks.size :  " + project_tasks.size());
                tasks_snack.setVisibility(View.VISIBLE);
                clickToCancel.setVisibility(View.VISIBLE);
                if (project_name.equals("todo") || (!btemp && project_tasks.get(position).getToindone().equals("To Do"))) {
                    snack1.setText("Done");
                    snack2.setText("In progress");
                } else if (project_name.equals("in progress") || (!btemp && project_tasks.get(position).getToindone().equals("In Progress"))) {
                    snack1.setText("ToDo");
                    snack2.setText("Done");
                } else if (project_name.equals("done") || (!btemp && project_tasks.get(position).getToindone().equals("Done"))) {
                    snack1.setText("ToDo");
                    snack2.setText("In progress");
                }
                snack1.setOnClickListener(v -> {
                    Tasks task = project_tasks.get(position);

                    if (project_name.equals("todo") || (!btemp && project_tasks.get(position).getToindone().equals("To Do"))) {
                        ConvertTask(task, "Done");
                    } else if (project_name.equals("in progress") || project_name.equals("done") || (!btemp && !project_tasks.get(position).getToindone().equals("To Do"))) {
                        ConvertTask(task, "To Do");
                    }

                    Tasksadapter.setTasks(project_tasks);
                    Tasksadapter.notifyDataSetChanged();
                    tasks_snack.setVisibility(View.GONE);
                    clickToCancel.setVisibility(View.GONE);
                });
                snack2.setOnClickListener(v -> {
                    Tasks task = project_tasks.get(position);
                    if (project_name.equals("todo") || project_name.equals("done") || (!btemp && !project_tasks.get(position).getToindone().equals("In Progress"))) {
                        ConvertTask(task, "In Progress");
                    } else if (project_name.equals("in progress") || (!btemp && project_tasks.get(position).getToindone().equals("In Progress"))) {
                        ConvertTask(task, "Done");
                    }
                    Tasksadapter.setTasks(project_tasks);
                    Tasksadapter.notifyDataSetChanged();
                    tasks_snack.setVisibility(View.GONE);
                    clickToCancel.setVisibility(View.GONE);
                });

                snack3_delete.setOnClickListener(v -> {
                    Tasks task = project_tasks.get(position);
                    if (!btemp)
                        project_tasks.remove(position);
                    deleteTask(task);
                    Tasksadapter.setTasks(project_tasks);
                    Tasksadapter.notifyDataSetChanged();
                    tasks_snack.setVisibility(View.GONE);
                    clickToCancel.setVisibility(View.GONE);
                });
            }
        }));

        project_tasks_recyclerview.setAdapter(Tasksadapter);
        project_tasks_recyclerview.setLayoutManager(manager);

        clickToCancel.setOnClickListener(v -> {
            tasks_snack.setVisibility(View.GONE);
            clickToCancel.setVisibility(View.GONE);
        });
        return view;
    }

    private void ConvertTask(Tasks task, String type) {
        for (int i = 0; i < Projects.size(); i++)
            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++) {
                if (task.equals(Projects.get(i).getProject_tasks().get(j))) {
                    Projects.get(i).getProject_tasks().get(j).setToindone(type);
                    setProjectsProcess();
                    setProjectsTime();
                    getToInDone(context);

                    switch (project_name) {
                        case "todo":
                            project_tasks = ToDo_tasks;
                            break;
                        case "in progress":
                            project_tasks = InProgress_tasks;
                            break;
                        case "done":
                            project_tasks = Done_tasks;
                            break;
                        default:
                            project_tasks = check_project_is_exist(project_name).getProject_tasks();
                            break;
                    }

                    uploadProject(currentUser);
                    return;
                }
            }

    }

    private void deleteTask(Tasks task) {
        for (int i = 0; i < Projects.size(); i++)
            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++) {
                if (task.equals(Projects.get(i).getProject_tasks().get(j))) {
                    Projects.get(i).getProject_tasks().remove(j);
                    setProjectsProcess();
                    setProjectsTime();
                    getToInDone(context);
                    if (project_name.equals("todo"))
                        project_tasks = ToDo_tasks;
                    else if (project_name.equals("in progress"))
                        project_tasks = InProgress_tasks;
                    else if (project_name.equals("done"))
                        project_tasks = Done_tasks;
                    else
                        project_tasks = check_project_is_exist(project_name).getProject_tasks();

                    uploadProject(currentUser);
                    return;
                }
            }

    }

    public static void getToInDone(Context context) {

        ToDo_tasks.clear();
        InProgress_tasks.clear();
        Done_tasks.clear();

        for (int i = 0; i < Projects.size(); i++) {
            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++) {
                if (Projects.get(i).getProject_tasks().get(j).getToindone().equals("To Do"))
                    ToDo_tasks.add(Projects.get(i).getProject_tasks().get(j));
                else if (Projects.get(i).getProject_tasks().get(j).getToindone().equals("In Progress"))
                    InProgress_tasks.add(Projects.get(i).getProject_tasks().get(j));
                else if (Projects.get(i).getProject_tasks().get(j).getToindone().equals("Done"))
                    Done_tasks.add(Projects.get(i).getProject_tasks().get(j));
            }
        }

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(TODO_TASKS, ToDo_tasks.size());
        editor.putInt(IN_TASKS, InProgress_tasks.size());
        editor.putInt(DONE_TASKS, Done_tasks.size());
        editor.apply();

        if (HomeFragment.done_tasks_tv != null)
            HomeFragment.done_tasks_tv.setText(Done_tasks.size() + " tasks now . ");
        if (HomeFragment.todo_tasks_tv != null)
            HomeFragment.todo_tasks_tv.setText(ToDo_tasks.size() + " tasks now . ");
        if (HomeFragment.inProgress_tasks_tv != null)
            HomeFragment.inProgress_tasks_tv.setText(InProgress_tasks.size() + " tasks now . ");
    }
}
