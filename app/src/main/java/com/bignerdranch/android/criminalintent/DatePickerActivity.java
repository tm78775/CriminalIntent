package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Timothy on 8/3/16.
 */
public class DatePickerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DatePickerActivityFragment();
    }

}
