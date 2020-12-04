package com.phoenix.music_application;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.Objects;


public class settingsActivity extends AppCompatActivity {

    DialogProperties properties;
    FilePickerDialog dialog;
    static String folderPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        properties = new DialogProperties();                            //dialogbox for selecting storage location to scan
        properties.selection_mode = DialogConfigs.SINGLE_MODE;           //setting some preferences for dialog box
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;  //read the documentation if you want on github
        properties.root = new File(DialogConfigs.DEFAULT_DIR + "/" + "sdcard");
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        properties.show_hidden_files = false;
    }

    public void setStorageLoc(View view) {

        //Intent i = new Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //startActivity(i);

        dialog = new FilePickerDialog(settingsActivity.this, properties);
        dialog.setTitle("Select Folder/s");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                Log.i("selected", files[0]);
//                Toast.makeText(this, files[0], Toast.LENGTH_SHORT).show();
                folderPath = files[0];

            }

        });
        dialog.show();
    }

    public void setSleepTimer(View view) { new sleepTimerDialogFragment().show(getSupportFragmentManager(), "fragmentDialog"); }

    public void about(View view) {}


}


