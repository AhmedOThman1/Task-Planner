package com.example.taskplanner.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.taskplanner.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.taskplanner.ui.Launcher.SHARED_PREF;
import static com.example.taskplanner.ui.Launcher.USER_NAME;

public class TodayActivity extends AppCompatActivity {

    CollapsingToolbarLayout coolToolbar;
    TextView addTask, hello, mon_year,
            day1_name, day2_name, day3_name, day4_name, day5_name, day6_name, day7_name,
            day1_num, day2_num, day3_num, day4_num, day5_num, day6_num, day7_num;

    Calendar calendar;
    ArrayList<ImageView> hours_arr_iv = new ArrayList<>();
    ArrayList<TextView> hours_arr_tv = new ArrayList<>();

    RelativeLayout relativeLayout;

    int relCount = 0;
    public static String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};


    private static final String TAG = "Create";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        coolToolbar = findViewById(R.id.coolToolbar);
        addTask = findViewById(R.id.add_task);
        hello = findViewById(R.id.hello_name);
        mon_year = findViewById(R.id.mon_year);

        day1_name = findViewById(R.id.day1_name);
        day2_name = findViewById(R.id.day2_name);
        day3_name = findViewById(R.id.day3_name);
        day4_name = findViewById(R.id.day4_name);
        day5_name = findViewById(R.id.day5_name);
        day6_name = findViewById(R.id.day6_name);
        day7_name = findViewById(R.id.day7_name);

        day1_num = findViewById(R.id.day1_num);
        day2_num = findViewById(R.id.day2_num);
        day3_num = findViewById(R.id.day3_num);
        day4_num = findViewById(R.id.day4_num);
        day5_num = findViewById(R.id.day5_num);
        day6_num = findViewById(R.id.day6_num);
        day7_num = findViewById(R.id.day7_num);

        relativeLayout = findViewById(R.id.tasks_time_container);

        SharedPreferences share = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String[] name = share.getString(USER_NAME, "").split(" ");
        String fname = "Productive Day, " + name[0];
        hello.setText(fname);

        calendar = Calendar.getInstance();
        String month_name = (String) android.text.format.DateFormat.format("MMMM", new Date());
        int year = calendar.get(Calendar.YEAR);
        mon_year.setText(month_name + ", " + year);

        int day = calendar.get(Calendar.DAY_OF_MONTH),
                dayIndx = calendar.get(Calendar.DAY_OF_WEEK);
        day1_num.setText(day + "");
        day1_name.setText(days[dayIndx - 1]);

        calendar.add(Calendar.DATE, 1);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayIndx = calendar.get(Calendar.DAY_OF_WEEK);
        day2_num.setText(day + "");
        day2_name.setText(days[dayIndx - 1]);

        calendar.add(Calendar.DATE, 1);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayIndx = calendar.get(Calendar.DAY_OF_WEEK);
        day3_num.setText(day + "");
        day3_name.setText(days[dayIndx - 1]);

        calendar.add(Calendar.DATE, 1);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayIndx = calendar.get(Calendar.DAY_OF_WEEK);
        day4_num.setText(day + "");
        day4_name.setText(days[dayIndx - 1]);


        calendar.add(Calendar.DATE, 1);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayIndx = calendar.get(Calendar.DAY_OF_WEEK);
        day5_num.setText(day + "");
        day5_name.setText(days[dayIndx - 1]);

        calendar.add(Calendar.DATE, 1);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayIndx = calendar.get(Calendar.DAY_OF_WEEK);
        day6_num.setText(day + "");
        day6_name.setText(days[dayIndx - 1]);

        calendar.add(Calendar.DATE, 1);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayIndx = calendar.get(Calendar.DAY_OF_WEEK);
        day7_num.setText(day + "");
        day7_name.setText(days[dayIndx - 1]);

        calendar = Calendar.getInstance();

        AppBarLayout appBarLayout = findViewById(R.id.AppBarL);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (Math.abs(i) == appBarLayout.getTotalScrollRange()) {
                    coolToolbar.setTitle("Today");
                    appBarLayout.setBackgroundResource(R.drawable.background_toolbar2);
                } else if (Math.abs(i) < (appBarLayout.getTotalScrollRange()) / 2) {
                    coolToolbar.setTitle("");
                    appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
                } else {
                    coolToolbar.setTitle("Today");
                    appBarLayout.setBackgroundResource(R.drawable.background_toolbar);
                }
            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTask = new Intent(TodayActivity.this, CreateNewTaskFragment.class);
                startActivity(addTask);
            }
        });


        /** if the text less than 4 word add description , if
         the per less than 1 hour description gone
         if the description ="" && title less than 4 words centerpar = true

         <RelativeLayout
         android:id="@+id/your_task"
         android:layout_width="match_parent"
         android:background="@drawable/background_createtask"
         android:layout_marginEnd="24dp"
         android:layout_marginStart="24dp"
         android:layout_toEndOf="@id/h7AM"
         android:layout_marginTop="-10dp"       // -50 + minutes

         all above const

         android:layout_height="120dp"          //  2 hours = 120 minutes
         android:layout_below="@id/h9AM"        //  if the task begin 7:20
         it 'll be below 7 it's now 7:10 & adding -50 + 20 = -30 then the minutes = 20
         >
         <TextView
         android:layout_width="wrap_content"
         android:layout_height="wrap_content"
         android:text="Project Research"
         android:fontFamily="@font/poppins_medium"
         android:textColor="#FFF"
         android:textSize="16sp"
         android:layout_marginTop="8dp"
         android:layout_marginStart="16dp"
         />
         </RelativeLayout>
         **/

        hours_arr_tv.add((TextView) findViewById(R.id.h12AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h1AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h2AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h3AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h4AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h5AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h6AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h7AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h8AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h9AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h10AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h11AM));
        hours_arr_tv.add((TextView) findViewById(R.id.h12PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h1PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h2PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h3PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h4PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h5PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h6PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h7PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h8PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h9PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h10PM));
        hours_arr_tv.add((TextView) findViewById(R.id.h11PM));


        hours_arr_iv.add((ImageView) findViewById(R.id.d12AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d1AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d2AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d3AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d4AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d5AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d6AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d7AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d8AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d9AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d10AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d11AM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d12PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d1PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d2PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d3PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d4PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d5PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d6PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d7PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d8PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d9PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d10PM));
        hours_arr_iv.add((ImageView) findViewById(R.id.d11PM));


        for (int i = 1; i <= 6; i++) {
            hours_arr_tv.get(i).setVisibility(View.GONE);
            hours_arr_iv.get(i).setVisibility(View.GONE);
        }
