package com.bignerdranch.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

/**
 * Created by TMiller on 8/8/2016.
 */
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString (getColumnIndex(CrimeDbSchema.Cols.UUID));
        String tite       = getString (getColumnIndex(CrimeDbSchema.Cols.TITLE));
        long date         = getLong   (getColumnIndex(CrimeDbSchema.Cols.DATE));
        int isSolved      = getInt    (getColumnIndex(CrimeDbSchema.Cols.SOLVED));
        String suspect    = getString (getColumnIndex(CrimeDbSchema.Cols.SUSPECT));
        int suspectId     = getInt    (getColumnIndex(CrimeDbSchema.Cols.SUSPECT_ID));
        String suspectNum = getString (getColumnIndex(CrimeDbSchema.Cols.SUSPECT_NUM));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle              ( tite );
        crime.setDate               ( new Date(date) );
        crime.setSolved             ((isSolved == 1 ? true : false));
        crime.setSuspect            ( suspect );
        crime.setSuspectPhoneNumber (suspectNum);
        crime.setSuspectId          (suspectId);

        return crime;
    }

}
