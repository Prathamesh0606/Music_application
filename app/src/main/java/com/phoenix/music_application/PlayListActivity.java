package com.phoenix.music_application;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    ListView listView;
    String[] songNames;
    File file;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        listView = findViewById(R.id.playListView);

        //folderpath is static variable inside settings screen activity, which gives path of folder selected to serach for songs
        if (settingsActivity.folderPath == null) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());           //generates new file object with /mnt as root directory
        } else {
            file = new File(settingsActivity.folderPath);
        }
        final ArrayList<Audio> songs = scanDeviceForMp3Files();

        songNames = new String[songs.size()];

        for (int i = 0; i < songs.size(); i++) {
            songNames[i] = songs.get(i).getTitle();             //did this so that song doesnt show .mp3 in its name
        }

        //arrayadapter to
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.songs_layout, R.id.songNameText, songNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
                Intent i = new Intent(PlayListActivity.this, MainActivity.class);
                i.putExtra("songIndex", position);
                i.putExtra("songList", songs);
                startActivity(i);

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ArrayList<Audio> scanDeviceForMp3Files() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,

        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        ArrayList<Audio> mp3Files = new ArrayList<>();

        Uri uri;
        Cursor cursor = null;
        try {
            if (settingsActivity.folderPath == null) {
                uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            } else {
                uri = Uri.fromFile(new File(settingsActivity.folderPath));
            }

            cursor = getContentResolver().query(uri, projection, selection, null, sortOrder);
            if (cursor != null) {
                cursor.moveToFirst();


                while (!cursor.isAfterLast()) {
                    Audio a = new Audio();
                    //a.setTitle(cursor.getString(0));
                    a.setArtist(cursor.getString(1));

                    a.setPath(cursor.getString(2));
                    a.setTitle(cursor.getString(0));
                    a.setDuration(cursor.getString(4));
                    mp3Files.add(a);
                    cursor.moveToNext();

                }

            }

            // print to see list of mp3 files

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        PreferencesConfig.writeInPref(this, mp3Files);
        return mp3Files;
    }
}