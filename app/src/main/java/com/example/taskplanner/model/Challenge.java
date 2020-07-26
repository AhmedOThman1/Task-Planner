package com.example.taskplanner.model;

import java.util.Calendar;
import java.util.Comparator;

import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;

public class Challenge {
    private String name;
    private String startDate;
    private String days_to_without;
    private Integer duration;

    public Challenge(String name, String startDate, String days_to_without, Integer duration) {
        this.name = name;
        this.startDate = startDate;
        this.days_to_without = days_to_without;
        this.duration = duration;
    }

    public String getDays_to_without() {
        return days_to_without;
    }

    public void setDays_to_without(String days_to_without) {
        this.days_to_without = days_to_without;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    // sorting by challenge start time
    public static Comparator<Challenge> challengesComparator = new Comparator<Challenge>() {
        @Override
        public int compare(Challenge challenge1 , Challenge challenge2) {

            Calendar challenge1_start_time = (Calendar) StringToCalendar(challenge1.getStartDate()).clone(),
                    challenge2_start_time = (Calendar) StringToCalendar(challenge2.getStartDate()).clone();
            return challenge1_start_time.compareTo(challenge2_start_time);
        }
    };


}
