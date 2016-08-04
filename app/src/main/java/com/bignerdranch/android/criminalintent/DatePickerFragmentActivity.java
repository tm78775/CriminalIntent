package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Date;

/**
 * Created by Timothy on 8/3/16.
 */
public class DatePickerFragmentActivity extends Fragment {

    public static final String EXTRA_DATE = "__extra_date";
    private DatePicker mDatePicker;
    private Button mOkButton;
    private Date mInitDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInitDate = (Date) getArguments().getSerializable(EXTRA_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the view.
        View v = inflater.inflate(R.layout.dialog_date, container, false);

        int year = mInitDate.getYear() + 1900;
        int month = mInitDate.getMonth();
        int day = mInitDate.getDate();

        // configure the datepicker.
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        // configure the ok button.
        mOkButton = (Button) v.findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            }
        });

        return v;
    }

    private void setResult(int resultCode, int year, int month, int dayOfMonth) {
        Date returnDate = new Date(year, month, dayOfMonth);
        Intent data = new Intent();
        data.putExtra(EXTRA_DATE, returnDate);

        getActivity().setResult(resultCode, data);
        getActivity().finish();
    }

}
