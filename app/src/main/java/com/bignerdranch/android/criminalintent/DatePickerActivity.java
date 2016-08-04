package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by Timothy on 8/3/16.
 */
public class DatePickerActivity extends SingleFragmentActivity {

    public static Intent newInstance(Context context, Date date) {
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(DatePickerFragment.EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();

        if (getIntent().hasExtra(DatePickerFragment.EXTRA_DATE)) {
            args.putSerializable(DatePickerFragment.EXTRA_DATE, getIntent().getSerializableExtra(DatePickerFragment.EXTRA_DATE));
        }
        else {
            args.putSerializable(DatePickerFragment.EXTRA_DATE, new Date());
        }

        fragment.setArguments(args);
        return fragment;
    }

}
