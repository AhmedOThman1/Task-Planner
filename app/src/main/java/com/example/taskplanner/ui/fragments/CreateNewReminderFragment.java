package com.example.taskplanner.ui.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.taskplanner.AlarmReceiver;
import com.example.taskplanner.R;
import com.example.taskplanner.model.Reminder;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;
import static com.example.taskplanner.ui.activities.MainActivity.Reminders;
import static com.example.taskplanner.ui.fragments.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.activities.MainActivity.hideNav;
import static com.example.taskplanner.ui.activities.MainActivity.open_drawer;
import static com.example.taskplanner.ui.activities.MainActivity.showNav;

public class CreateNewReminderFragment extends Fragment {

    TextInputLayout date, title_input_layout;
    CollapsingToolbarLayout coolToolbar;
    AppBarLayout appBarLayout;
    ChipGroup time_chips, week_days1, week_days2, repeats_chips;
    Chip time_set_time;
    Chip[] days = new Chip[7];
    Calendar calendar_selected_day, calendar, untilCalendar;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    FirebaseUser currentUser;
    RelativeLayout repeat_content, every_rl;
    TextView repeat_period, time, eventtv, until_date;
    Spinner repeat_spinner;
    EditText for_num_of_events, repeat_number;

    final int SOUND_CODE = 5;
    boolean tempbool = false;

    public CreateNewReminderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_create_reminder, container, false);


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow));
        }

        date = view.findViewById(R.id.date_input_layout);
        coolToolbar = view.findViewById(R.id.coolToolbar);
        title_input_layout = view.findViewById(R.id.title_input_layout);
        appBarLayout = view.findViewById(R.id.AppBarL);
        time_set_time = view.findViewById(R.id.time_set_time);
        time_chips = view.findViewById(R.id.time_chips);
        repeats_chips = view.findViewById(R.id.repeats_chips);
        week_days1 = view.findViewById(R.id.week_days1);
        week_days2 = view.findViewById(R.id.week_days2);
        repeat_content = view.findViewById(R.id.repeat_content);
        repeat_period = view.findViewById(R.id.repeat_period);
        time = view.findViewById(R.id.time);
        repeat_spinner = view.findViewById(R.id.repeat_spinner);
        for_num_of_events = view.findViewById(R.id.for_num_of_events);
        eventtv = view.findViewById(R.id.eventtv);
        until_date = view.findViewById(R.id.until_date);
        repeat_number = view.findViewById(R.id.repeat_number);
        every_rl = view.findViewById(R.id.every_rl);
        days[0] = view.findViewById(R.id.day2);
        days[1] = view.findViewById(R.id.day3);
        days[2] = view.findViewById(R.id.day4);
        days[3] = view.findViewById(R.id.day5);
        days[4] = view.findViewById(R.id.day6);
        days[5] = view.findViewById(R.id.day7);
        days[6] = view.findViewById(R.id.day1);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        calendar = Calendar.getInstance();
        init();

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

