package com.example.taskplanner.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.taskplanner.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.example.taskplanner.ui.activities.MainActivity.ToDo_tasks;
import static com.example.taskplanner.ui.fragments.TasksFragment.getToInDone;

public class Launcher extends AppCompatActivity {

    public static final String SHARED_PREF = "sharedPrefs";
    public static final String USER_NAME = "username";
    public static final String USER_JOB = "userjob";
    public static final String TODO_TASKS = "todo_tasks";
    public static final String IN_TASKS = "in_tasks";
    public static final String DONE_TASKS = "done_tasks";
    public static final String TODO_STARTED = "todo_started";
    public static final String IN_STARTED = "in_started";
    public static final String MAIN_PROGRESS = "main_progress";
    public static final String LOGIN_TYPE = "login_type";
    public static final String PIC_PATH = "profile_pic";

    static public String SP_FULL_NAME = "";
    static public String SP_JOB = "";

    String FirstTime;

    SharedPreferences sharedPreferences;

    private static final int RC_SIGN_IN = 1;
    DatabaseReference DbRef;
    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancher);
        Log.w("Log", "Launcher<-----------------------");
        /* change status bar color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }


        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        FirstTime = sharedPreferences.getString(LOGIN_TYPE, "");

        if (!FirstTime.equals("")) {

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            SP_FULL_NAME = sharedPreferences.getString(USER_NAME, "");
            SP_JOB = sharedPreferences.getString(USER_JOB, "");
//            SP_PROFILE_IMAGE = sharedPreferences.getString(prof_img,"");


            if (SP_FULL_NAME.equals("")) {
                AuthUI.getInstance()
                        .signOut(Launcher.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString(LOGIN_TYPE, "");
                                editor.putString(USER_NAME, "");
                                editor.putString(USER_JOB, "");
                                editor.apply();
                            }
                        })
                        .addOnFailureListener(e -> Log.w("Log", "Fail " + e.getMessage()));

//                Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.osman)).getBitmap();
//                if (isStoragePermissionGranted(Launcher.this))
//                    saveImage(bitmap, Launcher.this, "jpg");
//                    Intent login = new Intent(Launcher.this, TestLogin.class);
//                    startActivity(login);
//                    finish();
                login();
            } else {
                Toast.makeText(Launcher.this, getResources().getString(R.string.welcome)+" "+ SP_FULL_NAME, Toast.LENGTH_LONG).show();
//            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//            if (currentUser != null) {
//                retrieveProject( currentUser);
//            }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent homeIntent = new Intent(Launcher.this, MainActivity.class);
                        startActivity(homeIntent);
                        finish();

                    }
                }, 1000);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.osman)).getBitmap();
//                    if (isStoragePermissionGranted(Launcher.this))
//                        saveImage(bitmap, Launcher.this, "jpg");
//                    Intent login = new Intent(Launcher.this, TestLogin.class);
//                    startActivity(login);
//                    finish();
                    login();
                }
            }, 1000);
        }

    }

    void login() {
        //stop circular progress bar
        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );


        Random rand = new Random();
        int indx = rand.nextInt(10);

        Log.w("Lol", indx + "<-------");
        switch (indx) {
            case 0:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme0)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 1:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme1)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 2:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme2)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 3:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme3)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 4:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme4)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 5:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme5)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 6:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme6)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 7:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme7)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 8:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme8)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;
            case 9:
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme9)
//                    .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            final IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                DbRef = FirebaseDatabase.getInstance().getReference();
                DbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        assert currentUser != null;
                        if (dataSnapshot.hasChild("" + currentUser.getUid()) && first) {
                            String name = "", job = "";
                            name = dataSnapshot.child(currentUser.getUid() + "/name").getValue(String.class);
                            job = dataSnapshot.child(currentUser.getUid() + "/job").getValue(String.class);
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(USER_NAME, name);
                            editor.putString(USER_JOB, job);
                            assert response != null;
                            editor.putString(LOGIN_TYPE, response.getProviderType());
                            Log.w("Lol2", "MultiLogin 1111" + name + "\t" + job + "\t" + ToDo_tasks.size());
                            editor.apply();

//                            retrieveProject(currentUser);
                            getToInDone(Launcher.this);
                            first = false;
                            if (name == null || name.equals("")) {
                                startActivity(new Intent(Launcher.this, Login.class));
                                finish();
                            } else
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(Launcher.this, MainActivity.class));
                                        finish();
                                    }
                                }, 1000);

                        } else if (first) {
                            DbRef.child(currentUser.getUid() + "/name").setValue(currentUser.getDisplayName());
                            DbRef.child(currentUser.getUid() + "/job").setValue("Student");
                            DbRef.child(currentUser.getUid() + "/profile_pic").setValue(String.valueOf(currentUser.getPhotoUrl()));
                            Log.w("Lol2", "MultiLogin 2222" + currentUser.getDisplayName());

                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(USER_NAME, currentUser.getDisplayName());
                            editor.putString(USER_JOB, "Student");
                            assert response != null;
                            editor.putString(LOGIN_TYPE, response.getProviderType());
                            editor.apply();
                            first = false;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Launcher.this, Login.class));
                                    finish();
                                }
                            }, 1000);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w("Lol", "Failed to read value.", error.toException());


                    }
                });

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response == null) {
                    login();
                    Log.w("TestLogin", "user canceled the sign-in flow using the back button");
                } else {
                    Log.w("TestLogin", Objects.requireNonNull(response.getError()).getErrorCode() + "");
                    Log.w("TestLogin", Objects.requireNonNull(response.getError()).getMessage() + "");
                    Log.w("TestLogin", Objects.requireNonNull(response.getError()).getLocalizedMessage() + "");
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w("Log", "---> start <---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w("Log", "---> stop <---");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w("Log", "---> destroy <---");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("Log", "---> pause <---");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("Log", "---> resume <---");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w("Log", "---> restart <---");

    }

    /*
     * convert Bitmap to String
     /
    static public String encodeImage(Bitmap imagee) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imagee.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] b = bytes.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }


    /**
     * to convert string to Bitmap
     /
    static public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    **/
}
