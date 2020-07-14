package com.example.taskplanner.ui;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.taskplanner.model.Activeprojects;
import com.example.taskplanner.model.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProjectViewModel extends ViewModel {

    MutableLiveData<ArrayList<Activeprojects>> ProjectsMutableLiveData = new MutableLiveData<>();

    private ArrayList<Activeprojects> Projects;

    private boolean first = true;

    private ArrayList<Activeprojects> getProjectsFromDatabase(FirebaseUser currentUser) {
        Projects = new ArrayList<>();

        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Projects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(first) {
                    Projects.clear();
                    Log.w("Log", "num : " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Activeprojects project = new Activeprojects(
                                data.child("progress").getValue(Integer.class),
                                data.child("title").getValue(String.class),
                                data.child("time").getValue(String.class));
                        if (!Projects.contains(project))
                            Projects.add(project);
                        getThisProjectTasksFromDatabase(data.getKey(), currentUser);
                    }

                    ProjectsMutableLiveData.setValue(Projects);
                    first = false;
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed1 to read value.", error.toException());
            }
        });

        return Projects;
    }

    private void getThisProjectTasksFromDatabase(final String projectNum, final FirebaseUser currentUser) {
        // Read from the database
        DatabaseReference myDbRef = FirebaseDatabase.getInstance().getReference();
        myDbRef.child(currentUser.getUid() + "/Projects/" + projectNum + "/project_tasks/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (Integer.parseInt(projectNum)<Projects.size())
                    Projects.get(Integer.parseInt(projectNum)).getProject_tasks().clear();

                if (dataSnapshot.exists())
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Tasks task = new Tasks(
                                data.child("projectName").getValue(String.class),
                                data.child("task_name").getValue(String.class),
                                data.child("start_calendar").getValue(String.class),
                                data.child("end_calendar").getValue(String.class),
                                data.child("task_description").getValue(String.class),
                                data.child("toindone").getValue(String.class)
                        );

                        if (Integer.parseInt(projectNum)<Projects.size()&&!Projects.get(Integer.parseInt(projectNum)).getProject_tasks().contains(task))
                        {
                            Projects.get(Integer.parseInt(projectNum)).getProject_tasks().add(task);
                            ProjectsMutableLiveData.setValue(Projects);
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Lol", "Failed2 to read value.", error.toException());
            }
        });
    }

    public void getProjects(FirebaseUser user) {
        ArrayList<Activeprojects> projects = getProjectsFromDatabase(user);
        ProjectsMutableLiveData.setValue(projects);
    }

}
