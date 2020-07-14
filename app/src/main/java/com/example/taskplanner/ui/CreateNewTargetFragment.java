package com.example.taskplanner.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.adapter.StepAdapter;
import com.example.taskplanner.model.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.taskplanner.ui.CreateNewTaskFragment.CalendarToString;
import static com.example.taskplanner.ui.MainActivity.Targets;
import static com.example.taskplanner.ui.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.adapter.StepAdapter.Steps;
import static com.example.taskplanner.ui.MainActivity.hideNav;
import static com.example.taskplanner.ui.MainActivity.open_drawer;
import static com.example.taskplanner.ui.MainActivity.showNav;
import static com.example.taskplanner.ui.TodayActivity.days;

public class CreateNewTargetFragment extends Fragment {

    TextInputLayout date, title_input_layout, note;
    CollapsingToolbarLayout coolToolbar;
    public static AppBarLayout appBarLayout;
    public static Chip add_new_step, add_note, add_target_steps;
    //    RelativeLayout stepper;
//    ArrayList<RelativeLayout> Array_RelativeLayout_steps = new ArrayList<>();
    //    ArrayList<Target.Step> ThisTargetSTEPS = new ArrayList<>();
    public static Calendar calendar_selected_day, calendar;
    public static StepAdapter stepAdapter;
    RecyclerView Steps_recycler;
    FirebaseUser currentUser;
    public static String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    View view;

