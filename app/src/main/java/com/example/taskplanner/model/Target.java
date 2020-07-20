package com.example.taskplanner.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import static com.example.taskplanner.ui.CreateNewTaskFragment.StringToCalendar;

public class Target {

    private String name;
    private String note;
    private String day;
    private ArrayList<Step> steps;
    private double progress;

    public Target(String name, String day) {
        this.name = name;
        this.day = day;
        this.note = "";
        this.steps =  new ArrayList<>();
    }

    public Target(String name, String day, ArrayList<Step> steps) {
        this.name = name;
        this.day = day;
        this.steps = new ArrayList<>(steps);
        this.note = "";
    }

    public Target(String name, String note, String day) {
        this.name = name;
        this.note = note;
        this.day = day;
        this.steps = new ArrayList<>();
    }

    public Target(String name, String note, String day, ArrayList<Step> steps) {
        this.name = name;
        this.note = note;
        this.day = day;
        this.steps = new ArrayList<>(steps);
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = new ArrayList<> (steps);
    }



    // sorting by target deadline
    public static Comparator<Target> targetsComparator = new Comparator<Target>() {
        @Override
        public int compare(Target target1, Target target2) {

            Calendar target1_deadline = (Calendar) StringToCalendar(target1.getDay()).clone(),
                    target2_deadline = (Calendar) StringToCalendar(target2.getDay()).clone();
            return target1_deadline.compareTo(target2_deadline);
        }
    } ;



    public static class Step{
        private String name ;
        private String description;
        private boolean check;

        public Step(String name, String description) {
            this.name = name;
            this.description = description;
            this.check = false;
        }

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
