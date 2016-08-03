package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by TMiller on 8/3/2016.
 */
public class TimePickerFragment extends AppCompatDialogFragment {

    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
    private static final String ARG_TIME = "time.argument";
    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Calendar calendar) {

        // on retrieving a new instance, we set the timepicker widget to the time currently set in the Crime object.
        Date date = calendar.getTime();

        // set the time in the fragment arguments bundle.
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(date.getHours());
        mTimePicker.setCurrentMinute(date.getMinutes());

        // todo: this is where you left off.
        return null;
    }

}
