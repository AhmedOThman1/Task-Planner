package com.example.taskplanner.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.taskplanner.R;
import com.example.taskplanner.RecyclerViewTouchListener;
import com.example.taskplanner.adapter.ProjectsRecyclerViewAdapter;
import com.example.taskplanner.model.Activeprojects;
import com.example.taskplanner.model.Tasks;
import com.example.taskplanner.ui.activities.PhotoActivity;
import com.example.taskplanner.ui.viewModel.ProjectViewModel;
import com.example.taskplanner.ui.viewModel.ReminderViewModel;
import com.example.taskplanner.ui.viewModel.TargetViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.uploadProject;
import static com.example.taskplanner.ui.activities.Launcher.DONE_TASKS;
import static com.example.taskplanner.ui.activities.Launcher.IN_TASKS;
import static com.example.taskplanner.ui.activities.Launcher.MAIN_PROGRESS;
import static com.example.taskplanner.ui.activities.Launcher.SHARED_PREF;
import static com.example.taskplanner.ui.activities.Launcher.TODO_TASKS;
import static com.example.taskplanner.ui.activities.Launcher.USER_JOB;
import static com.example.taskplanner.ui.activities.Launcher.USER_NAME;
import static com.example.taskplanner.ui.activities.MainActivity.Done_tasks;
import static com.example.taskplanner.ui.activities.MainActivity.InProgress_tasks;
import static com.example.taskplanner.ui.activities.MainActivity.Projects;
import static com.example.taskplanner.ui.activities.MainActivity.Reminders;
import static com.example.taskplanner.ui.activities.MainActivity.Targets;
import static com.example.taskplanner.ui.activities.MainActivity.ToDo_tasks;
import static com.example.taskplanner.ui.activities.MainActivity.hideNav;
import static com.example.taskplanner.ui.activities.MainActivity.open_drawer;
import static com.example.taskplanner.ui.activities.MainActivity.projectViewModel;
import static com.example.taskplanner.ui.activities.MainActivity.reminderViewModel;
import static com.example.taskplanner.ui.activities.MainActivity.setCheck;
import static com.example.taskplanner.ui.activities.MainActivity.showNav;
import static com.example.taskplanner.ui.activities.MainActivity.targetViewModel;
import static com.example.taskplanner.ui.fragments.TasksFragment.getToInDone;

public class HomeFragment extends Fragment {

    private final static int CODE1_PERMISSION = 1, CODE2_PERMISSION = 2, CODE2_CAM = 1, CODE3_GAL = 2;
    // el nas 12054
    public static ProjectsRecyclerViewAdapter adapter;
    static TextView job_tv, todo_tasks_tv, inProgress_tasks_tv, done_tasks_tv;
    private static RelativeLayout NoProjects;
    private CollapsingToolbarLayout coolToolbar;
    private static ProgressBar mainProgressBar;
    private CircleImageView profile_image;
    private AlertDialog dialog;

    private int mainProgress = 0;
    private FirebaseUser currentUser;
    private StorageReference Pic_StorageRef;
    private DatabaseReference myDbRef;
    private boolean
            button1Debounce = true,
            button2Debounce = true,
            button3Debounce = true,
            button4Debounce = true,
            button5Debounce = true;
    public static boolean isDrawerOpen = false;
    static Context context;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(getActivity());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        StorageReference DbRef = FirebaseStorage.getInstance().getReference();
        Pic_StorageRef = DbRef.child("profile_pic/" + currentUser.getUid() + "__image");


        context = getContext();

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


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        LinearLayout todo = view.findViewById(R.id.todoLL);
        LinearLayout inprogress = view.findViewById(R.id.inprogressLL);
        LinearLayout done = view.findViewById(R.id.doneLL);
        todo_tasks_tv = view.findViewById(R.id.todo_tasks_tv);
        inProgress_tasks_tv = view.findViewById(R.id.inProgress_tasks_tv);
        done_tasks_tv = view.findViewById(R.id.done_tasks_tv);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        RecyclerView active_projects_recyclerview = view.findViewById(R.id.grid_active_projects);
        coolToolbar = view.findViewById(R.id.coolToolbar);
        job_tv = view.findViewById(R.id.job);
        mainProgressBar = view.findViewById(R.id.mainProgressBar);
        profile_image = view.findViewById(R.id.profile_image);
        LinearLayout profile = view.findViewById(R.id.profile);
        NoProjects = view.findViewById(R.id.no_projects);

