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
        intent.putExtra(DatePickerFragmentActivity.EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        DatePickerFragmentActivity fragment = new DatePickerFragmentActivity();
        Bundle args = new Bundle();

        if (getIntent().hasExtra(DatePickerFragmentActivity.EXTRA_DATE)) {
            args.putSerializable(DatePickerFragmentActivity.EXTRA_DATE, getIntent().getSerializableExtra(DatePickerFragmentActivity.EXTRA_DATE));
        }
        else {
            args.putSerializable(DatePickerFragmentActivity.EXTRA_DATE, new Date());
        }

        fragment.setArguments(args);
        return fragment;
    }

}
