package com.dong.edu.data;

import android.net.Uri;

public class Day {

    private String mPictureUri;
    private int mDayNumber;
    private int mTotalDays;
    private String mDescription;

    public Day() {
    }

    public String getPictureUri() {
        return mPictureUri;
    }

    public int getmDayNumber() {
        return mDayNumber;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getmTotalDays() {
        return mTotalDays;
    }

    public void setPictureUri(String mPictureUri) {
        this.mPictureUri = mPictureUri;
    }

    public void setmDayNumber(int mDayNumber) {
        this.mDayNumber = mDayNumber;
    }

    public void setmTotalDays(int mTotalDays) {
        this.mTotalDays = mTotalDays;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}
