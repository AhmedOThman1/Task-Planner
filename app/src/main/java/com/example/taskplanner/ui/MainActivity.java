package com.example.taskplanner.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskplanner.R;
import com.example.taskplanner.model.Activeprojects;
import com.example.taskplanner.model.Reminder;
import com.example.taskplanner.model.Target;
import com.example.taskplanner.model.Tasks;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.taskplanner.ui.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.Launcher.LOGIN_TYPE;
import static com.example.taskplanner.ui.Launcher.SHARED_PREF;
import static com.example.taskplanner.ui.Launcher.USER_JOB;
import static com.example.taskplanner.ui.Launcher.USER_NAME;
import static com.example.taskplanner.adapter.StepAdapter.Steps;

public class MainActivity extends AppCompatActivity {

    public static CardView[] cardItems = new CardView[11];
    public static TextView[] textItem = new TextView[11];
    public static CardView mainCard;
    public static ImageView open_drawer;
    public static View clickToCancel;
    public static ProjectViewModel projectViewModel;
    public static TargetViewModel targetViewModel;
    public static ReminderViewModel reminderViewModel;
    public static float x, y, z;
    static int pos = -1;

    static ArrayList<Activeprojects> Projects = new ArrayList<>();
    static ArrayList<Tasks> ToDo_tasks = new ArrayList<>();
    static ArrayList<Tasks> InProgress_tasks = new ArrayList<>();
    static ArrayList<Tasks> Done_tasks = new ArrayList<>();
    static ArrayList<Target> Targets = new ArrayList<>();
    static ArrayList<Reminder> Reminders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = (222 * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        y = (88 * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        z = (10 * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, Launcher.class));
            finish();
        }