        registerForContextMenu(profile);

        SharedPreferences share = getContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String username = share.getString(USER_NAME, "");
        String userjob = share.getString(USER_JOB, "");
        int todo_tasks = share.getInt(TODO_TASKS, 0),
                inProgress_tasks = share.getInt(IN_TASKS, 0),
                done_tasks = share.getInt(DONE_TASKS, 0);
        mainProgress = share.getInt(MAIN_PROGRESS, 0);

        retrieveProfilePicture();
        coolToolbar.setTitle(username);
        job_tv.setText(userjob);

        done_tasks_tv.setText(todo_tasks + " tasks now . ");
        todo_tasks_tv.setText(done_tasks + " tasks now . ");
        inProgress_tasks_tv.setText(inProgress_tasks + " tasks now . ");

        mainProgressBar.setProgress(mainProgress);

        view.findViewById(R.id.tasks).setFocusable(true);

        todo.setOnClickListener(v -> {
            new Handler().postDelayed(() -> button2Debounce = true, 350);
            if (button2Debounce) {
                button2Debounce = false;
                Fragment tasks_fragment = new TasksFragment();
                Bundle bundle = new Bundle();
                bundle.putString("project name", "todo");
                tasks_fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, tasks_fragment).commit();

            }
        });

        inprogress.setOnClickListener(v -> {
            new Handler().postDelayed(() -> button3Debounce = true, 350);
            if (button3Debounce) {
                button3Debounce = false;
                Fragment tasks_fragment = new TasksFragment();
                Bundle bundle = new Bundle();
                bundle.putString("project name", "in progress");
                tasks_fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, tasks_fragment).commit();
            }
        });

        done.setOnClickListener(v -> {
            new Handler().postDelayed(() -> button4Debounce = true, 350);
            if (button4Debounce) {
                button4Debounce = false;
                Fragment tasks_fragment = new TasksFragment();
                Bundle bundle = new Bundle();
                bundle.putString("project name", "done");
                tasks_fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, tasks_fragment).commit();
            }
        });

        fab.setOnClickListener(v -> {
            new Handler().postDelayed(() -> button5Debounce = true, 350);
            if (button5Debounce) {
                button5Debounce = false;
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new CalendarFragment()).commit();
                setCheck(1, false, getContext());
            }
        });

        // sorting projects array-list by project title
        Collections.sort(Projects, Activeprojects.projectsComparator);

        adapter = new ProjectsRecyclerViewAdapter(getActivity());
        adapter.setProjects(Projects);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(calculateNoOfColumns(getContext()), StaggeredGridLayoutManager.VERTICAL);

        active_projects_recyclerview.setAdapter(adapter);
        active_projects_recyclerview.setLayoutManager(manager);

        active_projects_recyclerview.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), active_projects_recyclerview, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                Fragment tasks_fragment = new TasksFragment();
                Bundle bundle = new Bundle();
                bundle.putString("project name", Projects.get(position).getTitle());
                bundle.putString("color", "" + position % 4);
                tasks_fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, tasks_fragment).commit();

            }

            @Override
            public void onLongClick(View view, int position) {

                view.setBackgroundColor(getResources().getColor(R.color.black));
                deleteProject(position);
            }
        }));
        if (Projects.size() != 0)
            setMainProgress();
