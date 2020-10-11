package com.phoenix.music_application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

//class to display timer dialog fragment in settings activity
public class sleepTimerDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final String[] times = Objects.requireNonNull(getActivity())
                .getResources()
                .getStringArray(R.array.sleepTimes);    //assigning array from resources
        //strings.xml to this array


        //Creating AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose sleep timer");

        //Setting the array in resources as the list of items in dialog box
        builder.setItems(times, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), times[which], Toast.LENGTH_LONG).show();
            }
        });


        return builder.create();            //returns DialogBox
    }
}
