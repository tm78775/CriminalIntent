package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by TMiller on 8/10/2016.
 */
public class CrimePhotoViewerFragment extends AppCompatDialogFragment {

    public static final String EXTRA_PHOTO = "com.bignerdranch.android.criminalintent.photoviewer";
    private static final String ARG_PHOTO_DIR = "photo_dir";
    private ImageView mImageView;
    private String mPhotoDir;

    public static CrimePhotoViewerFragment newInstance(File photoDir) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_DIR, photoDir);

        CrimePhotoViewerFragment fragment = new CrimePhotoViewerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        File photoDir = (File) getArguments().getSerializable(ARG_PHOTO_DIR);
        mPhotoDir = photoDir.toString();
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_viewer, null);
        mImageView = (ImageView) v.findViewById(R.id.crime_photo_view);
        ViewTreeObserver observer = v.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.crime_scene)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                    }
                })
                .create();
    }

    private void updatePhotoView() {
        mImageView.setImageBitmap(PictureUtils.getScaledBitmap(mPhotoDir, getActivity()));
    }

}
