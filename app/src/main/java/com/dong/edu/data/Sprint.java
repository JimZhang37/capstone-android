package com.dong.edu.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Sprint {
    private String mSprintName;
    private String mSprintTime;
    private long mStartDate;
    private long mEndDate;


    public Sprint() {
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public void setmStartDate(long mStartDate) {
        this.mStartDate = mStartDate;
    }

    public void setmEndDate(long mEndDate) {
        this.mEndDate = mEndDate;
    }

    public String getSprintName() {
        return mSprintName;
    }

    public String getSprintTime() {
        return mSprintTime;
    }

    public long getmStartDate() {
        return mStartDate;
    }

    public long getmEndDate() {
        return mEndDate;
    }

    public void setSprintName(String mSprintName) {
        this.mSprintName = mSprintName;
    }

    public void setSprintTime(String mSprintTime) {
        this.mSprintTime = mSprintTime;
    }

    public String getStartedDateString() {
        return dateFormat.format(mStartDate);
    }

    public String getEndedDateString() {
        return dateFormat.format(mEndDate);
    }

    public String getMessage() {

        long difference = mEndDate - mStartDate;
        long days = difference / (1000 * 60 * 60 * 24);
        days += 1;


        Calendar now = Calendar.getInstance();

        long diffUtilNow = now.getTime().getTime() - mStartDate;
        long daysUtiNow = diffUtilNow / (1000 * 60 * 60 * 24);
        daysUtiNow += 1;
        String showDiff = "The " + String.valueOf(daysUtiNow) + " Day in " + String.valueOf(days) + " Days";
        return showDiff;
    }

    public String getStatus() {
        long now = Calendar.getInstance().getTime().getTime();
        if (now > mEndDate) return "finished!";
        if (now < mStartDate) return "not started yet!";
        return "in progress!";
    }

    public String getDayNumber(){
        if(getStatus() != "in progress!"){
            return null;
        }
        Calendar now = Calendar.getInstance();

        long diffUtilNow = now.getTime().getTime() - mStartDate;
        long daysUtiNow = diffUtilNow / (1000 * 60 * 60 * 24);
        daysUtiNow += 1;
        return String.valueOf(daysUtiNow);
    }

}
