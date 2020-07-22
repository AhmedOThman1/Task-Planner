package com.example.taskplanner.model;

import java.util.Comparator;

public class Tasks {
    private String projectName;
    private String task_name;
    private String start_calendar;
    private String end_calendar;
    private String task_description;
    private String toindone;

    public Tasks() {
        projectName = null;
        task_name = null;
        start_calendar = null;
        end_calendar = null;
        task_description = null;
        toindone = "To Do";
    }

    public Tasks(String projectName, String task_name, String start_calendar, String end_calendar, String task_description) {
        this.projectName = projectName;
        this.task_name = task_name;
        this.start_calendar = start_calendar;
        this.end_calendar = end_calendar;
        this.task_description = task_description;
        this.toindone = "To Do";
    }

    public Tasks(String projectName, String task_name, String start_calendar, String end_calendar, String task_description, String toindone) {
        this.projectName = projectName;
        this.task_name = task_name;
        this.start_calendar = start_calendar;
        this.end_calendar = end_calendar;
        this.task_description = task_description;
        this.toindone = toindone;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getStart_calendar() {
        return start_calendar;
    }

    public void setStart_calendar(String start_calendar) {
        this.start_calendar = start_calendar;
    }

    public String getEnd_calendar() {
        return end_calendar;
    }

    public void setEnd_calendar(String end_calendar) {
        this.end_calendar = end_calendar;
    }

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getToindone() {
        return toindone;
    }

    public void setToindone(String toindone) {
        this.toindone = toindone;
    }

    // sorting by task time
    public static Comparator<Tasks> tasksComparator = (tasks1, tasks2) -> {

        String tasks1Start_calendar = tasks1.getStart_calendar(),
                tasks2Start_calendar = tasks2.getStart_calendar();
        return tasks1Start_calendar.compareTo(tasks2Start_calendar);
    };


}