//        view.findViewById(R.id.repeatstv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(getContext() , RemindersFragment.class));
//            }
//        });
//        view.findViewById(R.id.timetv).setOnClickListener(new View.OnClickListener() {   //TODO move these code to setting activity
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
//                startActivityForResult(intent, SOUND_CODE);
//            }
//        });
/// to show date picker and select date & put it in date TextView
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice_date();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice_date();
            }
        });
        date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice_date();
            }
        });
        time_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice_time();
            }
        });
        time_chips.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                switch (i) {
                    case R.id.time_morning:
                        time.setText("8:00 AM");
                        calendar_selected_day.set(Calendar.HOUR_OF_DAY, 8);
                        calendar_selected_day.set(Calendar.MINUTE, 0);
                        calendar_selected_day.set(Calendar.SECOND, 0);
                        calendar_selected_day.set(Calendar.MILLISECOND, 0);
                        break;
                    case R.id.time_afternoon:
                        time.setText("1:00 PM");
                        calendar_selected_day.set(Calendar.HOUR_OF_DAY, 13);
                        calendar_selected_day.set(Calendar.MINUTE, 0);
                        calendar_selected_day.set(Calendar.SECOND, 0);
                        calendar_selected_day.set(Calendar.MILLISECOND, 0);
                        break;
                    case R.id.time_evening:
                        time.setText("6:00 PM");
                        calendar_selected_day.set(Calendar.HOUR_OF_DAY, 18);
                        calendar_selected_day.set(Calendar.MINUTE, 0);
                        calendar_selected_day.set(Calendar.SECOND, 0);
                        calendar_selected_day.set(Calendar.MILLISECOND, 0);
                        break;
                    case R.id.time_night:
                        time.setText("9:00 PM");
                        calendar_selected_day.set(Calendar.HOUR_OF_DAY, 21);
                        calendar_selected_day.set(Calendar.MINUTE, 0);
                        calendar_selected_day.set(Calendar.SECOND, 0);
                        calendar_selected_day.set(Calendar.MILLISECOND, 0);
                        break;
                }
            }
        });
        week_days1.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                if (i == -1) {
                    switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                        case 1:
                            week_days1.check(R.id.day2);
                            break;

                        case 2:
                            week_days1.check(R.id.day3);
                            break;

                        case 3:
                            week_days1.check(R.id.day4);
                            break;

                        case 7:
                            week_days1.check(R.id.day1);
                            break;
                    }
                }
            }
        });
        week_days2.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                if (i == -1) {
                    switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                        case 4:
                            week_days2.check(R.id.day5);
                            break;

                        case 5:
                            week_days2.check(R.id.day6);
                            break;

                        case 6:
                            week_days2.check(R.id.day7);
                            break;

                    }
                }
            }
        });
        repeats_chips.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Log.w("Lol", "ChipId: " + i);
                switch (i) {
                    case -1:
                        repeats_chips.check(R.id.do_not_repeat);
                        repeat_content.setVisibility(View.GONE);
                        break;
                    case R.id.do_not_repeat:
                        repeat_content.setVisibility(View.GONE);
                        break;
                    case R.id.repeat_daily:
                        repeat_period.setText("day");
                        repeat_content.setVisibility(View.VISIBLE);
                        every_rl.setVisibility(View.VISIBLE);
                        week_days1.setVisibility(View.GONE);
                        week_days2.setVisibility(View.GONE);
                        break;
                    case R.id.repeat_monthly:
                        repeat_period.setText("month");
                        repeat_content.setVisibility(View.VISIBLE);
                        every_rl.setVisibility(View.VISIBLE);
                        week_days1.setVisibility(View.GONE);
                        week_days2.setVisibility(View.GONE);
                        break;
                    case R.id.repeat_yearly:
                        repeat_period.setText("year");
                        repeat_content.setVisibility(View.VISIBLE);
                        every_rl.setVisibility(View.VISIBLE);
                        week_days1.setVisibility(View.GONE);
                        week_days2.setVisibility(View.GONE);
                        break;
                    case R.id.repeat_weekly:
                        repeat_period.setText("week");
                        repeat_content.setVisibility(View.VISIBLE);
                        week_days1.setVisibility(View.VISIBLE);
                        week_days2.setVisibility(View.VISIBLE);
                        every_rl.setVisibility(View.GONE);

                        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                            case 1:
                                week_days1.check(R.id.day2);
                                break;

                            case 2:
                                week_days1.check(R.id.day3);
                                break;

                            case 3:
                                week_days1.check(R.id.day4);
                                break;

                            case 4:
                                week_days2.check(R.id.day5);
                                break;

                            case 5:
                                week_days2.check(R.id.day6);
                                break;

                            case 6:
                                week_days2.check(R.id.day7);
                                break;

                            case 7:
                                week_days1.check(R.id.day1);
                                break;


                        }
                        break;

                }
            }
        });
        repeat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        eventtv.setVisibility(View.GONE);
                        for_num_of_events.setVisibility(View.GONE);
                        until_date.setVisibility(View.GONE);
                        tempbool = false;
                        break;
                    case 1:
                        eventtv.setVisibility(View.GONE);
                        for_num_of_events.setVisibility(View.GONE);
                        until_date.setVisibility(View.VISIBLE);
                        String today = calendar_selected_day.get(Calendar.DAY_OF_MONTH) + "/" + calendar_selected_day.get(Calendar.MONTH) + "/" + calendar_selected_day.get(Calendar.YEAR);
                        until_date.setText(today);
                        tempbool = true;
                        break;
                    case 2:
                        eventtv.setVisibility(View.VISIBLE);
                        until_date.setVisibility(View.GONE);
                        for_num_of_events.setVisibility(View.VISIBLE);
                        for_num_of_events.setText("1");
                        tempbool = false;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                eventtv.setVisibility(View.GONE);
                for_num_of_events.setVisibility(View.GONE);
                until_date.setVisibility(View.GONE);
                tempbool = false;
            }
        });

        until_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempbool) {
                    close_keyboard();
                    int year = calendar_selected_day.get(Calendar.YEAR);
                    int month = calendar_selected_day.get(Calendar.MONTH);
                    int day = calendar_selected_day.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            untilCalendar = Calendar.getInstance();
                            untilCalendar.set(Calendar.YEAR, year);
                            untilCalendar.set(Calendar.MONTH, month);
                            untilCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String thisday = untilCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (untilCalendar.get(Calendar.MONTH) + 1) + "/" + untilCalendar.get(Calendar.YEAR);
                            until_date.setText(thisday);


                        }
                    }, year, month, day);
                    pickerDialog.getDatePicker().setMinDate(calendar_selected_day.getTimeInMillis() - 1000);
                    pickerDialog.show();
                }
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (Math.abs(i) == appBarLayout.getTotalScrollRange()) {
                    coolToolbar.setTitle("Create new reminder");
                    appBarLayout.setBackgroundResource(R.drawable.background_toolbar2);
                } else if (Math.abs(i) < (appBarLayout.getTotalScrollRange()) / 2) {
                    coolToolbar.setTitle("");
                    appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
                } else {
                    coolToolbar.setTitle("Create new reminder");
                    appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
                }
            }
        });

        title_input_layout.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!title_input_layout.getEditText().getText().toString().isEmpty())
                    title_input_layout.setError(null);

                return false;
            }
        });

        view.findViewById(R.id.create_reminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                close_keyboard();

                calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 9);


                if (title_input_layout.getEditText().getText().toString().trim().isEmpty()) {
                    title_input_layout.setError("Can't be empty");
                    title_input_layout.requestFocus();
                    open_keyboard(title_input_layout.getEditText());
                    appBarLayout.setExpanded(true);
                } else if (time.getText().toString().isEmpty()) {
                    title_input_layout.setError(null);
                    Toast.makeText(getContext(), "Please select the time", Toast.LENGTH_SHORT).show();
                    view.findViewById(R.id.time_layout).setBackground(getResources().getDrawable(R.drawable.background_error));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.findViewById(R.id.time_layout).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        }
                    }, 1000);
                } else if (calendar_selected_day.before(Calendar.getInstance())) {
                    title_input_layout.setError(null);
                    Toast.makeText(getContext(), "Reminder can't be in the past!", Toast.LENGTH_SHORT).show();
                } else {
                    title_input_layout.setError(null);
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(getContext(), AlarmReceiver.class);
                    String thisday = calendar_selected_day.get(Calendar.DAY_OF_MONTH) + "/" + calendar_selected_day.get(Calendar.MONTH) + "/" + calendar_selected_day.get(Calendar.YEAR);
                    String t = calendar_selected_day.get(Calendar.HOUR_OF_DAY) + ":" + calendar_selected_day.get(Calendar.MINUTE), type = "", AM_PM = "";
                    myIntent.putExtra("Content", t);
                    myIntent.putExtra("title", title_input_layout.getEditText().getText().toString().trim());

                    calendar_selected_day.set(Calendar.SECOND, 0);
                    calendar_selected_day.set(Calendar.MILLISECOND, 0);


                    if (repeats_chips.getCheckedChipId() == R.id.do_not_repeat) {
                        assert alarmManager != null;
                        myIntent.putExtra("type", "do_not");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, calendar_selected_day.getTimeInMillis(), pendingIntent);
                        t = calendar_selected_day.get(Calendar.HOUR_OF_DAY) + ":" + calendar_selected_day.get(Calendar.MINUTE);
                        AM_PM = (calendar_selected_day.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                        Reminders.add(new Reminder(t, AM_PM, thisday, title_input_layout.getEditText().getText().toString().trim(), "do_not"));
                        uploadReminders(currentUser);
                        Toast.makeText(getContext(), "Done \uD83D\uDE0C♥️", Toast.LENGTH_SHORT).show();
                        Log.w("Lol", "noti time :" + t);
                    } else if (repeats_chips.getCheckedChipId() == R.id.repeat_daily || repeats_chips.getCheckedChipId() == R.id.repeat_weekly || repeats_chips.getCheckedChipId() == R.id.repeat_monthly || repeats_chips.getCheckedChipId() == R.id.repeat_yearly) {
                        assert alarmManager != null;
                        if (repeat_number.getText().toString().trim().isEmpty() && repeats_chips.getCheckedChipId() != R.id.repeat_weekly) {
                            repeat_number.setError("Can't be empty");
                        } else {
                            if (repeat_spinner.getSelectedItemPosition() == 0) {
                                myIntent.putExtra("type", "forever");
                                type = "forever";
                                if (repeats_chips.getCheckedChipId() != R.id.repeat_weekly) {
                                    Reminders.add(new Reminder(t, AM_PM, thisday, title_input_layout.getEditText().getText().toString().trim(), "forever"));
                                    uploadReminders(currentUser);
                                }

                            } else if (repeat_spinner.getSelectedItemPosition() == 1) {
                                myIntent.putExtra("type", "until");
                                String day = untilCalendar.get(Calendar.DAY_OF_MONTH) + "/" + untilCalendar.get(Calendar.MONTH) + "/" + untilCalendar.get(Calendar.YEAR);
                                myIntent.putExtra("until", day);
                                type = "until";
                                if (repeats_chips.getCheckedChipId() != R.id.repeat_weekly) {
                                    Reminders.add(new Reminder(t, AM_PM, thisday, title_input_layout.getEditText().getText().toString().trim(), "until", day));
                                    uploadReminders(currentUser);
                                }

                            } else if (repeat_spinner.getSelectedItemPosition() == 2) {
                                if (for_num_of_events.getText().toString().trim().isEmpty()) {
                                    for_num_of_events.setError("Can't be empty");
                                } else {
                                    Calendar c = (Calendar) calendar_selected_day.clone();
                                    int num_of_days = Integer.parseInt(repeat_number.getText().toString().trim()) * Integer.parseInt(for_num_of_events.getText().toString().trim());
                                    c.add(Calendar.DAY_OF_MONTH, num_of_days);
                                    myIntent.putExtra("type", "until");
                                    String day = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
                                    myIntent.putExtra("until", day);
                                    for_num_of_events.setError(null);
                                    type = "until";
                                    if (repeats_chips.getCheckedChipId() != R.id.repeat_weekly) {
                                        Reminders.add(new Reminder(t, AM_PM, thisday, title_input_layout.getEditText().getText().toString().trim(), "until", day));
                                        uploadReminders(currentUser);
                                    }

                                }
                            }

                            if (repeats_chips.getCheckedChipId() == R.id.repeat_weekly) {
                                myIntent.putExtra("week", true);
                                boolean[] bb = new boolean[]{
                                        days[0].isChecked(),
                                        days[1].isChecked(),
                                        days[2].isChecked(),
                                        days[3].isChecked(),
                                        days[4].isChecked(),
                                        days[5].isChecked(),
                                        days[6].isChecked()
                                };

                                ArrayList b = new ArrayList();
                                b.add(days[0].isChecked());
                                b.add(days[1].isChecked());
                                b.add(days[2].isChecked());
                                b.add(days[3].isChecked());
                                b.add(days[4].isChecked());
                                b.add(days[5].isChecked());
                                b.add(days[6].isChecked());

                                myIntent.putExtra("check", bb);
                                Reminders.add(new Reminder(t, AM_PM, thisday, type, title_input_layout.getEditText().getText().toString().trim(), true, b));
                                uploadReminders(currentUser);
                            }

                            if ((repeat_spinner.getSelectedItemPosition() == 2 && !for_num_of_events.getText().toString().trim().isEmpty()) || (repeat_spinner.getSelectedItemPosition() != 2)) {
                                repeat_number.setError(null);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                if (repeats_chips.getCheckedChipId() == R.id.repeat_daily)
                                    alarmManager.setRepeating(AlarmManager.RTC, calendar_selected_day.getTimeInMillis(), (24 * Integer.parseInt(repeat_number.getText().toString().trim())) * 60 * 60 * 1000, pendingIntent);
                                else if (repeats_chips.getCheckedChipId() == R.id.repeat_weekly)
                                    alarmManager.setRepeating(AlarmManager.RTC, calendar_selected_day.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
                                else if (repeats_chips.getCheckedChipId() == R.id.repeat_monthly)
                                    alarmManager.setRepeating(AlarmManager.RTC, calendar_selected_day.getTimeInMillis(), (30 * Integer.parseInt(repeat_number.getText().toString().trim())) * 24 * 60 * 60 * 1000, pendingIntent);
                                else if (repeats_chips.getCheckedChipId() == R.id.repeat_yearly)
                                    alarmManager.setRepeating(AlarmManager.RTC, calendar_selected_day.getTimeInMillis(), (12 * Integer.parseInt(repeat_number.getText().toString().trim())) * 30 * 24 * 60 * 60 * 1000, pendingIntent);

                                Toast.makeText(getContext(), "Done \uD83D\uDE0C♥️", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    init();
                }
            }
        });
        return view;
    }


    public static void uploadReminders(FirebaseUser currentUser) {
        Log.w("Lol2", "CreateNewTask , uploadTargets , Reminders.size :" + Reminders.size());
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Reminders/").setValue(Reminders);
        Log.w("Lol2", "-----------------create , End of upload Reminders-------------------");
    }

    private void choice_time() {
        close_keyboard();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        calendar_selected_day.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
        calendar_selected_day.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendar_selected_day.set(Calendar.AM_PM, calendar.get(Calendar.AM_PM));
        calendar.set(Calendar.YEAR, calendar_selected_day.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar_selected_day.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar_selected_day.get(Calendar.DAY_OF_MONTH));

        int Chour = calendar.get(Calendar.HOUR);
        int Cminute = calendar.get(Calendar.MINUTE);
        if (calendar.get(Calendar.AM_PM) == Calendar.PM)
            Chour += 12;
        TimePickerDialog pickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar_selected_day.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar_selected_day.set(Calendar.MINUTE, minute);
                calendar_selected_day.set(Calendar.SECOND, 0);
                calendar_selected_day.set(Calendar.MILLISECOND, 0);
                time_chips.clearCheck();
                String t = (calendar_selected_day.get(Calendar.HOUR) == 0 ? 12 : calendar_selected_day.get(Calendar.HOUR)) + ":" +
                        (calendar_selected_day.get(Calendar.MINUTE) < 10 ? "0" + calendar_selected_day.get(Calendar.MINUTE) : calendar_selected_day.get(Calendar.MINUTE)) + " " + (calendar_selected_day.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                time.setText(t);
                if (hourOfDay < 7) {
                    // you should be sleep
                    shouldSleep();
                }
                calendar = (Calendar) calendar_selected_day.clone();
            }
        }, Chour, Cminute, false);
        //getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        pickerDialog.show();
    }

    private void shouldSleep() {
        builder = new AlertDialog.Builder(getContext());

        View sleep_dialog = getLayoutInflater().inflate(R.layout.sleep_dialog, null);

        TextView ok = sleep_dialog.findViewById(R.id.ok_dialog),
                cancel = sleep_dialog.findViewById(R.id.cancel_dialog),
                sl = sleep_dialog.findViewById(R.id.sltxt);

        cancel.setText(R.string.ignore_sleep);
        sl.setText(R.string.ok_sleep);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Ok️\uD83D\uDE25", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Love you ,babe\uD83D\uDE0D\uD83D\uDE48\uD83D\uDE48♥️", Toast.LENGTH_LONG).show();
                choice_time();

                dialog.dismiss();
            }
        });

        builder.setView(sleep_dialog).setCancelable(false);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
