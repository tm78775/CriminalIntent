package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by TMiller on 7/27/2016.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID      = "crime_id";
    private static final String DIALOG_DATE       = "DialogDate";
    private static final String DIALOG_TIME       = "DialogTime";
    private static final String DIALOG_VIEW_PHOTO = "DialogViewPhoto";
    private static final int REQUEST_DATE       = 0;
    private static final int REQUEST_TIME       = 1;
    private static final int REQUEST_CONTACT    = 2;
    private static final int REQUEST_PHOTO      = 3;
    private static final int REQUEST_VIEW_PHOTO = 4;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;


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
        if (mCrime != null) {
            mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        }
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
        mTitleField =        (EditText)    v.findViewById(R.id.crime_title);
        mDateButton =        (Button)      v.findViewById(R.id.crime_date);
        mTimeButton =        (Button)      v.findViewById(R.id.crime_time);
        mSolvedCheckBox =    (CheckBox)    v.findViewById(R.id.crime_solved);
        mReportButton =      (Button)      v.findViewById(R.id.crime_report);
        mCallSuspectButton = (Button)      v.findViewById(R.id.phone_suspect);
        mPhotoButton =       (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView =         (ImageView)   v.findViewById(R.id.crime_photo);

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
                // Launch a new activity to contain the fragment.
                Intent intent = DatePickerActivity.newInstance(getActivity(), mCrime.getDate());
                startActivityForResult(intent, REQUEST_DATE);
            }
        });

        // setup the time button and dialog fragment.
        mTimeButton.setText(mCrime.getDate().getHours() + ":" + mCrime.getDate().getMinutes());
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

        // PackageManager is required to determine if the device is capable of performing certain Implicit Intents.
        PackageManager packageManager = getActivity().getPackageManager();

        // setup the suspect button
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        if (!mCrime.getSuspect().isEmpty()) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

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
                String number = "tel:" + Uri.parse(mCrime.getSuspectPhoneNumber());
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(number));
                startActivity(intent);
            }
        });

        if (!mCrime.getSuspectPhoneNumber().isEmpty()) {
            mCallSuspectButton.setEnabled(true);
        }
        else {
            mCallSuspectButton.setEnabled(false);
        }

        // setup the mPhotoButton.
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        // setup the PhotoView.
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                CrimePhotoViewerFragment photoDialog = CrimePhotoViewerFragment.newInstance(CrimeLab.get(getActivity()).getPhotoFile(mCrime));
                photoDialog.setTargetFragment(CrimeFragment.this, REQUEST_VIEW_PHOTO);
                photoDialog.show(fm, DIALOG_VIEW_PHOTO);
            }
        });
        updatePhotoView();



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
                mTimeButton.setText(date.getHours() + ":" + date.getMinutes());
                break;
            case REQUEST_CONTACT:
                if (data == null) {
                    return;
                }

                Uri contactUri = data.getData();

                // specify which fields you want to query to return values for.
                String[] queryFields = new String[] {
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


                    // Query to get the suspect's number to call.
                    Cursor cursorPhone = getActivity()
                            .getContentResolver()
                            .query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER},
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? AND " +
                                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                new String[] { Integer.toString(suspectId) },
                                null
                            );

                    try {
                        cursorPhone.moveToFirst();
                        if (cursorPhone.getCount() == 0) {
                            return;
                        }

                        String suspectNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mCrime.setSuspectPhoneNumber(suspectNumber);

                        if (mCrime.getSuspectPhoneNumber().length() > 6) {
                            mCallSuspectButton.setEnabled(true);
                        }
                    }
                    finally {
                        cursorPhone.close();
                    }

                }
                finally {
                    cursor.close();
                }
                break;
            case REQUEST_PHOTO:
                updatePhotoView();
                break;
        }

    }

    // Update the button texts
    private void updateDate(CharSequence format) { mDateButton.setText(format); }

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

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
