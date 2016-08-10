package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

/**
 * Created by TMiller on 7/27/2016.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;


    /*
     *  Return a new instance of itself.
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /*
     *  Overridden (Inherited) methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the view.
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        // get references to widgets in the view that was just inflated.
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mCallSuspectButton = (Button) v.findViewById(R.id.phone_suspect);

        // assign fields and checkboxes.
        mTitleField.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        // add listeners where appropriate.
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // do nothing.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mCrime.setTitle(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing.
            }
        });

        // setup the date button and dialog fragment.
        DateFormat df = new DateFormat();
        updateDate(df.format("MMMM dd, yyyy", mCrime.getDate()));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // LAUNCH A NEW ACTIVITY TO CONTAIN THE FRAGMENT.
                Intent intent = DatePickerActivity.newInstance(getActivity(), mCrime.getDate());
                startActivityForResult(intent, REQUEST_DATE);
            }
        });

        // setup the time button and dialog fragment.
        updateTime(mCrime.getDate().getHours() + ":" + mCrime.getDate().getMinutes());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        // setup the "solved" check box.
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        // setup the report button
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.crime_report_subject);
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        // setup the suspect button
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        // setup the call suspect button.
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // todo: This is where you left off.
                // Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_crime_pager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_delete_item:
                deleteCrime();
                Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                getActivity().finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate(mCrime.getDate().toString());
                break;
            case REQUEST_TIME:
                Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                date = mCrime.getDate();
                date.setMinutes(time.getMinutes());
                date.setHours(time.getHours());
                updateTime(date.getHours() + ":" + date.getMinutes());
                break;
            case REQUEST_CONTACT:
                if (data == null) {
                    return;
                }

                Uri contactUri = data.getData();

                // specify which fields you want to query to return values for.
                String[] queryFields = new String[] {
                        // ContactsContract.Contacts.DISPLAY_NAME,
                        // ContactsContract.Contacts.HAS_PHONE_NUMBER
                        ContactsContract.Contacts._ID
                        , ContactsContract.Contacts.DISPLAY_NAME
                };

                // perform query with the contentresolver.
                Cursor cursor = getActivity()
                        .getContentResolver()
                        .query(contactUri, queryFields, null, null, null);

                try {
                    // double check that you actually got results.
                    if (cursor.getCount() == 0) {
                        return;
                    }

                    // Pull out the first column of the first row of data.
                    cursor.moveToFirst();
                    int suspectId = cursor.getInt(0);
                    String suspect = cursor.getString(1);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);

                    Cursor phoneCursor = getActivity()
                            .getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                , null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? "
                                , new String[] { Integer.toString(suspectId) }
                                , null
                            );
                        try {
                            if (phoneCursor.getCount() == 0) {
                                return;
                            }

                            phoneCursor.moveToFirst();
                            phoneCursor.getString(0);
                        }
                        finally {
                            phoneCursor.close();
                        }


                }
                finally {
                    cursor.close();
                }

                break;
        }

    }

    // Update the button texts
    private void updateDate(CharSequence format) { mDateButton.setText(format); }
    private void updateTime(CharSequence format) { mTimeButton.setText(format); }

    private void deleteCrime() {
        CrimeLab.get(getActivity()).deleteCrime(mCrime);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        }
        else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
