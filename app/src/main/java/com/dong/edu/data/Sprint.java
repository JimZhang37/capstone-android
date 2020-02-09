package com.dong.edu.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Sprint {
    private String mSprintName;
    private String mSprintTime;
    private long mStartDate;
    private long mEndDate;
    private int mStatus;  //1:WIP, 2:finished but not evaluated, 3:evaluated, 4:not started
    private String mEvaluation;

    public Sprint() {
    }

    public int getmStatus() {
        if (mStatus == 3) return mStatus;
        long now = Calendar.getInstance().getTime().getTime();
        if (now > mEndDate) mStatus = 2; //finished, but not evaluated
        if (now < mStartDate) mStatus = 4; //not started
        if (mStartDate < now && now < mEndDate) mStatus = 1; //work in progress

        return mStatus;
    }

    public String getEvaluation() {
        return mEvaluation;
    }

    public void setEvaluation(String mEvaluation) {
        this.mEvaluation = mEvaluation;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
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

    public String startedDateString() {
        return dateFormat.format(mStartDate);
    }

    public String endedDateString() {
        return dateFormat.format(mEndDate);
    }

    public String message() {

        int i = getmStatus();
        switch (i) {
            case 1:
                long difference = mEndDate - mStartDate;
                long days = difference / (1000 * 60 * 60 * 24);
                Calendar now = Calendar.getInstance();

                long diffUtilNow = now.getTime().getTime() - mStartDate;
                long daysUtiNow = diffUtilNow / (1000 * 60 * 60 * 24);
                daysUtiNow += 1;
                String showDiff = "The " + String.valueOf(daysUtiNow) + " Day in " + String.valueOf(days) + " Days";
                return showDiff;

            case 2:
                return "Please evaluate your sprint";
            case 3:
                return "Congratulations!";
            case 4:
                return "Be patient! Your sprint is not started";
            default:
                return "Default";

        }

    }

    public String status() {

        int i = getmStatus();
        switch (i) {
            case 1:
                return "1: Work in Progress";
            case 2:
                return "2: To be evaluated";
            case 3:
                return "3: Congratulations!";
            case 4:
                return "4: Be patient! Your sprint is not started";
            default:
                return "0: Default";

        }
    }

    public String dayNumber() {
        if (getmStatus() != 1) {
            return null;
        }
        Calendar now = Calendar.getInstance();

        long diffUtilNow = now.getTime().getTime() - mStartDate;
        long daysUtiNow = diffUtilNow / (1000 * 60 * 60 * 24);
        daysUtiNow += 1;
        return String.valueOf(daysUtiNow);
    }

    public void initSprint(long startDate, long endDate) {
        mStartDate = startDate;
        mEndDate = endDate;
        long now = Calendar.getInstance().getTime().getTime();

        //3 is reserved for evaluated
        if (now > mEndDate) mStatus = 2; //finished, but not evaluated
        if (now < mStartDate) mStatus = 4; //not started
        if (mStartDate < now && now < mEndDate) mStatus = 1; //work in progress
    }

}