//
//        active_projects_recyclerview.addItemDecoration(new ItemDecorationAlbumColumns(
//                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), calculateNoOfColumns(getContext())));

        if (Projects.size() == 0)
            NoProjects.setVisibility(View.VISIBLE);
        else
            NoProjects.setVisibility(View.GONE);


        AppBarLayout appBarLayout = view.findViewById(R.id.AppBarL);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, i) -> {
            if (Math.abs(i) == appBarLayout1.getTotalScrollRange()) {
                appBarLayout1.setBackgroundResource(R.drawable.background_toolbar2);
            } else {
                appBarLayout1.setBackgroundResource(R.drawable.background_toolbar);
            }
        });

        projectViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);
        targetViewModel = new ViewModelProvider(this).get(TargetViewModel.class);
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);


        if (Projects.size() == 0)
            projectViewModel.getProjects(currentUser);
        if (Targets.size() == 0)
            targetViewModel.getTargets(currentUser);
        if (Reminders.size() == 0)
            reminderViewModel.getReminders(currentUser);

        projectViewModel.ProjectsMutableLiveData.observe(getActivity(), projects -> {
//            Projects.clear();
            Projects = projects;
            setProjectsProcess();
            setProjectsTime();

            mainProgress = getMainProgress();
            if (Projects.size() != 0)
                mainProgress = mainProgress / Projects.size();
            if (adapter != null && mainProgressBar != null && NoProjects != null) {
                adapter.setProjects(Projects);
                adapter.notifyDataSetChanged();
                mainProgressBar.setProgress(mainProgress);
                NoProjects.setVisibility(View.GONE);
                getToInDone(context);
                todo_tasks_tv.setText(ToDo_tasks.size() + " tasks now . ");
                inProgress_tasks_tv.setText(InProgress_tasks.size() + " tasks now . ");
                done_tasks_tv.setText(Done_tasks.size() + " tasks now . ");
                if (Projects.size() == 0)
                    NoProjects.setVisibility(View.VISIBLE);
                else
                    NoProjects.setVisibility(View.GONE);
            }
        });

        targetViewModel.TargetsMutableLiveData.observe(getActivity(), targets -> {
            Targets = targets;
        });

        reminderViewModel.RemindersMutableLiveData.observe(getActivity(), reminders -> {
            Reminders = reminders;
        });


        return view;
    }

    AlertDialog.Builder builder;

    private void deleteProject(int position) {

        builder = new AlertDialog.Builder(getContext());

        View delete_dialog = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        TextView back = delete_dialog.findViewById(R.id.back_dialog),
                delete = delete_dialog.findViewById(R.id.delete_dialog),
                del_text = delete_dialog.findViewById(R.id.deltxt);

        String del_message = "Are you sure you want to delete " + Projects.get(position).getTitle()
                + " project with all tasks inside it( " + Projects.get(position).getProject_tasks().size() + " task ) ?";
        del_text.setText(del_message);
        back.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ok ♥️nothing deleted", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        delete.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Project " + Projects.get(position).getTitle() + " deleted \uD83D\uDE0D\uD83D\uDE48\uD83D\uDE48♥️", Toast.LENGTH_LONG).show();
            Projects.remove(position);
            adapter.notifyDataSetChanged();
            uploadProject(currentUser);
            setMainProgress();
            dialog.dismiss();
        });

        builder.setView(delete_dialog);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void retrieveProfilePicture() {
        myDbRef = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        myDbRef.child(currentUser.getUid() + "/profile_pic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                final String pic = dataSnapshot.getValue(String.class);

                Glide.with(HomeFragment.this)
                        .load(pic)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(profile_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mainProgress = getMainProgress();
        if (Projects.size() != 0)
            mainProgress = mainProgress / Projects.size();
        if (adapter != null && mainProgressBar != null && NoProjects != null) {
            setProjectsProcess();
            setProjectsTime();
            adapter.setProjects(Projects);
            adapter.notifyDataSetChanged();
            mainProgressBar.setProgress(mainProgress);
            NoProjects.setVisibility(View.GONE);
            getToInDone(context);
            todo_tasks_tv.setText(ToDo_tasks.size() + " tasks now . ");
            inProgress_tasks_tv.setText(InProgress_tasks.size() + " tasks now . ");
            done_tasks_tv.setText(Done_tasks.size() + " tasks now . ");
            if (Projects.size() == 0)
                NoProjects.setVisibility(View.VISIBLE);
            else
                NoProjects.setVisibility(View.GONE);
        }
    }

    private int getMainProgress() {
        int main = 0;
        for (int i = 0; i < Projects.size(); i++)
            main += Projects.get(i).getProgress();
        return main;
    }

    static void setProjectsProcess() {
        for (int i = 0; i < Projects.size(); i++) {
            int progress = 0;
            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++)
                if (Projects.get(i).getProject_tasks().get(j).getToindone().equals("Done"))
                    progress++;
            if (Projects.get(i).getProject_tasks() != null && Projects.get(i).getProject_tasks().size() != 0)
                progress = progress * 100 / Projects.get(i).getProject_tasks().size();
            Projects.get(i).setProgress(progress);
            Log.w("time", "Project " + i + " : " + progress + "% progress");

        }
        adapter.setProjects(Projects);
        adapter.notifyDataSetChanged();
    }

    static void setProjectsTime() {
        for (int i = 0; i < Projects.size(); i++) {
            int hours = 0;
            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++)
                if (Projects.get(i).getProject_tasks().get(j).getToindone().equals("Done")) {
                    Tasks task = Projects.get(i).getProject_tasks().get(j);
                    Calendar start = (Calendar) StringToCalendar(task.getStart_calendar()).clone();
                    Calendar end = (Calendar) StringToCalendar(task.getEnd_calendar()).clone();
                    long diff = end.getTimeInMillis() - start.getTimeInMillis();
                    hours += (int) (diff / (1000 * 60 * 60));
                }
            Projects.get(i).setTime(hours + " hours progress");
            Log.w("time", "Project " + i + " : " + hours + " hours progress");

        }
        adapter.setProjects(Projects);
        adapter.notifyDataSetChanged();
    }

