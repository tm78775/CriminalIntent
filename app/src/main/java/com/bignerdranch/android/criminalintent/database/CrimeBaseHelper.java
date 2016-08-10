package com.bignerdranch.android.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.Cols;


/**
 * Created by Timothy on 8/5/16.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME +
            "(" +
              Cols.UUID        + ", " +
              Cols.TITLE       + ", " +
              Cols.DATE        + ", " +
              Cols.SOLVED      + ", " +
              Cols.SUSPECT_ID  + ", " +
              Cols.SUSPECT_NUM + ", " +
              Cols.SUSPECT     +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
