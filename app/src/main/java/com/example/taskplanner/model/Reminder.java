package com.example.taskplanner.model;

import java.util.ArrayList;

public class Reminder {
    private String time;
    private String AM_PM;
    private String date;
    private String title;
    private String type;
    private boolean week;
    private ArrayList<Boolean> b;
    private String until_day;

    public Reminder(String time, String AM_PM, String date, String title, String type) {
        this.time = time;
        this.AM_PM = AM_PM;
        this.date = date;
        this.title = title;
        this.type = type;
        this.week = false;
        this.b = new ArrayList<>();
        this.until_day = "";
    }

    public Reminder(String time, String AM_PM, String date, String title, String type, boolean week, ArrayList<Boolean> b) {
        this.time = time;
        this.AM_PM = AM_PM;
        this.date = date;
        this.title = title;
        this.type = type;
        this.week = week;
        this.b = b;
        this.until_day = "";

    }

    public Reminder(String time, String AM_PM, String date, String title, String type, String until_day) {
        this.time = time;
        this.AM_PM = AM_PM;
        this.date = date;
        this.title = title;
        this.type = type;
        this.week = false;
        this.b = new ArrayList<>();
        this.until_day = until_day;
    }


    public String getUntil_day() {
        return until_day;
    }

    public void setUntil_day(String until_day) {
        this.until_day = until_day;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWeek() {
        return week;
    }

    public void setWeek(boolean week) {
        this.week = week;
    }

    public ArrayList<Boolean> getB() {
        return b;
    }

    public void setB(ArrayList<Boolean> b) {
        this.b = b;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAM_PM() {
        return AM_PM;
    }

    public void setAM_PM(String AM_PM) {
        this.AM_PM = AM_PM;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
