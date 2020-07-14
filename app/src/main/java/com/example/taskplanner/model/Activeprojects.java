package com.example.taskplanner.model;

import java.util.ArrayList;
import java.util.Comparator;

public class Activeprojects {
    private int progress;
    private String title;
    private String time;
    private ArrayList<Tasks> project_tasks;

    public Activeprojects(int progress, String title, String time) {
        this.progress = progress;
        this.title = title;
        this.time = time;
        this.project_tasks = new ArrayList<>();
    }


    public Activeprojects(int progress, String title, String time, ArrayList<Tasks> project_tasks) {
        this.progress = progress;
        this.title = title;
        this.time = time;
        this.project_tasks = project_tasks;
    }

    public ArrayList<Tasks> getProject_tasks() {
        return project_tasks;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    // sorting by project title
    public static Comparator<Activeprojects> projectsComparator = new Comparator<Activeprojects>() {
        @Override
        public int compare(Activeprojects project1, Activeprojects project2) {

            String project1_title = project1.getTitle().toUpperCase(),
                    project2_title = project2.getTitle().toUpperCase();
            return project1_title.compareTo(project2_title);
        }
    } ;


}
