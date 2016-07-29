package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by TMiller on 7/29/2016.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    public void makingAChange() {
        // do nothing.
    }

}
