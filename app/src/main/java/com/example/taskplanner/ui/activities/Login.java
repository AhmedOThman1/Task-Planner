package com.example.taskplanner.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskplanner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.taskplanner.ui.activities.Launcher.LOGIN_TYPE;
import static com.example.taskplanner.ui.activities.Launcher.SHARED_PREF;
import static com.example.taskplanner.ui.activities.Launcher.USER_JOB;
import static com.example.taskplanner.ui.activities.Launcher.USER_NAME;

public class Login extends AppCompatActivity {

    TextInputLayout username, job;
    TextView login;
    CircleImageView profile_image;
    private final static int CODE_PERMISSION = 1, CODE_GAL = 2;
    StorageReference Pic_StorageRef;
    StorageReference DbRef;
    DatabaseReference myDbRef;
    FirebaseUser currentUser;
    boolean p_image = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.w("Log", "LLLLLLLOOOOOOOGGIIN");

        username = findViewById(R.id.username);
        job = findViewById(R.id.job);
        login = findViewById(R.id.login);
        profile_image = findViewById(R.id.profile_image);

        ArrayList<Integer> loginImage = new ArrayList<>();

        loginImage.add(R.drawable.login0);
        loginImage.add(R.drawable.login1);
        loginImage.add(R.drawable.login2);
        loginImage.add(R.drawable.login3);
        loginImage.add(R.drawable.login4);
        loginImage.add(R.drawable.login5);
        loginImage.add(R.drawable.login6);
        loginImage.add(R.drawable.login7);
        loginImage.add(R.drawable.login8);
        loginImage.add(R.drawable.login9);

        Random rand = new Random();
        int indx = rand.nextInt(10);


        findViewById(R.id.parent).setBackground(getResources().getDrawable(loginImage.get(indx)));

        profile_image.bringToFront();
        login.bringToFront();


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DbRef = FirebaseStorage.getInstance().getReference();
        myDbRef = FirebaseDatabase.getInstance().getReference();
        Pic_StorageRef = DbRef.child("profile_pic/" + currentUser.getUid() + "__image");

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        username.getEditText().setText(sharedPreferences.getString(USER_NAME, currentUser.getDisplayName()));
        job.getEditText().setText(sharedPreferences.getString(USER_JOB, "Student"));
        String type = sharedPreferences.getString(LOGIN_TYPE, "");
        if (!type.equals("phone") && !type.equals("password")) {
            profile_image.setImageURI(currentUser.getPhotoUrl());
            p_image = true;

            if (profile_image.getDrawable() == null) {
                profile_image.setImageResource(R.drawable.osman);
                p_image = false;
            }
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Login.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(Login.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_PERMISSION);
                } else {
                    Intent gal = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    gal.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    gal.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                    startActivityForResult(Intent.createChooser(gal, "select media file"), CODE_GAL);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = username.getEditText().getText().toString().trim(),
                        Job = job.getEditText().getText().toString().trim();

                if (confirm_name(Name) && confirm_job(Job) && confirm_photo()) {

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_NAME, Name);
                    editor.putString(USER_JOB, Job);
                    editor.apply();

                    myDbRef.child(currentUser.getUid() + "/name").setValue(Name);
                    myDbRef.child(currentUser.getUid() + "/job").setValue(Job);

                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }
            }
        });


    }

    private boolean confirm_photo() {
        if (!p_image) {
            profile_image.setBorderColor(getResources().getColor(R.color.no_image_border));
            Toast.makeText(this, "Please pick a picture", Toast.LENGTH_SHORT).show();
            return false;
        }
        profile_image.setBorderColor(getResources().getColor(R.color.image_border));
        return true;
    }

    private boolean confirm_name(String name) {
        if (name.isEmpty()) {
            username.setError(getString(R.string.empty));
            username.requestFocus();
            return false;
        }
        username.setError("");
        return true;
    }

    private boolean confirm_job(String Str_job) {
        if (Str_job.isEmpty()) {
            job.setError(getString(R.string.empty));
            job.requestFocus();
            return false;
        }
        job.setError("");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent gal = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    gal.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    gal.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                    startActivityForResult(Intent.createChooser(gal, "select media file"), CODE_GAL);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GAL && resultCode == RESULT_OK) {
            // image from gallery
            assert data != null;
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // one image
                //get image type ( png , jpg , webp )
//                ContentResolver cR = getContentResolver();
//                MimeTypeMap mime = MimeTypeMap.getSingleton();
//                String type = mime.getExtensionFromMimeType(cR.getType(imageUri));
//                Log.w("Log", "image1 type : " + type);
                // end of get image type


                Pic_StorageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Pic_StorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                myDbRef.child(currentUser.getUid() + "/profile_pic").setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }
                        });

                    }
                });
                try {

                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    //TO DONE save this image to the device
                    profile_image.setImageBitmap(bitmap);
                    p_image = true;
//                    if (isStoragePermissionGranted(Login.this)) {
//                        assert type != null;
//                        saveImage(bitmap, Login.this, type);
//                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }



/*

    private boolean confirm_password(String pass) {
        if (pass.isEmpty()) {
            password.setError(getString(R.string.empty));
            password.requestFocus();
            return false;
        }else if(pass.length()<6){
            password.setError(getString(R.string.len_less_6));
            password.requestFocus();
            return false;
        }
        password.setError("");
        return true;
    }

    private boolean confirm_email(String username) {
        if (username.isEmpty()) {
            user.setError(getString(R.string.empty));
            user.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            user.setError(getString(R.string.not_valid));
            user.requestFocus();
            return false;
        }
        user.setError("");
        return true;
    }

    void login( String FBemail , String FBpassword)
    {
        mAuth.signInWithEmailAndPassword(FBemail, FBpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(LOGIN_TYPE,"Email");
                            editor.apply();
                            // Read from the database
                            FirebaseDatabase.getInstance().getReference().child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    String name = dataSnapshot.child("name").getValue(String.class) ,
                                           job  = dataSnapshot.child("job").getValue(String.class) ;
                                    SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString(USER_NAME,name);
                                    editor.putString(USER_JOB,job);
                                    Log.w("Lol2","$%^"+name+"\n"+job);
                                    editor.apply();
                                    */
/**
 retrieveProject("Projects", currentUser);***//*

                                    Intent home = new Intent(Login.this, HomeFragment.class);
                                    startActivity(home);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w("Lol", "Failed to read value.", error.toException());
                                }
                            });

//                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("allMessages").child(user.getUid());
//                                myRef.child("myId").setValue(user.getUid());
//                                myRef.child("name").setValue(user.getDisplayName());
//                                myRef.child("photo").setValue(user.getPhotoUrl());


                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(Login.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            String error = task.getException().getMessage();
                            Log.d("Lol",error);
                            if(error.contains("network error")){
                                RelativeLayout parent = findViewById(R.id.parent);
                                Snackbar.make(parent, "No internet connection", Snackbar.LENGTH_LONG)
                                        .setAction("Open Wifi", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(Settings. ACTION_WIFI_SETTINGS));
                                            }
                                        })
                                        .setActionTextColor(getResources().getColor(R.color.red))
                                        .show();
                            }else if(error.contains("no user record")){
                                user.setError(getString(R.string.not_valid));
                                user.requestFocus();
                            }else if(error.contains("password is invalid")){
                                password.setError(getString(R.string.pass_not_valid));
                                password.requestFocus();
                            }else
                                Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
            Log.d("Llogin","null");
        else
            Log.d("Llogin",currentUser.getUid()) ;
    }
*/


}
