package com.phoenix.music_application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

//class to display timer dialog fragment in settings activity
public class sleepTimerDialogFragment extends DialogFragment {
    public boolean isBound = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final String[] times = Objects.requireNonNull(getActivity())
                .getResources()
                .getStringArray(R.array.sleepTimes);    //assigning array from resources
        //strings.xml to this array

        final int[] timesInInteger = Objects.requireNonNull(getActivity())
                .getResources()
                .getIntArray(R.array.timesInInt);


        //Creating AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose sleep timer");

        //Setting the array in resources as the list of items in dialog box
        builder.setItems(times, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "set timer to " + times[which], Toast.LENGTH_LONG).show();

                Intent i = new Intent(getContext(), MediaPlayerService.class);
                i.putExtra("sleepTimerDuration", timesInInteger[which]);
                getContext().bindService(i, connection, Context.BIND_AUTO_CREATE);

            }
        });


        return builder.create();            //returns DialogBox
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            MediaPlayerService localService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}
