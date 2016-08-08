package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by TMiller on 7/28/2016.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    // constructor.
    private CrimeLab(Context context) {
        mContext = context;
        mCrimes = new ArrayList<>();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    // public methods
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }

    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public void deleteCrime(Crime crime) {
        for(int i=0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crime.getId())) {
                mCrimes.remove(i);
                break;
            }
        }
    }
}