//    static void retrieveTargets(final FirebaseUser currentUser) {
//        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
//        DbRef.child(currentUser.getUid() + "/Targets").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Targets.clear();
//                Log.w("Lol2", "Count1 : " + dataSnapshot.getChildrenCount());
//                Log.w("Lol2", "" + dataSnapshot.getKey());
//
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    //dataSnapshot.child("" + i).child("start_calendar").getValue(String.class);
//                    Target target = new Target(
//                            data.child("name").getValue(String.class),
//                            data.child("note").getValue(String.class),
//                            data.child("day").getValue(String.class)
//                    );
//                    target.setProgress(data.child("progress").getValue(Integer.class));
//                    Targets.add(target);
//                    retrieveSteps(data.getKey(), currentUser);
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("Lol", "Failed1 to read value.", error.toException());
//            }
//        });
//
//    }
//    static void retrieveSteps(final String name, FirebaseUser currentUser) {
//        // Read from the database
//        DatabaseReference myDbRef = FirebaseDatabase.getInstance().getReference();
//        myDbRef.child(currentUser.getUid() + "/Targets/" + name + "/steps").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                if (dataSnapshot.exists()) {
//
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                        Target.Step step = new Target.Step(
//                                data.child("name").getValue(String.class),
//                                data.child("description").getValue(String.class));
//                        step.setCheck(data.child("check").getValue(boolean.class));
//
//                        if (!Targets.get(Integer.parseInt(name)).getSteps().contains(step))
//                            Targets.get(Integer.parseInt(name)).getSteps().add(step);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("Lol", "Failed2 to read value.", error.toException());
//            }
//        });
//    }
    //    static void retrieveProject(final FirebaseUser currentUser) {
//        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
//        DbRef.child(currentUser.getUid() + "/Projects").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Projects.clear();
//                Log.w("Lol2", "Count : " + dataSnapshot.getChildrenCount());
//                Log.w("Lol2", "" + dataSnapshot.getKey());
//
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    //dataSnapshot.child("" + i).child("start_calendar").getValue(String.class);
//                    Activeprojects project = new Activeprojects(
//                            data.child("progress").getValue(Integer.class),
//                            data.child("title").getValue(String.class),
//                            data.child("time").getValue(String.class));
//                    Projects.add(project);
//                    mainProgress += project.getProgress();
//
//                    retrieveTasks(data.getKey(), currentUser);
//                }
//                if (Projects.size() != 0)
//                    mainProgress = mainProgress / Projects.size();
//                Log.d("Lol2", "M#M" + mainProgress);
//
//                if (adapter != null && mainProgressBar != null && NoProjects != null) {
//                    adapter.notifyDataSetChanged();
//                    mainProgressBar.setProgress(mainProgress);
//                    NoProjects.setVisibility(View.GONE);
//                    getToInDone(context);
//                    if (Projects.size() == 0)
//                        NoProjects.setVisibility(View.VISIBLE);
//                    else
//                        NoProjects.setVisibility(View.GONE);
//
//                }
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("Lol", "Failed1 to read value.", error.toException());
//            }
//        });
//
//    }
//    static void retrieveTasks(final String name, final FirebaseUser currentUser) {
//        // Read from the database
//        DatabaseReference myDbRef = FirebaseDatabase.getInstance().getReference();
//        myDbRef.child(currentUser.getUid() + "/Projects/" + name + "/project_tasks/").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                if (dataSnapshot.exists()) {
//
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//
////                        GenericTypeIndicator<Tasks> t = new GenericTypeIndicator<Tasks>() {};
//                        Log.w("Log", currentUser.getUid() + "/Projects/" + name + "/project_tasks");
////                        Tasks task = dataSnapshot.getValue(t);
//                        Tasks task = new Tasks(
//                                data.child("projectName").getValue(String.class),
//                                data.child("task_name").getValue(String.class),
//                                data.child("start_calendar").getValue(String.class),
//                                data.child("end_calendar").getValue(String.class),
//                                data.child("task_description").getValue(String.class),
//                                data.child("toindone").getValue(String.class)
//                        );
//
//                        if (!Projects.get(Integer.parseInt(name)).getProject_tasks().contains(task))
//                            Projects.get(Integer.parseInt(name)).getProject_tasks().add(task);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("Lol", "Failed2 to read value.", error.toException());
//            }
//        });
//    }
//    static void retrieveReminders(final FirebaseUser currentUser) {
//        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
//        DbRef.child(currentUser.getUid() + "/Reminders").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Reminders.clear();
//                Log.w("Lol2", "Count2 : " + dataSnapshot.getChildrenCount());
//                Log.w("Lol2", "" + dataSnapshot.getKey());
//
//                GenericTypeIndicator<ArrayList<Boolean>> t = new GenericTypeIndicator<ArrayList<Boolean>>() {
//                };
//
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    Reminder reminder = new Reminder(
//                            data.child("time").getValue(String.class),
//                            data.child("AM_PM").getValue(String.class),
//                            data.child("date").getValue(String.class),
//                            data.child("title").getValue(String.class),
//                            data.child("type").getValue(String.class),
//                            data.child("week").getValue(Boolean.class),
//                            data.child("b").getValue(t)
//                    );
//                    Reminders.add(reminder);
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("Lol", "Failed1 to read value.", error.toException());
//            }
//        });
//
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.profile_image_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.view_image:
                startActivity(new Intent(getContext(), PhotoActivity.class));
                return true;
            case R.id.open_cam:

                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                            new String[]{Manifest.permission.CAMERA}, CODE1_PERMISSION);
                } else {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, CODE2_CAM);
                }
                return true;
            case R.id.open_gal:
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE2_PERMISSION);
                } else {
                    Intent gal = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    gal.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    gal.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                    startActivityForResult(Intent.createChooser(gal, "select media file"), CODE3_GAL);
                }
                return true;
            case R.id.change_name:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View name_dialog = getLayoutInflater().inflate(R.layout.add_new_chip, null);

                final TextInputLayout title = name_dialog.findViewById(R.id.new_chip_name);
                TextView ok = name_dialog.findViewById(R.id.ok_dialog),
                        cancel = name_dialog.findViewById(R.id.cancel_dialog);

                title.setHint("Enter your name \uD83D\uDE0C ");
                title.requestFocus();
                open_keyboard(Objects.requireNonNull(title.getEditText()));

                cancel.setOnClickListener(v -> dialog.dismiss());
                ok.setOnClickListener(v -> {

                    String name = title.getEditText().getText().toString().trim();

                    coolToolbar.setTitle(name);

                    myDbRef.child(currentUser.getUid() + "/name").setValue(name);

                    SharedPreferences share = getContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = share.edit();
                    editor.putString(USER_NAME, name);
                    editor.apply();

                    dialog.dismiss();


                });

                builder.setView(name_dialog).setCancelable(false);
                dialog = builder.create();
                dialog.show();
                Window window = dialog.getWindow();
                //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                assert window != null;
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                return true;

            case R.id.change_job:
                builder = new AlertDialog.Builder(getContext());
                View job_dialog = getLayoutInflater().inflate(R.layout.add_new_chip, null);

                final TextInputLayout job_title = job_dialog.findViewById(R.id.new_chip_name);
                TextView ok_job = job_dialog.findViewById(R.id.ok_dialog),
                        cancel_job = job_dialog.findViewById(R.id.cancel_dialog);

                job_title.setHint("Enter your job \uD83D\uDE0C ");
                job_title.requestFocus();
                open_keyboard(Objects.requireNonNull(job_title.getEditText()));

                cancel_job.setOnClickListener(v -> dialog.dismiss());

                ok_job.setOnClickListener(v -> {
                            String job = job_title.getEditText().getText().toString().trim();
                            job_tv.setText(job);
                            myDbRef.child(currentUser.getUid() + "/job").setValue(job);
                            SharedPreferences share = Objects.requireNonNull(getContext()).getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor editor = share.edit();
                            editor.putString(USER_JOB, job);
                            editor.apply();
                            dialog.dismiss();
                        }
                );

                builder.setView(job_dialog).setCancelable(false);
                dialog = builder.create();
                dialog.show();
                Window window2 = dialog.getWindow();
                //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                assert window2 != null;
                window2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE1_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, CODE2_CAM);
                }
                break;

            case CODE2_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent gal = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    gal.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    gal.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                    startActivityForResult(Intent.createChooser(gal, "select media file"), CODE3_GAL);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE2_CAM && resultCode == RESULT_OK) {
            // one photo from camera

            try {
                assert data != null;
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                if (bitmap != null) {
                    profile_image.setImageBitmap(bitmap);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Title", null));

                    // one image
                    Pic_StorageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> Pic_StorageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> myDbRef.child(currentUser.getUid() + "/profile_pic").setValue(String.valueOf(uri))
                                    .addOnSuccessListener(aVoid -> retrieveProfilePicture()).
                                            addOnFailureListener(e -> {
                                            })));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CODE3_GAL && resultCode == RESULT_OK) {
            // image from gallery
            assert data != null;
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // one image
                Pic_StorageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> Pic_StorageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> myDbRef.child(currentUser.getUid() + "/profile_pic").setValue(String.valueOf(uri))
                                .addOnSuccessListener(aVoid -> retrieveProfilePicture())
                                .addOnFailureListener(e -> {
                                })));
                try {

                    InputStream is = getContext().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    profile_image.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void open_keyboard(EditText textInputLayout) {
        textInputLayout.requestFocus();     // editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
        assert imm != null;
        imm.showSoftInput(textInputLayout, InputMethodManager.SHOW_IMPLICIT); //    first param -> editText
    }

    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void setMainProgress() {
        mainProgress = 0;
        for (int i = 0; i < Projects.size(); i++) {
            mainProgress += Projects.get(i).getProgress();
        }
        mainProgress = mainProgress / Projects.size();
        mainProgressBar.setProgress(mainProgress);

        SharedPreferences sharedPref = getContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(MAIN_PROGRESS, mainProgress);

        editor.apply();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / 180);
    }

    public static class ItemDecorationAlbumColumns extends RecyclerView.ItemDecoration {

        private int mSizeGridSpacingPx;
        private int mGridSize;

        private boolean mNeedLeftSpacing = false;

        ItemDecorationAlbumColumns(int gridSpacingPx, int gridSize) {
            mSizeGridSpacingPx = gridSpacingPx;
            mGridSize = gridSize;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, View view, RecyclerView parent, @NonNull RecyclerView.State state) {
            int frameWidth = (int) ((parent.getWidth() - (float) mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize);
            int padding = parent.getWidth() / mGridSize - frameWidth;
            int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
            if (itemPosition < mGridSize) {
                outRect.top = 0;
            } else {
                outRect.top = mSizeGridSpacingPx;
            }
            if (itemPosition % mGridSize == 0) {
                outRect.left = 0;
                outRect.right = padding;
                mNeedLeftSpacing = true;
            } else if ((itemPosition + 1) % mGridSize == 0) {
                mNeedLeftSpacing = false;
                outRect.right = 0;
                outRect.left = padding;
            } else if (mNeedLeftSpacing) {
                mNeedLeftSpacing = false;
                outRect.left = mSizeGridSpacingPx - padding;
                if ((itemPosition + 2) % mGridSize == 0) {
                    outRect.right = mSizeGridSpacingPx - padding;
                } else {
                    outRect.right = mSizeGridSpacingPx / 2;
                }
            } else if ((itemPosition + 2) % mGridSize == 0) {
                outRect.left = mSizeGridSpacingPx / 2;
                outRect.right = mSizeGridSpacingPx - padding;
            } else {
                outRect.left = mSizeGridSpacingPx / 2;
                outRect.right = mSizeGridSpacingPx / 2;
            }
            outRect.bottom = 0;
        }
    }
}