//
//        fun();

    }
//
//    void fun() {
//        Calendar now = Calendar.getInstance() , yesterday = Calendar.getInstance();
//
//        yesterday.add(Calendar.DAY_OF_MONTH,-1);
//
//        for (int i = 0; i < Projects.size(); i++)
//            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++) {
//                Calendar start_calendar = Projects.get(i).getProject_tasks().get(j).getStart_calendar(),
//                        end_calendar = Projects.get(i).getProject_tasks().get(j).getEnd_calendar(),
//                        temp;
//
//                temp = (Calendar) end_calendar.clone();
//                temp.set(Calendar.HOUR_OF_DAY, 0);
//                temp.set(Calendar.MINUTE,0);
//                temp.set(Calendar.SECOND,0);
//                temp.set(Calendar.MILLISECOND,0);
//
//                if (start_calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
//                        start_calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
//                        start_calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
//
//                    boolean start_ampm = (start_calendar.get(Calendar.AM_PM) == Calendar.AM),
//                            end_ampm = (end_calendar.get(Calendar.AM_PM) == Calendar.AM);
//
//                    long difference = end_calendar.getTimeInMillis() - start_calendar.getTimeInMillis();
//                    int minutes = (int) (difference / (1000 * 60));
//                    if (start_ampm && start_calendar.get(Calendar.HOUR_OF_DAY) < 7 )
//                        for (int k = start_calendar.get(Calendar.HOUR_OF_DAY); k <= 6; k++) {
//                            hours_arr_tv.get(k).setVisibility(View.VISIBLE);
//                            hours_arr_iv.get(k).setVisibility(View.VISIBLE);
//                        }
//
//                    RelativeLayout newRelative = (RelativeLayout) getLayoutInflater().inflate(R.layout.task_card, null);
//                    //new RelativeLayout(this);
//
//                    TextView task_title = newRelative.findViewById(R.id.task_title),
//                            task_description = newRelative.findViewById(R.id.task_description);
//
//                    task_title.setText(Projects.get(i).getProject_tasks().get(j).getTask_name());
//                    task_description.setText(Projects.get(i).getProject_tasks().get(j).getTask_description());
//
//                    if (start_calendar.get(Calendar.AM_PM) == Calendar.PM && end_calendar.get(Calendar.AM_PM) == Calendar.AM)
//                    {
////                        Log.d(TAG,(int) (temp.getTimeInMillis() - start_calendar.getTimeInMillis() / (1000 * 60))+"!!!");
////
////                        Log.d(TAG,( (24%start_calendar.get(Calendar.HOUR_OF_DAY))*60 - start_calendar.get(Calendar.MINUTE) )+"!@!");
//                        minutes = ( (24%start_calendar.get(Calendar.HOUR_OF_DAY))*60 - start_calendar.get(Calendar.MINUTE)  );
//                        if (relCount % 4 == 0)
//                            newRelative.setBackgroundResource(R.drawable.background_zetisq12);
//                        else if (relCount % 4 == 1)
//                            newRelative.setBackgroundResource(R.drawable.background_redsq12);
//                        else if (relCount % 4 == 2)
//                            newRelative.setBackgroundResource(R.drawable.background_yellowsq12);
//                        else if (relCount % 4 == 3)
//                            newRelative.setBackgroundResource(R.drawable.background_bluesq12);
//                    }
//
//                    else {
////                        Log.d(TAG,minutes+"!");
//                        if (relCount % 4 == 0)
//                            newRelative.setBackgroundResource(R.drawable.background_zetisq);
//                        else if (relCount % 4 == 1)
//                            newRelative.setBackgroundResource(R.drawable.background_redsq);
//                        else if (relCount % 4 == 2)
//                            newRelative.setBackgroundResource(R.drawable.background_yellowsq);
//                        else if (relCount % 4 == 3)
//                            newRelative.setBackgroundResource(R.drawable.background_bluesq);
//                    }
//
//                    if (minutes <= 60) {
//
//                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) task_title.getLayoutParams();
//                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                        task_title.setLayoutParams(params);
//                        task_description.setVisibility(View.GONE);
//                        task_title.setEllipsize(TextUtils.TruncateAt.END);
//                        task_title.setMaxLines(1);
//                    } else {
//                        task_description.setEllipsize(TextUtils.TruncateAt.END);
//                        task_description.setMaxLines(1);
//                        task_title.setEllipsize(TextUtils.TruncateAt.END);
//                        task_title.setMaxLines(1);
//                    }
//
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (minutes * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
//
//                    relCount++;
//
//                    newRelative.setId(relCount);
//
//                    params.rightMargin = (int) (24 * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
//                    params.leftMargin = (int) (24 * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
//                    params.topMargin = (int) ((-50 + (start_calendar.get(Calendar.MINUTE))) * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
//
//                    params.addRule(RelativeLayout.END_OF, R.id.h7AM);
//                    int id = hours_arr_tv.get(start_calendar.get(Calendar.HOUR_OF_DAY)).getId();
//                    params.addRule(RelativeLayout.BELOW, id);
//                    newRelative.setLayoutParams(params);
//
//                    final int finalI = i;
//                    final int finalJ = j;
//                    newRelative.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            Toast.makeText(TodayActivity.this, "" + Projects.get(finalI).getProject_tasks().get(finalJ).getTask_name(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                    relativeLayout.addView(newRelative);
//
//                    Toast.makeText(this, "h: " + start_calendar.get(Calendar.HOUR_OF_DAY) + "\nm: " + minutes + "\nm%60: " + minutes % 60 + "\nid: " + id + "\n8id", Toast.LENGTH_SHORT).show();
//                }
//
//                else if (end_calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
//                        end_calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
//                        end_calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) &&
//                        start_calendar.get(Calendar.DAY_OF_MONTH)==yesterday.get(Calendar.DAY_OF_MONTH)){
//
//                    long difference = end_calendar.getTimeInMillis() - temp.getTimeInMillis();
//                    int minutes = (int) (difference / (1000 * 60));
//
//                    for (int k = 0; k <= 6; k++) {
//                        hours_arr_tv.get(k).setVisibility(View.VISIBLE);
//                        hours_arr_iv.get(k).setVisibility(View.VISIBLE);
//                    }
//
//                    RelativeLayout newRelative = (RelativeLayout) getLayoutInflater().inflate(R.layout.task_card, null);
//                    //new RelativeLayout(this);
//
//                    TextView task_title = newRelative.findViewById(R.id.task_title),
//                            task_description = newRelative.findViewById(R.id.task_description);
//
//                    task_title.setText(Projects.get(i).getProject_tasks().get(j).getTask_name());
//                    task_description.setText(Projects.get(i).getProject_tasks().get(j).getTask_description());
//
//                    if (minutes <= 60) {
//
//                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) task_title.getLayoutParams();
//                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                        task_title.setLayoutParams(params);
//                        task_description.setVisibility(View.GONE);
//                        task_title.setEllipsize(TextUtils.TruncateAt.END);
//                        task_title.setMaxLines(1);
//                    } else {
//                        task_description.setEllipsize(TextUtils.TruncateAt.END);
//                        task_description.setMaxLines(1);
//                        task_title.setEllipsize(TextUtils.TruncateAt.END);
//                        task_title.setMaxLines(1);
//                    }
//
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (minutes * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
//                        if (relCount % 4 == 0)
//                            newRelative.setBackgroundResource(R.drawable.background_zetisq12a);
//                        else if (relCount % 4 == 1)
//                            newRelative.setBackgroundResource(R.drawable.background_redsq12a);
//                        else if (relCount % 4 == 2)
//                            newRelative.setBackgroundResource(R.drawable.background_yellowsq12a);
//                        else if (relCount % 4 == 3)
//                            newRelative.setBackgroundResource(R.drawable.background_bluesq12a);
//
//                    relCount++;
//
//                    newRelative.setId(relCount);
//
//                    params.rightMargin = (int) (24 * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
//                    params.leftMargin = (int) (24 * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
//                    params.topMargin = (int) ((-50) * ((float) TodayActivity.this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
//
//                    params.addRule(RelativeLayout.END_OF, R.id.h7AM);
//                    int id = hours_arr_tv.get(0).getId();
//                    params.addRule(RelativeLayout.BELOW, id);
//                    newRelative.setLayoutParams(params);
//
//                    final int finalI = i;
//                    final int finalJ = j;
//                    newRelative.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            Toast.makeText(TodayActivity.this, "" + Projects.get(finalI).getProject_tasks().get(finalJ).getTask_name(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                    relativeLayout.addView(newRelative);
//
//                }
//
//            }
//    }

}