        assert currentUser != null;
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid() + "");
        DbRef.keepSynced(true);

        init();

        if (savedInstanceState == null) //show the home fragment
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new HomeFragment()).commit();

        cardItems[0].setOnClickListener(v -> {
            if (pos != 0)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new HomeFragment()).commit();
            setCheck(0, true, MainActivity.this);

        });

        cardItems[1].setOnClickListener(v -> {
            if (pos != 1)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new CalendarFragment()).commit();
            setCheck(1, true, MainActivity.this);
        });

        cardItems[2].setOnClickListener(v -> {
            if (pos != 2)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new CreateNewTaskFragment()).commit();
            setCheck(2, true, MainActivity.this);
        });

        cardItems[3].setOnClickListener(v -> {
            if (pos != 3)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new CreateNewTargetFragment()).commit();
            setCheck(3, true, MainActivity.this);
        });

        cardItems[4].setOnClickListener(v -> {
            if (pos != 4)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new CreateNewReminderFragment()).commit();
            setCheck(4, true, MainActivity.this);
        });

        cardItems[5].setOnClickListener(v -> {
            if (pos != 5)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new HomeFragment()).commit();
            setCheck(5, true, MainActivity.this);
        });

        cardItems[6].setOnClickListener(v -> {
            if (pos != 6)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new TargetsFragment()).commit();
            setCheck(6, true, MainActivity.this);
        });

        cardItems[7].setOnClickListener(v -> {
            if (pos != 7)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new RemindersFragment()).commit();
            setCheck(7, true, MainActivity.this);
        });

        cardItems[8].setOnClickListener(v -> {
            if (pos != 8)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new HomeFragment()).commit();
            setCheck(8, true, MainActivity.this);
        });

        cardItems[9].setOnClickListener(v -> {
            if (pos != 9)
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, new HomeFragment()).commit();
            setCheck(9, true, MainActivity.this);
        });

        cardItems[10].setOnClickListener(v -> {

            Projects.clear();
            ToDo_tasks.clear();
            InProgress_tasks.clear();
            Done_tasks.clear();
            Targets.clear();
            Reminders.clear();

            AuthUI.getInstance()
                    .signOut(MainActivity.this)
                    .addOnCompleteListener(task -> {
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(LOGIN_TYPE, "");
                        editor.putString(USER_NAME, "");
                        editor.putString(USER_JOB, "");
                        editor.apply();
                        startActivity(new Intent(MainActivity.this, Launcher.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Log.w("Log", "Fail " + e.getMessage()));

    });

        clickToCancel.setOnClickListener(v -> hideNav());

    }

    void init() {

        mainCard = findViewById(R.id.main_card);
        clickToCancel = findViewById(R.id.clickToCancel);

        cardItems[0] = findViewById(R.id.card1);
        cardItems[1] = findViewById(R.id.card2);
        cardItems[2] = findViewById(R.id.card3);
        cardItems[3] = findViewById(R.id.card4);
        cardItems[4] = findViewById(R.id.card5);
        cardItems[5] = findViewById(R.id.card6);
        cardItems[6] = findViewById(R.id.card7);
        cardItems[7] = findViewById(R.id.card8);
        cardItems[8] = findViewById(R.id.card9);
        cardItems[9] = findViewById(R.id.card10);
        cardItems[10] = findViewById(R.id.card11);

        textItem[0] = findViewById(R.id.textItem1);
        textItem[1] = findViewById(R.id.textItem2);
        textItem[2] = findViewById(R.id.textItem3);
        textItem[3] = findViewById(R.id.textItem4);
        textItem[4] = findViewById(R.id.textItem5);
        textItem[5] = findViewById(R.id.textItem6);
        textItem[6] = findViewById(R.id.textItem7);
        textItem[7] = findViewById(R.id.textItem8);
        textItem[8] = findViewById(R.id.textItem9);
        textItem[9] = findViewById(R.id.textItem10);
        textItem[10] = findViewById(R.id.textItem11);

    }

    //    static void test(){
//        Log.w("TEST","projects size : "+Projects.size());
//        Log.w("TEST","Targets size : "+Targets.size());
//        Log.w("TEST","Reminders size : "+Reminders.size());
//        Log.w("TEST","to_do size : "+ToDo_tasks.size());
//        Log.w("TEST","in size : "+InProgress_tasks.size());
//        Log.w("TEST","done size : "+Done_tasks.size());
//
//        for (int i = 0; i < Projects.size(); i++) {
//
//            Log.w("TEST","project "+i+" has "+Projects.get(i).getProject_tasks().size()+ " task.");
//            for (int j = 0; j < Projects.get(i).getProject_tasks().size(); j++) {
//                Log.w("TEST","project "+i+" has "+Projects.get(i).getProject_tasks().get(j).getTask_name()+ " task.");
//            }
//        }
//
//
//        for (int i = 0; i < Targets.size(); i++) {
//
//            Log.w("TEST","target "+i+" has "+Targets.get(i).getSteps().size()+ " step.");
//            for (int j = 0; j < Targets.get(i).getSteps().size(); j++) {
//                Log.w("TEST","--- "+j+"  "+Targets.get(i).getSteps().get(j).getName()+ " " +Targets.get(i).getSteps().get(j).getDescription()+ " " +Targets.get(i).getSteps().get(j).isCheck());
//            }
//        }
//
//        if(Steps!=null) {
//            Log.w("TEST", "Steps ");
//            for (int i = 0; i < Steps.size(); i++) {
//                Log.w("TEST", "--- " + i + "  " + Steps.get(i).getName() + " " + Steps.get(i).getDescription() + " " + Steps.get(i).isCheck());
//
//            }
//        }
//    }
    boolean doubleBackToExitPressedOnce = false;

    /**
     * if user click back button
     **/
    @Override
    public void onBackPressed() {
        if (isDrawerOpen) {
            hideNav();
            isDrawerOpen = false;
            open_drawer.setImageResource(R.drawable.ic_menu);
            return;
        }

        Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.frameLayout);

        /* if the current fragment is the One Target fragment then back to the target fragment */

        if (currentFragment instanceof OneTargetFragment) {
            //show the home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new TargetsFragment()).commit();
            //select the home icon in the drawer
            setCheck(6, false, getApplicationContext());
            return;
        }

        /* if the current page isn't home page then go to home page **/

        if (!(currentFragment instanceof HomeFragment)) {
            //show the home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
            //select the home icon in the drawer
            setCheck(0, false, getApplicationContext());
            return;
        }

        /* exit only if the user click the back button twice in the Main Activity**/
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

    }

    public static void hideNav() {
        mainCard.animate().translationX(0).translationY(0).setDuration(500).start();
        mainCard.setRadius(0);
        clickToCancel.setVisibility(View.GONE);
        open_drawer.setImageResource(R.drawable.ic_menu);
        isDrawerOpen = false;
    }

    public static void showNav() {
        mainCard.animate().translationX(x).translationY(y).setDuration(500).start();
        mainCard.setRadius(z);
        clickToCancel.setVisibility(View.VISIBLE);
        open_drawer.setImageResource(R.drawable.ic_back);
        isDrawerOpen = true;
    }

    public static void setCheck(int position, boolean hide, Context context) {
        pos = position;
        for (int i = 0; i < 11; i++) {
            if (i != position) {
                cardItems[i].setCardBackgroundColor(context.getResources().getColor(R.color.white));
                textItem[i].setTextColor(context.getResources().getColor(R.color.black));
                textItem[i].setGravity(Gravity.START);
            } else {
                cardItems[i].setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
                textItem[i].setTextColor(context.getResources().getColor(R.color.white));
                textItem[i].setGravity(Gravity.END);
            }
        }

        if (hide) hideNav();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isDrawerOpen) {
            hideNav();
            open_drawer.setImageResource(R.drawable.ic_menu);
            isDrawerOpen = false;
        }
    }
}