    public CreateNewTargetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_new_target, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow));
        }

        date = view.findViewById(R.id.date_input_layout);
        coolToolbar = view.findViewById(R.id.coolToolbar);
        title_input_layout = view.findViewById(R.id.title_input_layout);
        note = view.findViewById(R.id.note);
        appBarLayout = view.findViewById(R.id.AppBarL);
        add_note = view.findViewById(R.id.add_note);
        add_target_steps = view.findViewById(R.id.add_target_steps);
        Steps_recycler = view.findViewById(R.id.steps_recycler);

        stepAdapter = new StepAdapter(getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        Steps_recycler.setAdapter(stepAdapter);
        Steps_recycler.setLayoutManager(manager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        init();
/// to show date picker and select date & put it in date TextView
        view.findViewById(R.id.fab).setOnClickListener(v -> choice_date());
        date.setOnClickListener(v -> choice_date());
        date.getEditText().setOnClickListener(v -> choice_date());

        add_note.setOnClickListener(v -> {
            add_note.setVisibility(View.GONE);
            note.setVisibility(View.VISIBLE);
            view.findViewById(R.id.delete_note).setVisibility(View.VISIBLE);
            note.getEditText().requestFocus();
            open_keyboard(note.getEditText());
//                test();
        });

        add_target_steps.setOnClickListener(v -> {
            if(Steps.size()!=0)
                Steps.clear();
            add_target_steps.setVisibility(View.GONE);
            Steps.add(new Target.Step("", ""));
            stepAdapter.notifyDataSetChanged();
        });

        view.findViewById(R.id.delete_note).setOnClickListener(v -> {
            note.getEditText().setText("");
            note.setVisibility(View.GONE);
            add_note.setVisibility(View.VISIBLE);
            view.findViewById(R.id.delete_note).setVisibility(View.GONE);
//                test();
        });

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

        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (Math.abs(i) == appBarLayout.getTotalScrollRange()) {
                coolToolbar.setTitle("Create new target");
                appBarLayout.setBackgroundResource(R.drawable.background_toolbar2);
            } else if (Math.abs(i) < (appBarLayout.getTotalScrollRange()) / 2) {
                coolToolbar.setTitle("");
                appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
            } else {
                coolToolbar.setTitle("Create new target");
                appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
            }
        });

        title_input_layout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (!title_input_layout.getEditText().getText().toString().isEmpty())
                title_input_layout.setError(null);

            return false;
        });

        view.findViewById(R.id.create_target).setOnClickListener(v -> {

            close_keyboard();

            calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 9);

            if (title_input_layout.getEditText().getText().toString().trim().isEmpty()) {
                title_input_layout.setError("Can't be empty");
                title_input_layout.requestFocus();
                open_keyboard(title_input_layout.getEditText());
                appBarLayout.setExpanded(true);
            } else {

                title_input_layout.setError(null);
                String targetName = title_input_layout.getEditText().getText().toString().trim(),
                        targetDescription = note.getEditText().getText().toString().trim();

                if (Steps.size() > 0 && Steps.get(Steps.size() - 1).getName().equals(""))
                    Toast.makeText(getContext(), "Last Step Name Can't be empty", Toast.LENGTH_SHORT).show();
                else if (Steps.size() > 0 && Steps.get(Steps.size() - 1).getDescription().equals(""))
                    Toast.makeText(getContext(), "Last Step Description Can't be empty", Toast.LENGTH_SHORT).show();
                else {
                    Log.w("TESE","before add");
//                        test();
                    add_target(targetName, targetDescription, calendar_selected_day, Steps);
                    Log.w("TESE","after add");
//                        test();
                    Steps.clear();
                    Toast.makeText(getContext(), "Done \uD83D\uDE0C♥️", Toast.LENGTH_SHORT).show();
                    init();
                }
            }
        });

        return view;
    }

    public static void uploadTarget(FirebaseUser currentUser) {
        Log.w("Lol2", "CreateNewTask , uploadTargets , Targets.size :" + Targets.size() + "\t" + Targets.get(0).getSteps().size() + "\n");
//        test();
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Targets/").setValue(Targets);
        Log.w("Lol2", "-----------------create , End of upload Targets-------------------");
    }

    private void add_target(String targetName, String targetDescription, Calendar deadline, ArrayList<Target.Step> Steps) {
        if (targetDescription.isEmpty()) {
            if (Steps.size() == 0)
                Targets.add(new Target(targetName, CalendarToString(deadline)));
            else
                Targets.add(new Target(targetName, CalendarToString(deadline), Steps));
        } else {
            if (Steps.size() == 0)
                Targets.add(new Target(targetName, targetDescription, CalendarToString(deadline)));
            else
                Targets.add(new Target(targetName, targetDescription, CalendarToString(deadline), Steps));
        }
        Log.w("Log","Steps size :"+Steps.size());
//        test();
        uploadTarget(currentUser);

//        ((RelativeLayout) findViewById(R.id.small_stepper)).removeAllViews();
//        Array_RelativeLayout_steps.clear();
        note.getEditText().setText("");
        note.setVisibility(View.GONE);
        add_note.setVisibility(View.VISIBLE);
        view.findViewById(R.id.delete_note).setVisibility(View.GONE);
        Steps.clear();
        stepAdapter.notifyDataSetChanged();
//        add_new_step.setVisibility(View.GONE);
        add_target_steps.setVisibility(View.VISIBLE);
    }

    /*
        void addNewStep() {
            Log.w("Lol", "SSSSize : " + Array_RelativeLayout_steps.size());
            final RelativeLayout newStep = (RelativeLayout) getLayoutInflater().inflate(R.layout.step_item, null);
            newStep.setId(Array_RelativeLayout_steps.size() + 1);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (Array_RelativeLayout_steps.size() != 0) {
                params.addRule(RelativeLayout.BELOW, Array_RelativeLayout_steps.get(Array_RelativeLayout_steps.size() - 1).getId());
    //            steps.get(steps.size() - 1).findViewById(R.id.line).setVisibility(View.VISIBLE);
                Toast.makeText(this, Array_RelativeLayout_steps.get(Array_RelativeLayout_steps.size() - 1).getId() + "\t " + ((EditText) Array_RelativeLayout_steps.get(Array_RelativeLayout_steps.size() - 1).findViewById(R.id.step_title)).getText().toString(), Toast.LENGTH_SHORT).show();
                Log.w("Lol", Array_RelativeLayout_steps.get(Array_RelativeLayout_steps.size() - 1).getId() + "\t " + ((EditText) Array_RelativeLayout_steps.get(Array_RelativeLayout_steps.size() - 1).findViewById(R.id.step_title)).getText().toString().trim());
            } //else newStep.setVisibility(View.GONE);
            params.bottomMargin = 8;
            newStep.setLayoutParams(params);

            newStep.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("Lol", Array_RelativeLayout_steps.size() + " , this is the size !");
                    for (int i = 0; i < Array_RelativeLayout_steps.size(); i++) {
                        Log.w("Lol", i + " : " + ((EditText) Array_RelativeLayout_steps.get(i).findViewById(R.id.step_title)).getText().toString().trim());
                        if (Array_RelativeLayout_steps.get(i) == newStep) {
                            Log.w("Lol", "---" + ((EditText) newStep.findViewById(R.id.step_title)).getText().toString().trim());
                            Array_RelativeLayout_steps.remove(newStep);
                            break;
                        }
                    }
    //                ((RelativeLayout) findViewById(R.id.small_stepper)).removeView(newStep);

                    if (Array_RelativeLayout_steps.size() == 0) {
    //                    add_new_step.setVisibility(View.GONE);
                        add_target_steps.setVisibility(View.VISIBLE);
                    }
                }
            });

            newStep.findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (newStep.findViewById(R.id.step_title).getVisibility() == View.VISIBLE && !((EditText) newStep.findViewById(R.id.step_title)).getText().toString().trim().isEmpty()) {
                        newStep.findViewById(R.id.step_title).setVisibility(View.GONE);
                        newStep.findViewById(R.id.step_description).setVisibility(View.GONE);
    //                    if (newStep != steps.get(steps.size() - 1))
    //                        newStep.findViewById(R.id.line).setVisibility(View.GONE);
                        newStep.findViewById(R.id.step_content).setVisibility(View.VISIBLE);
                        ((TextView) newStep.findViewById(R.id.step_content)).setText(((EditText) newStep.findViewById(R.id.step_title)).getText().toString().trim());
                    } else {
                        newStep.findViewById(R.id.step_title).setVisibility(View.VISIBLE);
                        newStep.findViewById(R.id.step_description).setVisibility(View.VISIBLE);
    //                    if (newStep != steps.get(steps.size() - 1))
    //                        newStep.findViewById(R.id.line).setVisibility(View.VISIBLE);
                        newStep.findViewById(R.id.step_content).setVisibility(View.GONE);
                    }
                }
            });

    //        ((RelativeLayout) findViewById(R.id.small_stepper)).addView(newStep);
            Array_RelativeLayout_steps.add(newStep);
        }
    */
    private void init() {
        appBarLayout.setExpanded(true);
        /// calculate date today
        calendar_selected_day = Calendar.getInstance();
        String today = days[calendar_selected_day.get(Calendar.DAY_OF_WEEK) - 1] +
                ", " + calendar_selected_day.get(Calendar.DAY_OF_MONTH) + " " + months[calendar_selected_day.get(Calendar.MONTH)];
        date.getEditText().setText(today);
        date.getEditText().setEnabled(false);
        title_input_layout.getEditText().setText("");
        note.getEditText().setText("");
    }

    public static void tempFun() {
        add_target_steps.setVisibility(View.VISIBLE);
        appBarLayout.setExpanded(true);
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
                String thisday = days[calendar_selected_day.get(Calendar.DAY_OF_WEEK) - 1] + ", " + calendar_selected_day.get(Calendar.DAY_OF_MONTH) + " " + months[calendar_selected_day.get(Calendar.MONTH)];
                date.getEditText().setText(thisday);

            }
        }, year, month, day);
        pickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        pickerDialog.show();
    }

    void open_keyboard(EditText textInputLayout) {
        textInputLayout.requestFocus();     // editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
        imm.showSoftInput(textInputLayout, InputMethodManager.SHOW_IMPLICIT); //    first param -> editText

    }

    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}