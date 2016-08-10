package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by TMiller on 7/27/2016.
 */
public class Crime {

    private UUID mId;
    private String mTitle;
    private int mSuspectId;
    private String mSuspect;
    private String mSuspectPhoneNumber;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    // Getters and Setters.
    public UUID getId() { return mId; }

    public String getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }

    public boolean isSolved() { return mSolved; }
    public void setSolved(boolean solved) { mSolved = solved; }

    public void setDate(Date date) { mDate = date; }
    public Date getDate() { return mDate; }

    public void setSuspect(String suspect) { mSuspect = suspect; }
    public String getSuspect() {
        if (mSuspect == null) {
            return "";
        }
        return mSuspect;
    }

    public void setSuspectId(int id) { mSuspectId = id; }
    public int getSuspectId() { return mSuspectId; }

    public void setSuspectPhoneNumber(String phoneNumber) { mSuspectPhoneNumber = phoneNumber; }
    public String getSuspectPhoneNumber() {
        if (mSuspectPhoneNumber == null) {
            return "";
        }
        return mSuspectPhoneNumber;
    }
}