/*
    public static void uploadReminder(FirebaseUser currentUser) {
        Log.w("Lol2", "CreateNewTask , uploadReminders , Reminders.size :" + Reminders.size() + "\n");
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Reminders/").setValue(Reminders);
        Log.w("Lol2", "-----------------create , End of upload Reminders-------------------");
    }
    private void add_reminder(String reminderTitle, Calendar date) {


        uploadReminder(currentUser);
    }
*/

    private void init() {
        /// calculate date today
        calendar_selected_day = Calendar.getInstance();
        calendar = Calendar.getInstance();
        String today = calendar_selected_day.get(Calendar.DAY_OF_MONTH) + "/" + (calendar_selected_day.get(Calendar.MONTH) + 1) + "/" + calendar_selected_day.get(Calendar.YEAR);
        if (calendar_selected_day.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                calendar_selected_day.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                calendar_selected_day.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))
            today = "ToDay";
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        if (calendar_selected_day.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                calendar_selected_day.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                calendar_selected_day.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))
            today = "Tomorrow";

        date.getEditText().setText(today);
        date.getEditText().setEnabled(false);
        title_input_layout.getEditText().setText("");
        time.setText("");
        time_chips.clearCheck();
        repeats_chips.check(R.id.do_not_repeat);
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

                String thisday = calendar_selected_day.get(Calendar.DAY_OF_MONTH) + "/" + (calendar_selected_day.get(Calendar.MONTH) + 1) + "/" + calendar_selected_day.get(Calendar.YEAR);
                Calendar calendar = Calendar.getInstance();
                if (calendar_selected_day.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        calendar_selected_day.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        calendar_selected_day.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))
                    thisday = "ToDay";
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                if (calendar_selected_day.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        calendar_selected_day.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        calendar_selected_day.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))
                    thisday = "Tomorrow";

                date.getEditText().setText(thisday);

                time_chips.clearCheck();

            }
        }, year, month, day);
        pickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        pickerDialog.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == SOUND_CODE) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            // TODO move these code to setting activity
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
                String title = ringtone.getTitle(getContext());
                Log.w("Lol", title + " uri " + uri.toString());

            }

        }
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