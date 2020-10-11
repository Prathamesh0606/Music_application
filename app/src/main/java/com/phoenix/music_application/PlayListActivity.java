package com.phoenix.music_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    ListView listView;
    String[] songNames;
    File file;

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
        final ArrayList<File> songs = readSongs(file);

        songNames = new String[songs.size()];

        for (int i = 0; i < songs.size(); i++) {
            songNames[i] = songs.get(i).getName().replace(".mp3", "");                //did this so that song doesnt show .mp3 in its name
        }

        //arrayadapter to
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.songs_layout, R.id.songNameText, songNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(PlayListActivity.this, MediaPlayerService.class);
//                intent.putExtra("song", songs.get(position).getAbsolutePath());
//                startService(intent);
                //intent.putExtra("songList", songs);
                Intent i = new Intent(PlayListActivity.this, MainActivity.class);
                i.putExtra("songIndex", position);
                i.putExtra("songList", songs);
                startActivity(i);

            }
        });

    }

    private ArrayList<File> readSongs(File root) {
        ArrayList<File> songsList = new ArrayList<>();
        File[] files = root.listFiles();

        assert files != null;
        for (File file : files) {

            //if the file is a directory, scan for mp3s in it recursively
            //else add the mp3 to songsList
            if (file.isDirectory()) {
                songsList.addAll(readSongs(file));


            } else {
                if (file.getName().endsWith(".mp3")) {
                    songsList.add(file);
//                    MediaMetadataRetriever metaRetriver;
//                    metaRetriver = new MediaMetadataRetriever();
//                    Uri uri = Uri.fromFile(file);
//                    metaRetriver.setDataSource(this,uri);
//                    String a = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                    String b = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//
//                        Log.i("Title :", a);
//                        Log.i("duration :", b);


                }
            }
        }
        //adding the list to sharedpreferences
        PreferencesConfig.writeInPref(this, songsList);
        return songsList;
    }
}